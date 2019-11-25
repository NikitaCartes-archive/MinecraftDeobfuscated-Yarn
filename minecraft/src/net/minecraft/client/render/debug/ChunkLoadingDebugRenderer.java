package net.minecraft.client.render.debug;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;

@Environment(EnvType.CLIENT)
public class ChunkLoadingDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;
	private double lastUpdateTime = Double.MIN_VALUE;
	private final int field_4511 = 12;
	@Nullable
	private ChunkLoadingDebugRenderer.ChunkLoadingStatus loadingData;

	public ChunkLoadingDebugRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		double d = (double)Util.getMeasuringTimeNano();
		if (d - this.lastUpdateTime > 3.0E9) {
			this.lastUpdateTime = d;
			IntegratedServer integratedServer = this.client.getServer();
			if (integratedServer != null) {
				this.loadingData = new ChunkLoadingDebugRenderer.ChunkLoadingStatus(integratedServer, cameraX, cameraZ);
			} else {
				this.loadingData = null;
			}
		}

		if (this.loadingData != null) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.lineWidth(2.0F);
			RenderSystem.disableTexture();
			RenderSystem.depthMask(false);
			Map<ChunkPos, String> map = (Map<ChunkPos, String>)this.loadingData.serverStates.getNow(null);
			double e = this.client.gameRenderer.getCamera().getPos().y * 0.85;

			for (Entry<ChunkPos, String> entry : this.loadingData.clientStates.entrySet()) {
				ChunkPos chunkPos = (ChunkPos)entry.getKey();
				String string = (String)entry.getValue();
				if (map != null) {
					string = string + (String)map.get(chunkPos);
				}

				String[] strings = string.split("\n");
				int i = 0;

				for (String string2 : strings) {
					DebugRenderer.drawString(string2, (double)((chunkPos.x << 4) + 8), e + (double)i, (double)((chunkPos.z << 4) + 8), -1, 0.15F);
					i -= 2;
				}
			}

			RenderSystem.depthMask(true);
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
		}
	}

	@Environment(EnvType.CLIENT)
	final class ChunkLoadingStatus {
		private final Map<ChunkPos, String> clientStates;
		private final CompletableFuture<Map<ChunkPos, String>> serverStates;

		private ChunkLoadingStatus(IntegratedServer integratedServer, double d, double e) {
			ClientWorld clientWorld = ChunkLoadingDebugRenderer.this.client.world;
			DimensionType dimensionType = ChunkLoadingDebugRenderer.this.client.world.dimension.getType();
			ServerWorld serverWorld;
			if (integratedServer.getWorld(dimensionType) != null) {
				serverWorld = integratedServer.getWorld(dimensionType);
			} else {
				serverWorld = null;
			}

			int i = (int)d >> 4;
			int j = (int)e >> 4;
			Builder<ChunkPos, String> builder = ImmutableMap.builder();
			ClientChunkManager clientChunkManager = clientWorld.getChunkManager();

			for (int k = i - 12; k <= i + 12; k++) {
				for (int l = j - 12; l <= j + 12; l++) {
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
				Builder<ChunkPos, String> builderx = ImmutableMap.builder();
				ServerChunkManager serverChunkManager = serverWorld.getChunkManager();

				for (int kx = i - 12; kx <= i + 12; kx++) {
					for (int lx = j - 12; lx <= j + 12; lx++) {
						ChunkPos chunkPosx = new ChunkPos(kx, lx);
						builderx.put(chunkPosx, "Server: " + serverChunkManager.method_23273(chunkPosx));
					}
				}

				return builderx.build();
			});
		}
	}
}
