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
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.annotation.Nullable;
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
import net.minecraft.util.profiling.jfr.event.ChunkGenerationEvent;
import net.minecraft.util.profiling.jfr.event.PacketReceivedEvent;
import net.minecraft.util.profiling.jfr.event.PacketSentEvent;
import net.minecraft.util.profiling.jfr.event.ServerTickTimeEvent;
import net.minecraft.util.profiling.jfr.event.WorldLoadFinishedEvent;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JfrProfiler implements FlightProfiler {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String MINECRAFT = "Minecraft";
	public static final String WORLD_GENERATION = "World Generation";
	public static final String TICKING = "Ticking";
	public static final String NETWORK = "Network";
	private static final List<Class<? extends Event>> EVENTS = List.of(
		ChunkGenerationEvent.class, WorldLoadFinishedEvent.class, ServerTickTimeEvent.class, PacketReceivedEvent.class, PacketSentEvent.class
	);
	private static final String CONFIG_PATH = "/flightrecorder-config.jfc";
	private static final DateTimeFormatter DATE_TIME_FORMAT = new DateTimeFormatterBuilder()
		.appendPattern("yyyy-MM-dd-HHmmss")
		.toFormatter()
		.withZone(ZoneId.systemDefault());
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
		URL uRL = JfrProfiler.class.getResource("/flightrecorder-config.jfc");
		if (uRL == null) {
			LOGGER.warn("Could not find default flight recorder config at {}", "/flightrecorder-config.jfc");
			return false;
		} else {
			try {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(uRL.openStream()));

				boolean var4;
				try {
					var4 = this.start(bufferedReader, instanceType);
				} catch (Throwable var7) {
					try {
						bufferedReader.close();
					} catch (Throwable var6) {
						var7.addSuppressed(var6);
					}

					throw var7;
				}

				bufferedReader.close();
				return var4;
			} catch (IOException var8) {
				LOGGER.warn("Failed to start flight recorder using configuration at {}", uRL, var8);
				return false;
			}
		}
	}

	@Override
	public Path stop() {
		if (this.currentRecording == null) {
			throw new IllegalStateException("Not currently profiling");
		} else {
			Path path = this.currentRecording.getDestination();
			this.currentRecording.stop();
			return path;
		}
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
		if (EventType.getEventType(ServerTickTimeEvent.class).isEnabled()) {
			long l = Util.nanoTimeSupplier.getAsLong();
			if (this.nextSampleTime <= l) {
				new ServerTickTimeEvent(tickTime).commit();
				this.nextSampleTime = l + TimeUnit.SECONDS.toNanos(1L);
			}
		}
	}

	private boolean start(Reader reader, InstanceType instanceType) {
		if (this.currentRecording != null) {
			LOGGER.warn("Profiling already in progress");
			return false;
		} else {
			try {
				Configuration configuration = Configuration.create(reader);
				String string = DATE_TIME_FORMAT.format(Instant.now());
				this.currentRecording = Util.make(new Recording(configuration), recording -> {
					EVENTS.forEach(recording::enable);
					recording.setDumpOnExit(true);
					recording.setToDisk(true);
					recording.setName("%s-%s-%s".formatted(instanceType.getName(), SharedConstants.getGameVersion().getName(), string));
				});
				Path path = Paths.get("debug/%s-%s.jfr".formatted(instanceType.getName(), string));
				if (!Files.exists(path.getParent(), new LinkOption[0])) {
					Files.createDirectories(path.getParent());
				}

				this.currentRecording.setDestination(path);
				this.currentRecording.start();
				this.addListener();
			} catch (ParseException | IOException var6) {
				LOGGER.warn("Failed to start jfr profiling", (Throwable)var6);
				return false;
			}

			LOGGER.info(
				"Started flight recorder profiling id({}):name({}) - will dump to {} on exit or stop command",
				this.currentRecording.getId(),
				this.currentRecording.getName(),
				this.currentRecording.getDestination()
			);
			return true;
		}
	}

	private void addListener() {
		FlightRecorder.addListener(new FlightRecorderListener() {
			final JfrListener innerListener = new JfrListener(() -> JfrProfiler.this.currentRecording = null);

			public void recordingStateChanged(Recording recording) {
				if (recording == JfrProfiler.this.currentRecording && recording.getState() == RecordingState.STOPPED) {
					this.innerListener.stop(recording.getDestination());
					FlightRecorder.removeListener(this);
				}
			}
		});
	}

	@Override
	public void onPacketReceived(Supplier<String> packetNameSupplier, SocketAddress remoteAddress, int bytes) {
		if (EventType.getEventType(PacketReceivedEvent.class).isEnabled()) {
			new PacketReceivedEvent((String)packetNameSupplier.get(), remoteAddress, bytes).commit();
		}
	}

	@Override
	public void onPacketSent(Supplier<String> packetNameSupplier, SocketAddress remoteAddress, int bytes) {
		if (EventType.getEventType(PacketSentEvent.class).isEnabled()) {
			new PacketReceivedEvent((String)packetNameSupplier.get(), remoteAddress, bytes).commit();
		}
	}

	@Nullable
	@Override
	public Finishable startWorldLoadProfiling() {
		if (!EventType.getEventType(WorldLoadFinishedEvent.class).isEnabled()) {
			return null;
		} else {
			WorldLoadFinishedEvent worldLoadFinishedEvent = new WorldLoadFinishedEvent();
			worldLoadFinishedEvent.begin();
			return worldLoadFinishedEvent::commit;
		}
	}

	@Nullable
	@Override
	public Finishable startChunkGenerationProfiling(ChunkPos chunkPos, RegistryKey<World> world, String targetStatus) {
		if (!EventType.getEventType(ChunkGenerationEvent.class).isEnabled()) {
			return null;
		} else {
			ChunkGenerationEvent chunkGenerationEvent = new ChunkGenerationEvent(chunkPos, world, targetStatus);
			chunkGenerationEvent.begin();
			return chunkGenerationEvent::commit;
		}
	}
}
