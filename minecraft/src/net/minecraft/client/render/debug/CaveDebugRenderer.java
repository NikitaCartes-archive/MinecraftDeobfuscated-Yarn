package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

@Environment(EnvType.CLIENT)
public class CaveDebugRenderer implements DebugRenderer.DebugRenderer {
	private final MinecraftClient field_4505;
	private final Map<BlockPos, BlockPos> field_4507 = Maps.<BlockPos, BlockPos>newHashMap();
	private final Map<BlockPos, Float> field_4508 = Maps.<BlockPos, Float>newHashMap();
	private final List<BlockPos> field_4506 = Lists.<BlockPos>newArrayList();

	public CaveDebugRenderer(MinecraftClient minecraftClient) {
		this.field_4505 = minecraftClient;
	}

	public void method_3704(BlockPos blockPos, List<BlockPos> list, List<Float> list2) {
		for (int i = 0; i < list.size(); i++) {
			this.field_4507.put(list.get(i), blockPos);
			this.field_4508.put(list.get(i), list2.get(i));
		}

		this.field_4506.add(blockPos);
	}

	@Override
	public void render(float f, long l) {
		PlayerEntity playerEntity = this.field_4505.player;
		BlockView blockView = this.field_4505.world;
		double d = MathHelper.lerp((double)f, playerEntity.prevRenderX, playerEntity.x);
		double e = MathHelper.lerp((double)f, playerEntity.prevRenderY, playerEntity.y);
		double g = MathHelper.lerp((double)f, playerEntity.prevRenderZ, playerEntity.z);
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_1033.SRC_ALPHA, GlStateManager.class_1027.ONE_MINUS_SRC_ALPHA, GlStateManager.class_1033.ONE, GlStateManager.class_1027.ZERO
		);
		GlStateManager.disableTexture();
		BlockPos blockPos = new BlockPos(playerEntity.x, 0.0, playerEntity.z);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);

		for (Entry<BlockPos, BlockPos> entry : this.field_4507.entrySet()) {
			BlockPos blockPos2 = (BlockPos)entry.getKey();
			BlockPos blockPos3 = (BlockPos)entry.getValue();
			float h = (float)(blockPos3.getX() * 128 % 256) / 256.0F;
			float i = (float)(blockPos3.getY() * 128 % 256) / 256.0F;
			float j = (float)(blockPos3.getZ() * 128 % 256) / 256.0F;
			float k = (Float)this.field_4508.get(blockPos2);
			if (blockPos.distanceTo(blockPos2) < 160.0) {
				WorldRenderer.buildBox(
					bufferBuilder,
					(double)((float)blockPos2.getX() + 0.5F) - d - (double)k,
					(double)((float)blockPos2.getY() + 0.5F) - e - (double)k,
					(double)((float)blockPos2.getZ() + 0.5F) - g - (double)k,
					(double)((float)blockPos2.getX() + 0.5F) - d + (double)k,
					(double)((float)blockPos2.getY() + 0.5F) - e + (double)k,
					(double)((float)blockPos2.getZ() + 0.5F) - g + (double)k,
					h,
					i,
					j,
					0.5F
				);
			}
		}

		for (BlockPos blockPos4 : this.field_4506) {
			if (blockPos.distanceTo(blockPos4) < 160.0) {
				WorldRenderer.buildBox(
					bufferBuilder,
					(double)blockPos4.getX() - d,
					(double)blockPos4.getY() - e,
					(double)blockPos4.getZ() - g,
					(double)((float)blockPos4.getX() + 1.0F) - d,
					(double)((float)blockPos4.getY() + 1.0F) - e,
					(double)((float)blockPos4.getZ() + 1.0F) - g,
					1.0F,
					1.0F,
					1.0F,
					1.0F
				);
			}
		}

		tessellator.draw();
		GlStateManager.enableDepthTest();
		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}
}
