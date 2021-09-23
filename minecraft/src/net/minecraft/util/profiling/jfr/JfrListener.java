package net.minecraft.util.profiling.jfr;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import javax.annotation.Nullable;
import net.minecraft.Bootstrap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.util.Supplier;

public class JfrListener {
	private static final Logger LOGGER = LogManager.getLogger();
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

	private static void log(Supplier<String> messageSupplier) {
		if (shouldUseLogger()) {
			LOGGER.info(messageSupplier);
		} else {
			Bootstrap.println(messageSupplier.get());
		}
	}

	private static void warn(Supplier<String> messageSupplier, Throwable throwable) {
		if (shouldUseLogger()) {
			LOGGER.warn(messageSupplier, throwable);
		} else {
			Bootstrap.println(messageSupplier.get());
			throwable.printStackTrace(Bootstrap.SYSOUT);
		}
	}

	private static boolean shouldUseLogger() {
		return LogManager.getContext() instanceof LifeCycle lifeCycle ? !lifeCycle.isStopped() : true;
	}
}
