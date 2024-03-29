package net.minecraft.client.session.telemetry;

import java.time.Duration;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WorldLoadTimesEvent {
	private final boolean newWorld;
	@Nullable
	private final Duration worldLoadTime;

	public WorldLoadTimesEvent(boolean newWorld, @Nullable Duration worldLoadTime) {
		this.worldLoadTime = worldLoadTime;
		this.newWorld = newWorld;
	}

	public void send(TelemetrySender sender) {
		if (this.worldLoadTime != null) {
			sender.send(TelemetryEventType.WORLD_LOAD_TIMES, builder -> {
				builder.put(TelemetryEventProperty.WORLD_LOAD_TIME_MS, (int)this.worldLoadTime.toMillis());
				builder.put(TelemetryEventProperty.NEW_WORLD, this.newWorld);
			});
		}
	}
}
