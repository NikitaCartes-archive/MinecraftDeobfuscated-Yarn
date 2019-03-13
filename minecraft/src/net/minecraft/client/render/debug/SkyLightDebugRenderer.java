package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4184;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class SkyLightDebugRenderer implements DebugRenderer.DebugRenderer {
	private final MinecraftClient client;

	public SkyLightDebugRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void render(long l) {
		class_4184 lv = this.client.field_1773.method_19418();
		World world = this.client.field_1687;
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.disableTexture();
		BlockPos blockPos = new BlockPos(lv.method_19326());
		LongSet longSet = new LongOpenHashSet();

		for (BlockPos blockPos2 : BlockPos.iterateBoxPositions(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
			int i = world.method_8314(LightType.SKY, blockPos2);
			float f = (float)(15 - i) / 15.0F * 0.5F + 0.16F;
			int j = MathHelper.hsvToRgb(f, 0.9F, 0.9F);
			long m = ChunkSectionPos.toChunkLong(blockPos2.asLong());
			if (longSet.add(m)) {
				DebugRenderer.method_19429(
					world.method_8398().method_12130().method_15564(LightType.SKY, BlockPos.fromLong(m)),
					(double)(BlockPos.unpackLongX(m) + 8),
					(double)(BlockPos.unpackLongY(m) + 8),
					(double)(BlockPos.unpackLongZ(m) + 8),
					16711680,
					0.3F
				);
			}

			if (i != 15) {
				DebugRenderer.method_3714(String.valueOf(i), (double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.25, (double)blockPos2.getZ() + 0.5, j);
			}
		}

		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}
}
