package net.minecraft;

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
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.SystemUtil;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;

@Environment(EnvType.CLIENT)
public class class_860 implements DebugRenderer.DebugRenderer {
	private final MinecraftClient field_4509;
	private double field_4510 = Double.MIN_VALUE;
	private final int field_4511 = 12;
	@Nullable
	private class_860.class_861 field_4512;

	public class_860(MinecraftClient minecraftClient) {
		this.field_4509 = minecraftClient;
	}

	@Override
	public void render(long l) {
		double d = (double)SystemUtil.getMeasuringTimeNano();
		if (d - this.field_4510 > 3.0E9) {
			this.field_4510 = d;
			IntegratedServer integratedServer = this.field_4509.method_1576();
			if (integratedServer != null) {
				this.field_4512 = new class_860.class_861(integratedServer);
			} else {
				this.field_4512 = null;
			}
		}

		if (this.field_4512 != null) {
			GlStateManager.disableFog();
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
			);
			GlStateManager.lineWidth(2.0F);
			GlStateManager.disableTexture();
			GlStateManager.depthMask(false);
			Map<ChunkPos, String> map = (Map<ChunkPos, String>)this.field_4512.field_4514.getNow(null);
			double e = this.field_4509.field_1773.method_19418().method_19326().y * 0.85;

			for (Entry<ChunkPos, String> entry : this.field_4512.field_4515.entrySet()) {
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
	final class class_861 {
		private final Map<ChunkPos, String> field_4515;
		private final CompletableFuture<Map<ChunkPos, String>> field_4514;

		private class_861(IntegratedServer integratedServer) {
			ClientWorld clientWorld = class_860.this.field_4509.field_1687;
			DimensionType dimensionType = class_860.this.field_4509.field_1687.field_9247.method_12460();
			ServerWorld serverWorld;
			if (integratedServer.method_3847(dimensionType) != null) {
				serverWorld = integratedServer.method_3847(dimensionType);
			} else {
				serverWorld = null;
			}

			class_4184 lv = class_860.this.field_4509.field_1773.method_19418();
			int i = (int)lv.method_19326().x >> 4;
			int j = (int)lv.method_19326().z >> 4;
			Builder<ChunkPos, String> builder = ImmutableMap.builder();
			ClientChunkManager clientChunkManager = clientWorld.method_2935();

			for (int k = i - 12; k <= i + 12; k++) {
				for (int l = j - 12; l <= j + 12; l++) {
					ChunkPos chunkPos = new ChunkPos(k, l);
					String string = "";
					WorldChunk worldChunk = clientChunkManager.method_12126(k, l, false);
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
