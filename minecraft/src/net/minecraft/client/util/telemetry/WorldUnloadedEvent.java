package net.minecraft.client.util.telemetry;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WorldUnloadedEvent implements TelemetryEvent {
	private Optional<Instant> startTime = Optional.empty();
	private long ticksSinceLoad;
	private long lastTick;

	public void setTick(long tick) {
		if (this.lastTick != 0L) {
			this.ticksSinceLoad = this.ticksSinceLoad + Math.max(0L, tick - this.lastTick);
		}

		this.lastTick = tick;
	}

	public void setStartTime() {
		this.startTime = Optional.of(Instant.now());
	}

	private int getSecondsSinceLoad(Instant startTime) {
		Duration duration = Duration.between(startTime, Instant.now());
		return (int)duration.toSeconds();
	}

	@Override
	public void send(TelemetrySender sender) {
		this.startTime.ifPresent(startTime -> sender.send(TelemetryEventType.WORLD_UNLOADED, builder -> {
				builder.put(TelemetryEventProperty.SECONDS_SINCE_LOAD, this.getSecondsSinceLoad(startTime));
				builder.put(TelemetryEventProperty.TICKS_SINCE_LOAD, (int)this.ticksSinceLoad);
			}));
	}
}
