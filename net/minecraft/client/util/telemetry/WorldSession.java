/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util.telemetry;

import java.time.Duration;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.telemetry.PerformanceMetricsEvent;
import net.minecraft.client.util.telemetry.PropertyMap;
import net.minecraft.client.util.telemetry.TelemetryEventProperty;
import net.minecraft.client.util.telemetry.TelemetrySender;
import net.minecraft.client.util.telemetry.WorldLoadTimesEvent;
import net.minecraft.client.util.telemetry.WorldLoadedEvent;
import net.minecraft.client.util.telemetry.WorldUnloadedEvent;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WorldSession {
    private final UUID sessionId = UUID.randomUUID();
    private final TelemetrySender sender;
    private final WorldLoadedEvent worldLoadedEvent;
    private final WorldUnloadedEvent worldUnloadedEvent = new WorldUnloadedEvent();
    private final PerformanceMetricsEvent performanceMetricsEvent;
    private final WorldLoadTimesEvent worldLoadTimesEvent;

    public WorldSession(TelemetrySender sender, boolean newWorld, @Nullable Duration worldLoadTime) {
        this.worldLoadedEvent = new WorldLoadedEvent();
        this.performanceMetricsEvent = new PerformanceMetricsEvent();
        this.worldLoadTimesEvent = new WorldLoadTimesEvent(newWorld, worldLoadTime);
        this.sender = sender.decorate(builder -> {
            this.worldLoadedEvent.putServerType((PropertyMap.Builder)builder);
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
}

