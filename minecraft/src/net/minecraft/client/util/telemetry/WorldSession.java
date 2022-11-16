package net.minecraft.client.util.telemetry;

import java.time.Duration;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class WorldSession {
	private final UUID sessionId = UUID.randomUUID();
	private final TelemetrySender sender;
	private final WorldLoadedEvent worldLoadedEvent;
	private final WorldUnloadedEvent worldUnloadedEvent = new WorldUnloadedEvent();
	private final PerformanceMetricsEvent performanceMetricsEvent;
	private final WorldLoadTimesEvent worldLoadTimesEvent;

	public WorldSession(TelemetrySender sender, boolean newWorld, @Nullable Duration worldLoadTime) {
		this.worldLoadedEvent = new WorldLoadedEvent(this::onLoad);
		this.performanceMetricsEvent = new PerformanceMetricsEvent(sender);
		this.worldLoadTimesEvent = new WorldLoadTimesEvent(newWorld, worldLoadTime);
		this.sender = sender.decorate(builder -> {
			this.worldLoadedEvent.putServerType(builder);
			builder.put(TelemetryEventProperty.WORLD_SESSION_ID, this.sessionId);
		});
	}

	public void tick() {
		this.performanceMetricsEvent.tick();
	}

	public void setGameMode(GameMode gameMode, boolean hardcore) {
		this.worldLoadedEvent.setGameMode(gameMode, hardcore);
		if (this.worldLoadedEvent.getBrand() != null) {
			this.worldLoadedEvent.send(this.sender);
		}
	}

	public void setBrand(String brand) {
		this.worldLoadedEvent.setBrand(brand);
		this.worldLoadedEvent.send(this.sender);
	}

	public void setTick(long tick) {
		this.worldUnloadedEvent.setTick(tick);
	}

	public void onLoad() {
		this.worldLoadTimesEvent.send(this.sender);
		this.worldUnloadedEvent.setStartTime();
		this.performanceMetricsEvent.start();
	}

	public void onUnload() {
		this.worldLoadedEvent.send(this.sender);
		this.performanceMetricsEvent.disableSampling();
		this.worldUnloadedEvent.send(this.sender);
	}
}
