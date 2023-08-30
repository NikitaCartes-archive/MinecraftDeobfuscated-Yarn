package net.minecraft.client.session.telemetry;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class GameLoadTimeEvent {
	public static final GameLoadTimeEvent INSTANCE = new GameLoadTimeEvent(Ticker.systemTicker());
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Ticker ticker;
	private final Map<TelemetryEventProperty<GameLoadTimeEvent.Measurement>, Stopwatch> stopwatches = new HashMap();
	private OptionalLong bootstrapTime = OptionalLong.empty();

	protected GameLoadTimeEvent(Ticker ticker) {
		this.ticker = ticker;
	}

	public synchronized void startTimer(TelemetryEventProperty<GameLoadTimeEvent.Measurement> property) {
		this.addTimer(property, propertyx -> Stopwatch.createStarted(this.ticker));
	}

	public synchronized void addTimer(TelemetryEventProperty<GameLoadTimeEvent.Measurement> property, Stopwatch stopwatch) {
		this.addTimer(property, propertyx -> stopwatch);
	}

	private synchronized void addTimer(
		TelemetryEventProperty<GameLoadTimeEvent.Measurement> property, Function<TelemetryEventProperty<GameLoadTimeEvent.Measurement>, Stopwatch> stopwatchProvider
	) {
		this.stopwatches.computeIfAbsent(property, stopwatchProvider);
	}

	public synchronized void stopTimer(TelemetryEventProperty<GameLoadTimeEvent.Measurement> property) {
		Stopwatch stopwatch = (Stopwatch)this.stopwatches.get(property);
		if (stopwatch == null) {
			LOGGER.warn("Attempted to end step for {} before starting it", property.id());
		} else {
			if (stopwatch.isRunning()) {
				stopwatch.stop();
			}
		}
	}

	public void send(TelemetrySender sender) {
		sender.send(
			TelemetryEventType.GAME_LOAD_TIMES,
			properties -> {
				synchronized (this) {
					this.stopwatches
						.forEach(
							(property, stopwatch) -> {
								if (!stopwatch.isRunning()) {
									long l = stopwatch.elapsed(TimeUnit.MILLISECONDS);
									properties.put(property, new GameLoadTimeEvent.Measurement((int)l));
								} else {
									LOGGER.warn(
										"Measurement {} was discarded since it was still ongoing when the event {} was sent.", property.id(), TelemetryEventType.GAME_LOAD_TIMES.getId()
									);
								}
							}
						);
					this.bootstrapTime
						.ifPresent(bootstrapTime -> properties.put(TelemetryEventProperty.LOAD_TIME_BOOTSTRAP_MS, new GameLoadTimeEvent.Measurement((int)bootstrapTime)));
					this.stopwatches.clear();
				}
			}
		);
	}

	public synchronized void setBootstrapTime(long bootstrapTime) {
		this.bootstrapTime = OptionalLong.of(bootstrapTime);
	}

	@Environment(EnvType.CLIENT)
	public static record Measurement(int millis) {
		public static final Codec<GameLoadTimeEvent.Measurement> CODEC = Codec.INT.xmap(GameLoadTimeEvent.Measurement::new, measurement -> measurement.millis);
	}
}
