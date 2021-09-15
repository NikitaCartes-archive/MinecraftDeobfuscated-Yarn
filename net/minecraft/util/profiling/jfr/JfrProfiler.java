/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiling.jfr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import jdk.jfr.Configuration;
import jdk.jfr.Event;
import jdk.jfr.FlightRecorder;
import jdk.jfr.FlightRecorderListener;
import jdk.jfr.Recording;
import jdk.jfr.RecordingState;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.jfr.JfrProfile;
import net.minecraft.util.profiling.jfr.JfrProfileRecorder;
import net.minecraft.util.profiling.jfr.event.network.PacketReceivedEvent;
import net.minecraft.util.profiling.jfr.event.network.PacketSentEvent;
import net.minecraft.util.profiling.jfr.event.ticking.ServerTickTimeEvent;
import net.minecraft.util.profiling.jfr.event.worldgen.ChunkGenerationEvent;
import net.minecraft.util.profiling.jfr.event.worldgen.WorldLoadFinishedEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.util.Supplier;
import org.jetbrains.annotations.Nullable;

public class JfrProfiler {
    static final Logger LOGGER = LogManager.getLogger();
    public static final String MINECRAFT = "Minecraft";
    public static final String WORLD_GENERATION = "World Generation";
    public static final String TICKING = "Ticking";
    public static final String NETWORK = "Network";
    public static final List<Class<? extends Event>> EVENTS = List.of(ChunkGenerationEvent.class, WorldLoadFinishedEvent.class, ServerTickTimeEvent.class, PacketReceivedEvent.class, PacketSentEvent.class);
    private static final String CONFIG_PATH = "/flightrecorder-config.jfc";
    private static final DateTimeFormatter DATE_TIME_FORMAT = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd-HHmm").toFormatter().withZone(ZoneId.systemDefault());
    @Nullable
    private static Recording currentRecording;

    private JfrProfiler() {
    }

    private static boolean start(Reader reader, InstanceType instanceType) {
        if (!FlightRecorder.isAvailable()) {
            LOGGER.warn("Flight Recorder not available!");
            return false;
        }
        if (currentRecording != null) {
            LOGGER.warn("Profiling already in progress");
            return false;
        }
        try {
            Configuration configuration = Configuration.create(reader);
            String string = DATE_TIME_FORMAT.format(Instant.now());
            currentRecording = Util.make(new Recording(configuration), recording -> {
                EVENTS.forEach(recording::enable);
                recording.setDumpOnExit(true);
                recording.setToDisk(true);
                recording.setName("%s-%s-%s".formatted(instanceType.getName(), SharedConstants.getGameVersion().getName(), string));
            });
            Path path = Paths.get("debug/%s-%s.jfr".formatted(instanceType.getName(), string), new String[0]);
            if (!Files.exists(path.getParent(), new LinkOption[0])) {
                Files.createDirectories(path.getParent(), new FileAttribute[0]);
            }
            currentRecording.setDestination(path);
            currentRecording.start();
            FlightRecorder.addListener(new Listener(currentRecording, () -> {
                currentRecording = null;
            }));
        } catch (IOException | ParseException exception) {
            LOGGER.warn("Failed to start jfr profiling", (Throwable)exception);
            return false;
        }
        LOGGER.info("Started flight recorder profiling id({}):name({}) - will dump to {} on exit or stop command", (Object)currentRecording.getId(), (Object)currentRecording.getName(), (Object)currentRecording.getDestination());
        return true;
    }

    public static boolean start(InstanceType instanceType) {
        boolean bl;
        URL uRL = JfrProfiler.class.getResource(CONFIG_PATH);
        if (uRL == null) {
            LOGGER.warn("Could not find default flight recorder config at {}", (Object)CONFIG_PATH);
            return false;
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(uRL.openStream()));
        try {
            bl = JfrProfiler.start(bufferedReader, instanceType);
        } catch (Throwable throwable) {
            try {
                try {
                    bufferedReader.close();
                } catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            } catch (IOException iOException) {
                LOGGER.warn("Failed to start flight recorder using configuration at {}", (Object)uRL, (Object)iOException);
                return false;
            }
        }
        bufferedReader.close();
        return bl;
    }

    public static Path stop() {
        if (currentRecording == null) {
            throw new IllegalStateException("Not currently profiling");
        }
        Path path = currentRecording.getDestination();
        currentRecording.stop();
        return path;
    }

    public static boolean isProfiling() {
        return currentRecording != null;
    }

    public static enum InstanceType {
        CLIENT("client"),
        SERVER("server");

        private final String name;

        private InstanceType(String name) {
            this.name = name;
        }

        public static InstanceType get(MinecraftServer server) {
            return server.isDedicated() ? SERVER : CLIENT;
        }

        String getName() {
            return this.name;
        }
    }

    static class Listener
    implements FlightRecorderListener {
        private final Recording recording;
        private final Runnable stopCallback;

        Listener(Recording recording, Runnable stopCallback) {
            this.recording = recording;
            this.stopCallback = stopCallback;
        }

        @Override
        public void recordingStateChanged(Recording recording) {
            JfrProfile jfrProfile;
            if (recording != this.recording || this.recording.getState() != RecordingState.STOPPED || recording.getDestination() == null) {
                return;
            }
            FlightRecorder.removeListener(this);
            this.stopCallback.run();
            Path path = recording.getDestination();
            Listener.log(() -> "Dumped flight recorder profiling to " + path);
            try {
                jfrProfile = JfrProfileRecorder.readProfile(path);
            } catch (Throwable throwable) {
                Listener.warn(() -> "Failed to parse JFR recording", throwable);
                return;
            }
            try {
                Listener.log(jfrProfile::toJson);
                Path path2 = path.resolveSibling("jfr-report-" + StringUtils.substringBefore(path.getFileName().toString(), ".jfr") + ".json");
                Files.writeString(path2, (CharSequence)jfrProfile.toJson(), StandardOpenOption.CREATE);
                Listener.log(() -> "Dumped recording summary to " + path2);
            } catch (Throwable throwable) {
                Listener.warn(() -> "Failed to output JFR report", throwable);
            }
        }

        private static void log(Supplier<String> messageSupplier) {
            if (Listener.shouldUseLogger()) {
                LOGGER.info(messageSupplier);
            } else {
                Bootstrap.println(messageSupplier.get());
            }
        }

        private static void warn(Supplier<String> messageSupplier, Throwable throwable) {
            if (Listener.shouldUseLogger()) {
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
}

