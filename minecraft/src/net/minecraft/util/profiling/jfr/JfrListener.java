package net.minecraft.util.profiling.jfr;

import com.mojang.logging.LogUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Bootstrap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class JfrListener {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Runnable stopCallback;

	protected JfrListener(Runnable stopCallback) {
		this.stopCallback = stopCallback;
	}

	public void stop(@Nullable Path dumpPath) {
		if (dumpPath != null) {
			this.stopCallback.run();
			log(() -> "Dumped flight recorder profiling to " + dumpPath);

			JfrProfile jfrProfile;
			try {
				jfrProfile = JfrProfileRecorder.readProfile(dumpPath);
			} catch (Throwable var5) {
				warn(() -> "Failed to parse JFR recording", var5);
				return;
			}

			try {
				log(jfrProfile::toJson);
				Path path = dumpPath.resolveSibling("jfr-report-" + StringUtils.substringBefore(dumpPath.getFileName().toString(), ".jfr") + ".json");
				Files.writeString(path, jfrProfile.toJson(), StandardOpenOption.CREATE);
				log(() -> "Dumped recording summary to " + path);
			} catch (Throwable var4) {
				warn(() -> "Failed to output JFR report", var4);
			}
		}
	}

	private static void log(Supplier<String> logSupplier) {
		if (LogUtils.isLoggerActive()) {
			LOGGER.info((String)logSupplier.get());
		} else {
			Bootstrap.println((String)logSupplier.get());
		}
	}

	private static void warn(Supplier<String> logSupplier, Throwable throwable) {
		if (LogUtils.isLoggerActive()) {
			LOGGER.warn((String)logSupplier.get(), throwable);
		} else {
			Bootstrap.println((String)logSupplier.get());
			throwable.printStackTrace(Bootstrap.SYSOUT);
		}
	}
}
