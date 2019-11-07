package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class CaveDebugRenderer implements DebugRenderer.Renderer {
	private final Map<BlockPos, BlockPos> field_4507 = Maps.<BlockPos, BlockPos>newHashMap();
	private final Map<BlockPos, Float> field_4508 = Maps.<BlockPos, Float>newHashMap();
	private final List<BlockPos> field_4506 = Lists.<BlockPos>newArrayList();

	public void method_3704(BlockPos blockPos, List<BlockPos> list, List<Float> list2) {
		for (int i = 0; i < list.size(); i++) {
			this.field_4507.put(list.get(i), blockPos);
			this.field_4508.put(list.get(i), list2.get(i));
		}

		this.field_4506.add(blockPos);
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, double d, double e, double f, long l) {
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableTexture();
		BlockPos blockPos = new BlockPos(d, 0.0, f);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);

		for (Entry<BlockPos, BlockPos> entry : this.field_4507.entrySet()) {
			BlockPos blockPos2 = (BlockPos)entry.getKey();
			BlockPos blockPos3 = (BlockPos)entry.getValue();
			float g = (float)(blockPos3.getX() * 128 % 256) / 256.0F;
			float h = (float)(blockPos3.getY() * 128 % 256) / 256.0F;
			float i = (float)(blockPos3.getZ() * 128 % 256) / 256.0F;
			float j = (Float)this.field_4508.get(blockPos2);
			if (blockPos.isWithinDistance(blockPos2, 160.0)) {
				WorldRenderer.drawBox(
					bufferBuilder,
					(double)((float)blockPos2.getX() + 0.5F) - d - (double)j,
					(double)((float)blockPos2.getY() + 0.5F) - e - (double)j,
					(double)((float)blockPos2.getZ() + 0.5F) - f - (double)j,
					(double)((float)blockPos2.getX() + 0.5F) - d + (double)j,
					(double)((float)blockPos2.getY() + 0.5F) - e + (double)j,
					(double)((float)blockPos2.getZ() + 0.5F) - f + (double)j,
					g,
					h,
					i,
					0.5F
				);
			}
		}

		for (BlockPos blockPos4 : this.field_4506) {
			if (blockPos.isWithinDistance(blockPos4, 160.0)) {
				WorldRenderer.drawBox(
					bufferBuilder,
					(double)blockPos4.getX() - d,
					(double)blockPos4.getY() - e,
					(double)blockPos4.getZ() - f,
					(double)((float)blockPos4.getX() + 1.0F) - d,
					(double)((float)blockPos4.getY() + 1.0F) - e,
					(double)((float)blockPos4.getZ() + 1.0F) - f,
					1.0F,
					1.0F,
					1.0F,
					1.0F
				);
			}
		}

		tessellator.draw();
		RenderSystem.enableDepthTest();
		RenderSystem.enableTexture();
		RenderSystem.popMatrix();
	}
}
