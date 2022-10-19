/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import com.mojang.authlib.minecraft.TelemetryEvent;
import com.mojang.authlib.minecraft.TelemetryPropertyContainer;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.minecraft.UserApiService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.GameVersion;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.TelemetryConstants;
import net.minecraft.util.Util;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TelemetrySender {
    private static final AtomicInteger NEXT_WORKER_ID = new AtomicInteger(1);
    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("Telemetry-Sender-#" + NEXT_WORKER_ID.getAndIncrement());
        return thread;
    });
    private final MinecraftClient client;
    private final TelemetrySession session;
    private boolean sent;
    @Nullable
    private PlayerGameMode gameMode;
    @Nullable
    private String serverBrand;

    public TelemetrySender(MinecraftClient client, UserApiService userApiService, Optional<String> userId, Optional<String> clientId, UUID deviceSessionId) {
        this.client = client;
        if (!SharedConstants.isDevelopment) {
            this.session = userApiService.newTelemetrySession(EXECUTOR);
            TelemetryPropertyContainer telemetryPropertyContainer = this.session.globalProperties();
            TelemetrySender.addProperty("UserId", userId, telemetryPropertyContainer);
            TelemetrySender.addProperty("ClientId", clientId, telemetryPropertyContainer);
            telemetryPropertyContainer.addProperty("deviceSessionId", deviceSessionId.toString());
            telemetryPropertyContainer.addProperty("WorldSessionId", UUID.randomUUID().toString());
            this.session.eventSetupFunction(container -> container.addProperty("eventTimestampUtc", TelemetryConstants.DATE_TIME_FORMATTER.format(Instant.now())));
        } else {
            this.session = TelemetrySession.DISABLED;
        }
    }

    private static void addProperty(String name, Optional<String> propertyValue, TelemetryPropertyContainer container) {
        propertyValue.ifPresentOrElse(value -> container.addProperty(name, (String)value), () -> container.addNullProperty(name));
    }

    public void setGameModeAndSend(GameMode gameMode, boolean hardcore) {
        this.gameMode = new PlayerGameMode(gameMode, hardcore);
        if (this.serverBrand != null) {
            this.send(this.gameMode);
        }
    }

    public void setServerBrandAndSend(String brand) {
        this.serverBrand = brand;
        if (this.gameMode != null) {
            this.send(this.gameMode);
        }
    }

    private void send(PlayerGameMode gameMode) {
        if (this.sent) {
            return;
        }
        this.sent = true;
        if (!this.session.isEnabled()) {
            return;
        }
        TelemetryEvent telemetryEvent = this.session.createNewEvent("WorldLoaded");
        GameVersion gameVersion = SharedConstants.getGameVersion();
        telemetryEvent.addProperty("build_display_name", gameVersion.getId());
        telemetryEvent.addProperty("clientModded", MinecraftClient.getModStatus().isModded());
        if (this.serverBrand != null) {
            telemetryEvent.addProperty("serverModded", !this.serverBrand.equals("vanilla"));
        } else {
            telemetryEvent.addNullProperty("serverModded");
        }
        telemetryEvent.addProperty("server_type", this.getServerType());
        telemetryEvent.addProperty("BuildPlat", Util.getOperatingSystem().getName());
        telemetryEvent.addProperty("Plat", System.getProperty("os.name"));
        telemetryEvent.addProperty("javaVersion", System.getProperty("java.version"));
        telemetryEvent.addProperty("PlayerGameMode", gameMode.getId());
        telemetryEvent.send();
    }

    private String getServerType() {
        if (this.client.isConnectedToRealms()) {
            return "realm";
        }
        if (this.client.isIntegratedServerRunning()) {
            return "local";
        }
        return "server";
    }

    public void send() {
        if (this.gameMode != null) {
            this.send(this.gameMode);
        }
    }

    @Environment(value=EnvType.CLIENT)
    record PlayerGameMode(GameMode gameMode, boolean hardcore) {
        public int getId() {
            if (this.hardcore && this.gameMode == GameMode.SURVIVAL) {
                return 99;
            }
            return switch (this.gameMode) {
                default -> throw new IncompatibleClassChangeError();
                case GameMode.SURVIVAL -> 0;
                case GameMode.CREATIVE -> 1;
                case GameMode.ADVENTURE -> 2;
                case GameMode.SPECTATOR -> 6;
            };
        }
    }
}

