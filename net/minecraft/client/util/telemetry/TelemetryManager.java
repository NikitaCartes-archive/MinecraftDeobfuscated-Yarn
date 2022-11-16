/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util.telemetry;

import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.minecraft.UserApiService;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.telemetry.PropertyMap;
import net.minecraft.client.util.telemetry.SentTelemetryEvent;
import net.minecraft.client.util.telemetry.TelemetryEventProperty;
import net.minecraft.client.util.telemetry.TelemetryEventType;
import net.minecraft.client.util.telemetry.TelemetryLogManager;
import net.minecraft.client.util.telemetry.TelemetryLogger;
import net.minecraft.client.util.telemetry.TelemetrySender;
import net.minecraft.client.util.telemetry.WorldSession;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TelemetryManager
implements AutoCloseable {
    private static final AtomicInteger NEXT_WORKER_ID = new AtomicInteger(1);
    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("Telemetry-Sender-#" + NEXT_WORKER_ID.getAndIncrement());
        return thread;
    });
    private final UserApiService userApiService;
    private final PropertyMap propertyMap;
    private final Path logDirectory;
    private final CompletableFuture<Optional<TelemetryLogManager>> logManager;

    public TelemetryManager(MinecraftClient client, UserApiService userApiService, Session session) {
        this.userApiService = userApiService;
        PropertyMap.Builder builder = PropertyMap.builder();
        session.getXuid().ifPresent(xuid -> builder.put(TelemetryEventProperty.USER_ID, xuid));
        session.getClientId().ifPresent(clientId -> builder.put(TelemetryEventProperty.CLIENT_ID, clientId));
        builder.put(TelemetryEventProperty.MINECRAFT_SESSION_ID, UUID.randomUUID());
        builder.put(TelemetryEventProperty.GAME_VERSION, SharedConstants.getGameVersion().getId());
        builder.put(TelemetryEventProperty.OPERATING_SYSTEM, Util.getOperatingSystem().getName());
        builder.put(TelemetryEventProperty.PLATFORM, System.getProperty("os.name"));
        builder.put(TelemetryEventProperty.CLIENT_MODDED, MinecraftClient.getModStatus().isModded());
        this.propertyMap = builder.build();
        this.logDirectory = client.runDirectory.toPath().resolve("logs/telemetry");
        this.logManager = TelemetryLogManager.create(this.logDirectory);
    }

    public WorldSession createWorldSession(boolean newWorld, @Nullable Duration worldLoadTime) {
        return new WorldSession(this.getSender(), newWorld, worldLoadTime);
    }

    private TelemetrySender getSender() {
        if (SharedConstants.isDevelopment) {
            return TelemetrySender.NOOP;
        }
        TelemetrySession telemetrySession = this.userApiService.newTelemetrySession(EXECUTOR);
        if (!telemetrySession.isEnabled()) {
            return TelemetrySender.NOOP;
        }
        CompletionStage completableFuture = this.logManager.thenCompose(manager -> manager.map(TelemetryLogManager::getLogger).orElseGet(() -> CompletableFuture.completedFuture(Optional.empty())));
        return (arg_0, arg_1) -> this.method_47705((CompletableFuture)completableFuture, telemetrySession, arg_0, arg_1);
    }

    public Path getLogManager() {
        return this.logDirectory;
    }

    @Override
    public void close() {
        this.logManager.thenAccept(manager -> manager.ifPresent(TelemetryLogManager::close));
    }

    private /* synthetic */ void method_47705(CompletableFuture eventType, TelemetrySession propertyAdder, TelemetryEventType telemetryEventType, Consumer consumer) {
        if (telemetryEventType.isOptional() && !MinecraftClient.getInstance().isOptionalTelemetryEnabled()) {
            return;
        }
        PropertyMap.Builder builder = PropertyMap.builder();
        builder.putAll(this.propertyMap);
        builder.put(TelemetryEventProperty.EVENT_TIMESTAMP_UTC, Instant.now());
        builder.put(TelemetryEventProperty.OPT_IN, telemetryEventType.isOptional());
        consumer.accept(builder);
        SentTelemetryEvent sentTelemetryEvent = new SentTelemetryEvent(telemetryEventType, builder.build());
        eventType.thenAccept(logger -> {
            if (logger.isEmpty()) {
                return;
            }
            ((TelemetryLogger)logger.get()).log(sentTelemetryEvent);
            sentTelemetryEvent.createEvent(propertyAdder).send();
        });
    }
}

