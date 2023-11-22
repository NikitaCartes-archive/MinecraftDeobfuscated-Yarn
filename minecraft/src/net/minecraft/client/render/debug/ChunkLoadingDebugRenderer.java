package net.minecraft.client.render.debug;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
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
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Colors;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

@Environment(EnvType.CLIENT)
public class ChunkLoadingDebugRenderer implements DebugRenderer.Renderer {
	final MinecraftClient client;
	private double lastUpdateTime = Double.MIN_VALUE;
	private final int LOADING_DATA_CHUNK_RANGE = 12;
	@Nullable
	private ChunkLoadingDebugRenderer.ChunkLoadingStatus loadingData;

	public ChunkLoadingDebugRenderer(MinecraftClient client) {
		this.client = client;
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
					DebugRenderer.drawString(
						matrices,
						vertexConsumers,
						string2,
						(double)ChunkSectionPos.getOffsetPos(chunkPos.x, 8),
						e + (double)i,
						(double)ChunkSectionPos.getOffsetPos(chunkPos.z, 8),
						Colors.WHITE,
						0.15F,
						true,
						0.0F,
						true
					);
					i -= 2;
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	final class ChunkLoadingStatus {
		final Map<ChunkPos, String> clientStates;
		final CompletableFuture<Map<ChunkPos, String>> serverStates;

		ChunkLoadingStatus(IntegratedServer server, double x, double z) {
			ClientWorld clientWorld = ChunkLoadingDebugRenderer.this.client.world;
			RegistryKey<World> registryKey = clientWorld.getRegistryKey();
			int i = ChunkSectionPos.getSectionCoord(x);
			int j = ChunkSectionPos.getSectionCoord(z);
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
			this.serverStates = server.submit(() -> {
				ServerWorld serverWorld = server.getWorld(registryKey);
				if (serverWorld == null) {
					return ImmutableMap.of();
				} else {
					Builder<ChunkPos, String> builderx = ImmutableMap.builder();
					ServerChunkManager serverChunkManager = serverWorld.getChunkManager();

					for (int kx = i - 12; kx <= i + 12; kx++) {
						for (int lx = j - 12; lx <= j + 12; lx++) {
							ChunkPos chunkPosx = new ChunkPos(kx, lx);
							builderx.put(chunkPosx, "Server: " + serverChunkManager.getChunkLoadingDebugInfo(chunkPosx));
						}
					}

					return builderx.build();
				}
			});
		}
	}
}
