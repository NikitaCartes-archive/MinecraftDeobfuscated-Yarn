package net.minecraft.util.profiling.jfr;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.annotation.Nullable;
import jdk.jfr.Configuration;
import jdk.jfr.Event;
import jdk.jfr.FlightRecorder;
import jdk.jfr.FlightRecorderListener;
import jdk.jfr.Recording;
import jdk.jfr.RecordingState;
import net.minecraft.SharedConstants;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.jfr.event.network.PacketReceivedEvent;
import net.minecraft.util.profiling.jfr.event.network.PacketSentEvent;
import net.minecraft.util.profiling.jfr.event.ticking.ServerTickTimeEvent;
import net.minecraft.util.profiling.jfr.event.worldgen.ChunkGenerationEvent;
import net.minecraft.util.profiling.jfr.event.worldgen.WorldLoadFinishedEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JfrProfiler {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String MINECRAFT = "Minecraft";
	public static final String WORLD_GENERATION = "World Generation";
	public static final String TICKING = "Ticking";
	public static final String NETWORK = "Network";
	public static final ImmutableList<Class<? extends Event>> EVENTS = ImmutableList.of(
		ChunkGenerationEvent.class, WorldLoadFinishedEvent.class, ServerTickTimeEvent.class, PacketReceivedEvent.class, PacketSentEvent.class
	);
	private static final String CONFIG_PATH = "/flightrecorder-config.jfc";
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd-HHmm";
	@Nullable
	static Recording currentRecording = null;

	private JfrProfiler() {
	}

	public static boolean start(Path path, JfrProfiler.InstanceType instanceType) {
		if (!FlightRecorder.isAvailable()) {
			LOGGER.warn("Flight Recorder not available!");
			return false;
		} else {
			boolean bl = FlightRecorder.getFlightRecorder()
				.getRecordings()
				.stream()
				.map(Recording::getState)
				.anyMatch(recordingState -> recordingState.equals(RecordingState.NEW) || recordingState.equals(RecordingState.RUNNING));
			if (bl || currentRecording != null) {
				LOGGER.warn("Profiling already in progress");
				return false;
			} else if (!Files.exists(path, new LinkOption[0])) {
				LOGGER.warn("Could not find flight recorder config at {}", "/flightrecorder-config.jfc");
				return false;
			} else {
				try {
					Configuration configuration = Configuration.create(path);
					String string = new SimpleDateFormat("yyyy-MM-dd-HHmm").format(new Date());
					currentRecording = Util.make(new Recording(configuration), recording -> {
						EVENTS.forEach(recording::enable);
						recording.setDumpOnExit(true);
						recording.setToDisk(true);
						recording.setName(instanceType.name().toLowerCase(Locale.ROOT) + "-" + SharedConstants.getGameVersion().getName() + "-" + string);
					});
					currentRecording.setDestination(Paths.get(String.format("debug/%s-%s.jfr", instanceType.name().toLowerCase(Locale.ROOT), string)));
					currentRecording.start();
					FlightRecorder.addListener(new FlightRecorderListener() {
						public void recordingStateChanged(Recording recording) {
							if (recording == JfrProfiler.currentRecording && JfrProfiler.currentRecording.getState() == RecordingState.STOPPED) {
								try {
									Path path = recording.getDestination();
									Path path2 = path.resolveSibling("jfr-report-" + StringUtils.substringBefore(path.getFileName().toString(), ".jfr") + ".json");
									Files.write(path2, JfrProfileRecorder.readProfile(path).collectJson().getBytes(StandardCharsets.UTF_8), new OpenOption[]{StandardOpenOption.CREATE});
								} catch (Throwable var4) {
									System.err.println("Failed to write JSON JFR report");
									var4.printStackTrace(new PrintStream(System.err));
								}
							}
						}
					});
				} catch (ParseException | IOException var5) {
					LOGGER.warn("Failed to start jfr profiling", (Throwable)var5);
					return false;
				}

				LOGGER.info(
					"Started flight recorder profiling id({}):name({}) - will dump to {} on exit or stop command",
					currentRecording.getId(),
					currentRecording.getName(),
					currentRecording.getDestination()
				);
				return true;
			}
		}
	}

	public static boolean start(JfrProfiler.InstanceType instanceType) {
		try {
			URL uRL = JfrProfiler.class.getResource("/flightrecorder-config.jfc");
			if (uRL == null) {
				LOGGER.warn("Could not find default flight recorder config at {}", "/flightrecorder-config.jfc");
				return false;
			} else {
				return start(Paths.get(uRL.toURI()), instanceType);
			}
		} catch (URISyntaxException var2) {
			LOGGER.warn("Failed to start flight recorder using default config", (Throwable)var2);
			return false;
		}
	}

	public static Either<Path, IllegalStateException> stop() {
		if (!FlightRecorder.isAvailable()) {
			return Either.right(new IllegalStateException("Flight recorder is not available"));
		} else {
			boolean bl = FlightRecorder.getFlightRecorder().getRecordings().stream().noneMatch(recording -> recording.getState().equals(RecordingState.RUNNING));
			if (!bl && currentRecording != null) {
				currentRecording.stop();
				Path path = currentRecording.getDestination();
				currentRecording = null;
				return Either.left(path);
			} else {
				return Either.right(new IllegalStateException("Not currently profiling"));
			}
		}
	}

	public static boolean isProfiling() {
		return currentRecording != null;
	}

	public static enum InstanceType {
		CLIENT,
		SERVER;

		public static JfrProfiler.InstanceType get(MinecraftServer server) {
			return server.isDedicated() ? SERVER : CLIENT;
		}
	}
}
