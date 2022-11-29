/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util.telemetry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.telemetry.PropertyMap;
import net.minecraft.client.util.telemetry.TelemetryEventProperty;
import net.minecraft.client.util.telemetry.TelemetryEventType;
import net.minecraft.client.util.telemetry.TelemetrySender;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WorldLoadedEvent {
    private boolean sent;
    @Nullable
    private TelemetryEventProperty.GameMode gameMode = null;
    @Nullable
    private String brand;

    public void putServerType(PropertyMap.Builder builder) {
        if (this.brand != null) {
            builder.put(TelemetryEventProperty.SERVER_MODDED, !this.brand.equals("vanilla"));
        }
        builder.put(TelemetryEventProperty.SERVER_TYPE, this.getServerType());
    }

    private TelemetryEventProperty.ServerType getServerType() {
        if (MinecraftClient.getInstance().isConnectedToRealms()) {
            return TelemetryEventProperty.ServerType.REALM;
        }
        if (MinecraftClient.getInstance().isIntegratedServerRunning()) {
            return TelemetryEventProperty.ServerType.LOCAL;
        }
        return TelemetryEventProperty.ServerType.OTHER;
    }

    public boolean send(TelemetrySender sender) {
        if (this.sent || this.gameMode == null || this.brand == null) {
            return false;
        }
        this.sent = true;
        sender.send(TelemetryEventType.WORLD_LOADED, builder -> builder.put(TelemetryEventProperty.GAME_MODE, this.gameMode));
        return true;
    }

    public void setGameMode(GameMode gameMode, boolean hardcore) {
        this.gameMode = switch (gameMode) {
            default -> throw new IncompatibleClassChangeError();
            case GameMode.SURVIVAL -> {
                if (hardcore) {
                    yield TelemetryEventProperty.GameMode.HARDCORE;
                }
                yield TelemetryEventProperty.GameMode.SURVIVAL;
            }
            case GameMode.CREATIVE -> TelemetryEventProperty.GameMode.CREATIVE;
            case GameMode.ADVENTURE -> TelemetryEventProperty.GameMode.ADVENTURE;
            case GameMode.SPECTATOR -> TelemetryEventProperty.GameMode.SPECTATOR;
        };
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}

