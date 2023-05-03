package net.minecraft.client.util.telemetry;

import java.time.Duration;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class WorldSession {
	private final UUID sessionId = UUID.randomUUID();
	private final TelemetrySender sender;
	private final WorldLoadedEvent worldLoadedEvent;
	private final WorldUnloadedEvent worldUnloadedEvent = new WorldUnloadedEvent();
	private final PerformanceMetricsEvent performanceMetricsEvent;
	private final WorldLoadTimesEvent worldLoadTimesEvent;

	public WorldSession(TelemetrySender sender, boolean newWorld, @Nullable Duration worldLoadTime, @Nullable String minigameName) {
		this.worldLoadedEvent = new WorldLoadedEvent(minigameName);
		this.performanceMetricsEvent = new PerformanceMetricsEvent();
		this.worldLoadTimesEvent = new WorldLoadTimesEvent(newWorld, worldLoadTime);
		this.sender = sender.decorate(builder -> {
			this.worldLoadedEvent.putServerType(builder);
			builder.put(TelemetryEventProperty.WORLD_SESSION_ID, this.sessionId);
		});
	}

	public void tick() {
		this.performanceMetricsEvent.tick(this.sender);
	}

	public void setGameMode(GameMode gameMode, boolean hardcore) {
		this.worldLoadedEvent.setGameMode(gameMode, hardcore);
		this.worldUnloadedEvent.start();
		this.onLoad();
	}

	public void setBrand(String brand) {
		this.worldLoadedEvent.setBrand(brand);
		this.onLoad();
	}

	public void setTick(long tick) {
		this.worldUnloadedEvent.setTick(tick);
	}

	public void onLoad() {
		if (this.worldLoadedEvent.send(this.sender)) {
			this.worldLoadTimesEvent.send(this.sender);
			this.performanceMetricsEvent.start();
		}
	}

	public void onUnload() {
		this.worldLoadedEvent.send(this.sender);
		this.performanceMetricsEvent.disableSampling();
		this.worldUnloadedEvent.send(this.sender);
	}

	public void onAdvancementMade(World world, Advancement advancement) {
		Identifier identifier = advancement.getId();
		if (advancement.sendsTelemetryEvent() && "minecraft".equals(identifier.getNamespace())) {
			long l = world.getTime();
			this.sender.send(TelemetryEventType.ADVANCEMENT_MADE, properties -> {
				properties.put(TelemetryEventProperty.ADVANCEMENT_ID, identifier.toString());
				properties.put(TelemetryEventProperty.ADVANCEMENT_GAME_TIME, l);
			});
		}
	}
}
