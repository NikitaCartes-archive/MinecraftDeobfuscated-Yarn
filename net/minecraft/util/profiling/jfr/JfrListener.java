/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiling.jfr;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import net.minecraft.Bootstrap;
import net.minecraft.util.profiling.jfr.JfrProfile;
import net.minecraft.util.profiling.jfr.JfrProfileRecorder;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.util.Supplier;
import org.jetbrains.annotations.Nullable;

public class JfrListener {
    private static final Logger LOGGER = LogManager.getLogger();
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

    private static void log(Supplier<String> messageSupplier) {
        if (JfrListener.shouldUseLogger()) {
            LOGGER.info(messageSupplier);
        } else {
            Bootstrap.println(messageSupplier.get());
        }
    }

    private static void warn(Supplier<String> messageSupplier, Throwable throwable) {
        if (JfrListener.shouldUseLogger()) {
            LOGGER.warn(messageSupplier, throwable);
        } else {
            Bootstrap.println(messageSupplier.get());
            throwable.printStackTrace(Bootstrap.SYSOUT);
        }
    }

    private static boolean shouldUseLogger() {
        LoggerContext loggerContext = LogManager.getContext();
        LoggerContext loggerContext2 = loggerContext;
        if (loggerContext2 instanceof LifeCycle) {
            LifeCycle lifeCycle = (LifeCycle)((Object)loggerContext2);
            return !lifeCycle.isStopped();
        }
        return true;
    }
}

