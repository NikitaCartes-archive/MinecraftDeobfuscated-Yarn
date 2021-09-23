/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiling.jfr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import jdk.jfr.Configuration;
import jdk.jfr.Event;
import jdk.jfr.EventType;
import jdk.jfr.FlightRecorder;
import jdk.jfr.FlightRecorderListener;
import jdk.jfr.Recording;
import jdk.jfr.RecordingState;
import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiling.jfr.Finishable;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import net.minecraft.util.profiling.jfr.InstanceType;
import net.minecraft.util.profiling.jfr.JfrListener;
import net.minecraft.util.profiling.jfr.event.ChunkGenerationEvent;
import net.minecraft.util.profiling.jfr.event.PacketReceivedEvent;
import net.minecraft.util.profiling.jfr.event.PacketSentEvent;
import net.minecraft.util.profiling.jfr.event.ServerTickTimeEvent;
import net.minecraft.util.profiling.jfr.event.WorldLoadFinishedEvent;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class JfrProfiler
implements FlightProfiler {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MINECRAFT = "Minecraft";
    public static final String WORLD_GENERATION = "World Generation";
    public static final String TICKING = "Ticking";
    public static final String NETWORK = "Network";
    private static final List<Class<? extends Event>> EVENTS = List.of(ChunkGenerationEvent.class, WorldLoadFinishedEvent.class, ServerTickTimeEvent.class, PacketReceivedEvent.class, PacketSentEvent.class);
    private static final String CONFIG_PATH = "/flightrecorder-config.jfc";
    private static final DateTimeFormatter DATE_TIME_FORMAT = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd-HHmmss").toFormatter().withZone(ZoneId.systemDefault());
    @Nullable
    Recording currentRecording;
    private long nextSampleTime;

    protected JfrProfiler() {
    }

    @Override
    public void registerEvents() {
        EVENTS.forEach(FlightRecorder::register);
    }

    @Override
    public boolean start(InstanceType instanceType) {
        boolean bl;
        URL uRL = JfrProfiler.class.getResource(CONFIG_PATH);
        if (uRL == null) {
            LOGGER.warn("Could not find default flight recorder config at {}", (Object)CONFIG_PATH);
            return false;
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(uRL.openStream()));
        try {
            bl = this.start(bufferedReader, instanceType);
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

    @Override
    public Path stop() {
        if (this.currentRecording == null) {
            throw new IllegalStateException("Not currently profiling");
        }
        Path path = this.currentRecording.getDestination();
        this.currentRecording.stop();
        return path;
    }

    @Override
    public boolean isProfiling() {
        return this.currentRecording != null;
    }

    @Override
    public boolean isAvailable() {
        return FlightRecorder.isAvailable();
    }

    @Override
    public void onTick(float tickTime) {
        long l;
        if (EventType.getEventType(ServerTickTimeEvent.class).isEnabled() && this.nextSampleTime <= (l = Util.nanoTimeSupplier.getAsLong())) {
            new ServerTickTimeEvent(tickTime).commit();
            this.nextSampleTime = l + TimeUnit.SECONDS.toNanos(1L);
        }
    }

    private boolean start(Reader reader, InstanceType instanceType) {
        if (this.currentRecording != null) {
            LOGGER.warn("Profiling already in progress");
            return false;
        }
        try {
            Configuration configuration = Configuration.create(reader);
            String string = DATE_TIME_FORMAT.format(Instant.now());
            this.currentRecording = Util.make(new Recording(configuration), recording -> {
                EVENTS.forEach(recording::enable);
                recording.setDumpOnExit(true);
                recording.setToDisk(true);
                recording.setName("%s-%s-%s".formatted(instanceType.getName(), SharedConstants.getGameVersion().getName(), string));
            });
            Path path = Paths.get("debug/%s-%s.jfr".formatted(instanceType.getName(), string), new String[0]);
            if (!Files.exists(path.getParent(), new LinkOption[0])) {
                Files.createDirectories(path.getParent(), new FileAttribute[0]);
            }
            this.currentRecording.setDestination(path);
            this.currentRecording.start();
            this.addListener();
        } catch (IOException | ParseException exception) {
            LOGGER.warn("Failed to start jfr profiling", (Throwable)exception);
            return false;
        }
        LOGGER.info("Started flight recorder profiling id({}):name({}) - will dump to {} on exit or stop command", (Object)this.currentRecording.getId(), (Object)this.currentRecording.getName(), (Object)this.currentRecording.getDestination());
        return true;
    }

    private void addListener() {
        FlightRecorder.addListener(new FlightRecorderListener(){
            final JfrListener innerListener = new JfrListener(() -> {
                JfrProfiler.this.currentRecording = null;
            });

            @Override
            public void recordingStateChanged(Recording recording) {
                if (recording != JfrProfiler.this.currentRecording || recording.getState() != RecordingState.STOPPED) {
                    return;
                }
                this.innerListener.stop(recording.getDestination());
                FlightRecorder.removeListener(this);
            }
        });
    }

    @Override
    public void onPacketReceived(Supplier<String> packetNameSupplier, SocketAddress remoteAddress, int bytes) {
        if (EventType.getEventType(PacketReceivedEvent.class).isEnabled()) {
            new PacketReceivedEvent(packetNameSupplier.get(), remoteAddress, bytes).commit();
        }
    }

    @Override
    public void onPacketSent(Supplier<String> packetNameSupplier, SocketAddress remoteAddress, int bytes) {
        if (EventType.getEventType(PacketSentEvent.class).isEnabled()) {
            new PacketReceivedEvent(packetNameSupplier.get(), remoteAddress, bytes).commit();
        }
    }

    @Override
    @Nullable
    public Finishable startWorldLoadProfiling() {
        if (!EventType.getEventType(WorldLoadFinishedEvent.class).isEnabled()) {
            return null;
        }
        WorldLoadFinishedEvent worldLoadFinishedEvent = new WorldLoadFinishedEvent();
        worldLoadFinishedEvent.begin();
        return worldLoadFinishedEvent::commit;
    }

    @Override
    @Nullable
    public Finishable startChunkGenerationProfiling(ChunkPos chunkPos, RegistryKey<World> world, String targetStatus) {
        if (!EventType.getEventType(ChunkGenerationEvent.class).isEnabled()) {
            return null;
        }
        ChunkGenerationEvent chunkGenerationEvent = new ChunkGenerationEvent(chunkPos, world, targetStatus);
        chunkGenerationEvent.begin();
        return chunkGenerationEvent::commit;
    }
}

