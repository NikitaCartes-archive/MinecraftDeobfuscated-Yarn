/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChunkLoadingDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;
    private double lastUpdateTime = Double.MIN_VALUE;
    private final int field_4511 = 12;
    @Nullable
    private ChunkLoadingStatus loadingData;

    public ChunkLoadingDebugRenderer(MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        double d = Util.getMeasuringTimeNano();
        if (d - this.lastUpdateTime > 3.0E9) {
            this.lastUpdateTime = d;
            IntegratedServer integratedServer = this.client.getServer();
            this.loadingData = integratedServer != null ? new ChunkLoadingStatus(integratedServer, cameraX, cameraZ) : null;
        }
        if (this.loadingData != null) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.lineWidth(2.0f);
            RenderSystem.disableTexture();
            RenderSystem.depthMask(false);
            Map map = this.loadingData.serverStates.getNow(null);
            double e = this.client.gameRenderer.getCamera().getPos().y * 0.85;
            for (Map.Entry entry : this.loadingData.clientStates.entrySet()) {
                ChunkPos chunkPos = (ChunkPos)entry.getKey();
                String string = (String)entry.getValue();
                if (map != null) {
                    string = string + (String)map.get(chunkPos);
                }
                String[] strings = string.split("\n");
                int i = 0;
                for (String string2 : strings) {
                    DebugRenderer.drawString(string2, ChunkSectionPos.getOffsetPos(chunkPos.x, 8), e + (double)i, ChunkSectionPos.getOffsetPos(chunkPos.z, 8), -1, 0.15f);
                    i -= 2;
                }
            }
            RenderSystem.depthMask(true);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }
    }

    @Environment(value=EnvType.CLIENT)
    final class ChunkLoadingStatus {
        private final Map<ChunkPos, String> clientStates;
        private final CompletableFuture<Map<ChunkPos, String>> serverStates;

        private ChunkLoadingStatus(IntegratedServer integratedServer, double d, double e) {
            ClientWorld clientWorld = ((ChunkLoadingDebugRenderer)ChunkLoadingDebugRenderer.this).client.world;
            RegistryKey<World> registryKey = clientWorld.getRegistryKey();
            int i = ChunkSectionPos.method_32204(d);
            int j = ChunkSectionPos.method_32204(e);
            ImmutableMap.Builder<ChunkPos, String> builder = ImmutableMap.builder();
            ClientChunkManager clientChunkManager = clientWorld.getChunkManager();
            for (int k = i - 12; k <= i + 12; ++k) {
                for (int l = j - 12; l <= j + 12; ++l) {
                    ChunkPos chunkPos = new ChunkPos(k, l);
                    String string = "";
                    WorldChunk worldChunk = clientChunkManager.getWorldChunk(k, l, false);
                    string = string + "Client: ";
                    if (worldChunk == null) {
                        string = string + "0n/a\n";
                    } else {
                        string = string + (worldChunk.isEmpty() ? " E" : "");
                        string = string + "\n";
                    }
                    builder.put(chunkPos, string);
                }
            }
            this.clientStates = builder.build();
            this.serverStates = integratedServer.submit(() -> {
                ServerWorld serverWorld = integratedServer.getWorld(registryKey);
                if (serverWorld == null) {
                    return ImmutableMap.of();
                }
                ImmutableMap.Builder<ChunkPos, String> builder = ImmutableMap.builder();
                ServerChunkManager serverChunkManager = serverWorld.getChunkManager();
                for (int k = i - 12; k <= i + 12; ++k) {
                    for (int l = j - 12; l <= j + 12; ++l) {
                        ChunkPos chunkPos = new ChunkPos(k, l);
                        builder.put(chunkPos, "Server: " + serverChunkManager.getChunkLoadingDebugInfo(chunkPos));
                    }
                }
                return builder.build();
            });
        }
    }
}

