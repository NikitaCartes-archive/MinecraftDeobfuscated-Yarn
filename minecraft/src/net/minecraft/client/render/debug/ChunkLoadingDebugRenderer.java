package net.minecraft.client.render.debug;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;

@Environment(EnvType.CLIENT)
public class ChunkLoadingDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;
	private double lastUpdateTime = Double.MIN_VALUE;
	private final int field_4511 = 12;
	@Nullable
	private ChunkLoadingDebugRenderer.ServerData serverData;

	public ChunkLoadingDebugRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void render(long l) {
		double d = (double)SystemUtil.getMeasuringTimeNano();
		if (d - this.lastUpdateTime > 3.0E9) {
			this.lastUpdateTime = d;
			IntegratedServer integratedServer = this.client.getServer();
			if (integratedServer != null) {
				this.serverData = new ChunkLoadingDebugRenderer.ServerData(integratedServer);
			} else {
				this.serverData = null;
			}
		}

		if (this.serverData != null) {
			GlStateManager.disableFog();
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
			);
			GlStateManager.lineWidth(2.0F);
			GlStateManager.disableTexture();
			GlStateManager.depthMask(false);
			Map<ChunkPos, String> map = (Map<ChunkPos, String>)this.serverData.field_4514.getNow(null);
			double e = this.client.gameRenderer.getCamera().getPos().y * 0.85;

			for (Entry<ChunkPos, String> entry : this.serverData.field_4515.entrySet()) {
				ChunkPos chunkPos = (ChunkPos)entry.getKey();
				String string = (String)entry.getValue();
				if (map != null) {
					string = string + (String)map.get(chunkPos);
				}

				String[] strings = string.split("\n");
				int i = 0;

				for (String string2 : strings) {
					DebugRenderer.method_19429(string2, (double)((chunkPos.x << 4) + 8), e + (double)i, (double)((chunkPos.z << 4) + 8), -1, 0.15F);
					i -= 2;
				}
			}

			GlStateManager.depthMask(true);
			GlStateManager.enableTexture();
			GlStateManager.disableBlend();
			GlStateManager.enableFog();
		}
	}

	@Environment(EnvType.CLIENT)
	final class ServerData {
		private final Map<ChunkPos, String> field_4515;
		private final CompletableFuture<Map<ChunkPos, String>> field_4514;

		private ServerData(IntegratedServer integratedServer) {
			ClientWorld clientWorld = ChunkLoadingDebugRenderer.this.client.world;
			DimensionType dimensionType = ChunkLoadingDebugRenderer.this.client.world.dimension.getType();
			ServerWorld serverWorld;
			if (integratedServer.getWorld(dimensionType) != null) {
				serverWorld = integratedServer.getWorld(dimensionType);
			} else {
				serverWorld = null;
			}

			Camera camera = ChunkLoadingDebugRenderer.this.client.gameRenderer.getCamera();
			int i = (int)camera.getPos().x >> 4;
			int j = (int)camera.getPos().z >> 4;
			Builder<ChunkPos, String> builder = ImmutableMap.builder();
			ClientChunkManager clientChunkManager = clientWorld.method_2935();

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

			this.field_4515 = builder.build();
			this.field_4514 = integratedServer.executeFuture(() -> {
				Builder<ChunkPos, String> builderx = ImmutableMap.builder();
				ServerChunkManager serverChunkManager = serverWorld.method_14178();

				for (int kx = i - 12; kx <= i + 12; kx++) {
					for (int lx = j - 12; lx <= j + 12; lx++) {
						ChunkPos chunkPosx = new ChunkPos(kx, lx);
						builderx.put(chunkPosx, "Server: " + serverChunkManager.getDebugString(chunkPosx));
					}
				}

				return builderx.build();
			});
		}
	}
}
