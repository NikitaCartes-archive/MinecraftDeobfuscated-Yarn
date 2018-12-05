package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.debug.RenderDebug;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class class_866 implements RenderDebug.DebugRenderer {
	private final MinecraftClient field_4612;

	public class_866(MinecraftClient minecraftClient) {
		this.field_4612 = minecraftClient;
	}

	@Override
	public void render(float f, long l) {
		PlayerEntity playerEntity = this.field_4612.player;
		World world = this.field_4612.world;
		double d = MathHelper.lerp((double)f, playerEntity.prevRenderX, playerEntity.x);
		double e = MathHelper.lerp((double)f, playerEntity.prevRenderY, playerEntity.y);
		double g = MathHelper.lerp((double)f, playerEntity.prevRenderZ, playerEntity.z);
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SrcBlendFactor.SRC_ALPHA,
			GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
			GlStateManager.SrcBlendFactor.ONE,
			GlStateManager.DstBlendFactor.ZERO
		);
		GlStateManager.disableTexture();
		BlockPos blockPos = new BlockPos(playerEntity.x, playerEntity.y, playerEntity.z);
		Iterable<BlockPos> iterable = BlockPos.iterateBoxPositions(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10));
		LongSet longSet = new LongOpenHashSet();

		for (BlockPos blockPos2 : iterable) {
			int i = world.getLightLevel(LightType.field_9284, blockPos2);
			float h = (float)(15 - i) / 15.0F * 0.5F + 0.16F;
			int j = MathHelper.hsvToRgb(h, 0.9F, 0.9F);
			long m = BlockPos.method_10090(blockPos2.asLong());
			if (longSet.add(m)) {
				RenderDebug.method_3712(
					world.getChunkManager().getLightingProvider().method_15564(LightType.field_9284, BlockPos.fromLong(m)),
					(double)(BlockPos.unpackLongX(m) + 8),
					(double)(BlockPos.unpackLongY(m) + 8),
					(double)(BlockPos.unpackLongZ(m) + 8),
					1.0F,
					16711680,
					0.3F
				);
			}

			if (i != 15) {
				RenderDebug.method_3714(String.valueOf(i), (double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.25, (double)blockPos2.getZ() + 0.5, 1.0F, j);
			}
		}

		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}
}
