package net.minecraft.client.util.telemetry;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WorldUnloadedEvent {
	private static final int field_41712 = -1;
	private Optional<Instant> startTime = Optional.empty();
	private long ticksSinceLoad;
	private long lastTick;

	public void start() {
		this.lastTick = -1L;
		if (this.startTime.isEmpty()) {
			this.startTime = Optional.of(Instant.now());
		}
	}

	public void setTick(long tick) {
		if (this.lastTick != -1L) {
			this.ticksSinceLoad = this.ticksSinceLoad + Math.max(0L, tick - this.lastTick);
		}

		this.lastTick = tick;
	}

	private int getSecondsSinceLoad(Instant startTime) {
		Duration duration = Duration.between(startTime, Instant.now());
		return (int)duration.toSeconds();
	}

	public void send(TelemetrySender sender) {
		this.startTime.ifPresent(startTime -> sender.send(TelemetryEventType.WORLD_UNLOADED, builder -> {
				builder.put(TelemetryEventProperty.SECONDS_SINCE_LOAD, this.getSecondsSinceLoad(startTime));
				builder.put(TelemetryEventProperty.TICKS_SINCE_LOAD, (int)this.ticksSinceLoad);
			}));
	}
}
