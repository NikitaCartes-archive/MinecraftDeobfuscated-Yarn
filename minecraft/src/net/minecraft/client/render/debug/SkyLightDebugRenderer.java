package net.minecraft.client.render.debug;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class SkyLightDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;
	private static final int RANGE = 10;

	public SkyLightDebugRenderer(MinecraftClient client) {
		this.client = client;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		World world = this.client.world;
		BlockPos blockPos = BlockPos.ofFloored(cameraX, cameraY, cameraZ);
		LongSet longSet = new LongOpenHashSet();

		for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
			int i = world.getLightLevel(LightType.SKY, blockPos2);
			float f = (float)(15 - i) / 15.0F * 0.5F + 0.16F;
			int j = MathHelper.hsvToRgb(f, 0.9F, 0.9F);
			long l = ChunkSectionPos.fromBlockPos(blockPos2.asLong());
			if (longSet.add(l)) {
				DebugRenderer.drawString(
					matrices,
					vertexConsumers,
					world.getChunkManager().getLightingProvider().displaySectionLevel(LightType.SKY, ChunkSectionPos.from(l)),
					(double)ChunkSectionPos.getOffsetPos(ChunkSectionPos.unpackX(l), 8),
					(double)ChunkSectionPos.getOffsetPos(ChunkSectionPos.unpackY(l), 8),
					(double)ChunkSectionPos.getOffsetPos(ChunkSectionPos.unpackZ(l), 8),
					16711680,
					0.3F
				);
			}

			if (i != 15) {
				DebugRenderer.drawString(
					matrices, vertexConsumers, String.valueOf(i), (double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.25, (double)blockPos2.getZ() + 0.5, j
				);
			}
		}
	}
}
