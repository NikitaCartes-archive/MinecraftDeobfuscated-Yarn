/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiling.jfr;

import com.mojang.logging.LogUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Supplier;
import net.minecraft.Bootstrap;
import net.minecraft.util.profiling.jfr.JfrProfile;
import net.minecraft.util.profiling.jfr.JfrProfileRecorder;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class JfrListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Runnable stopCallback;

    protected JfrListener(Runnable stopCallback) {
        this.stopCallback = stopCallback;
    }

    public void stop(@Nullable Path dumpPath) {
        JfrProfile jfrProfile;
        if (dumpPath == null) {
            return;
        }
        this.stopCallback.run();
        JfrListener.log(() -> "Dumped flight recorder profiling to " + dumpPath);
        try {
            jfrProfile = JfrProfileRecorder.readProfile(dumpPath);
        } catch (Throwable throwable) {
            JfrListener.warn(() -> "Failed to parse JFR recording", throwable);
            return;
        }
        try {
            JfrListener.log(jfrProfile::toJson);
            Path path = dumpPath.resolveSibling("jfr-report-" + StringUtils.substringBefore(dumpPath.getFileName().toString(), ".jfr") + ".json");
            Files.writeString(path, (CharSequence)jfrProfile.toJson(), StandardOpenOption.CREATE);
            JfrListener.log(() -> "Dumped recording summary to " + path);
        } catch (Throwable throwable) {
            JfrListener.warn(() -> "Failed to output JFR report", throwable);
        }
    }

    private static void log(Supplier<String> logSupplier) {
        if (LogUtils.isLoggerActive()) {
            LOGGER.info(logSupplier.get());
        } else {
            Bootstrap.println(logSupplier.get());
        }
    }

    private static void warn(Supplier<String> logSupplier, Throwable throwable) {
        if (LogUtils.isLoggerActive()) {
            LOGGER.warn(logSupplier.get(), throwable);
        } else {
            Bootstrap.println(logSupplier.get());
            throwable.printStackTrace(Bootstrap.SYSOUT);
        }
    }
}

