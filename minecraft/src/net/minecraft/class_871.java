package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

@Environment(EnvType.CLIENT)
public class class_871 implements DebugRenderer.DebugRenderer {
	private final MinecraftClient field_4628;

	public class_871(MinecraftClient minecraftClient) {
		this.field_4628 = minecraftClient;
	}

	@Override
	public void render(long l) {
		class_4184 lv = this.field_4628.field_1773.method_19418();
		double d = lv.method_19326().x;
		double e = lv.method_19326().y;
		double f = lv.method_19326().z;
		BlockView blockView = this.field_4628.field_1724.field_6002;
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.lineWidth(2.0F);
		GlStateManager.disableTexture();
		GlStateManager.depthMask(false);
		BlockPos blockPos = new BlockPos(lv.method_19326());

		for (BlockPos blockPos2 : BlockPos.iterateBoxPositions(blockPos.add(-6, -6, -6), blockPos.add(6, 6, 6))) {
			BlockState blockState = blockView.method_8320(blockPos2);
			if (blockState.getBlock() != Blocks.field_10124) {
				VoxelShape voxelShape = blockState.method_17770(blockView, blockPos2);

				for (BoundingBox boundingBox : voxelShape.getBoundingBoxList()) {
					BoundingBox boundingBox2 = boundingBox.method_996(blockPos2).expand(0.002).offset(-d, -e, -f);
					double g = boundingBox2.minX;
					double h = boundingBox2.minY;
					double i = boundingBox2.minZ;
					double j = boundingBox2.maxX;
					double k = boundingBox2.maxY;
					double m = boundingBox2.maxZ;
					float n = 1.0F;
					float o = 0.0F;
					float p = 0.0F;
					float q = 0.5F;
					if (Block.method_9501(blockState.method_11628(blockView, blockPos2), Direction.WEST)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
						bufferBuilder.method_1328(5, VertexFormats.field_1576);
						bufferBuilder.vertex(g, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, h, m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, k, m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (Block.method_9501(blockState.method_11628(blockView, blockPos2), Direction.SOUTH)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
						bufferBuilder.method_1328(5, VertexFormats.field_1576);
						bufferBuilder.vertex(g, k, m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, h, m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, k, m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, h, m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (Block.method_9501(blockState.method_11628(blockView, blockPos2), Direction.EAST)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
						bufferBuilder.method_1328(5, VertexFormats.field_1576);
						bufferBuilder.vertex(j, h, m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, k, m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (Block.method_9501(blockState.method_11628(blockView, blockPos2), Direction.NORTH)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
						bufferBuilder.method_1328(5, VertexFormats.field_1576);
						bufferBuilder.vertex(j, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (Block.method_9501(blockState.method_11628(blockView, blockPos2), Direction.DOWN)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
						bufferBuilder.method_1328(5, VertexFormats.field_1576);
						bufferBuilder.vertex(g, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, h, m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, h, m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (Block.method_9501(blockState.method_11628(blockView, blockPos2), Direction.UP)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
						bufferBuilder.method_1328(5, VertexFormats.field_1576);
						bufferBuilder.vertex(g, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, k, m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, k, m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}
				}
			}
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
}
