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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

@Environment(EnvType.CLIENT)
public class class_871 implements DebugRenderer.DebugRenderer {
	private final MinecraftClient field_4628;

	public class_871(MinecraftClient minecraftClient) {
		this.field_4628 = minecraftClient;
	}

	@Override
	public void render(float f, long l) {
		PlayerEntity playerEntity = this.field_4628.player;
		double d = MathHelper.lerp((double)f, playerEntity.prevRenderX, playerEntity.x);
		double e = MathHelper.lerp((double)f, playerEntity.prevRenderY, playerEntity.y);
		double g = MathHelper.lerp((double)f, playerEntity.prevRenderZ, playerEntity.z);
		BlockView blockView = this.field_4628.player.world;
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.lineWidth(2.0F);
		GlStateManager.disableTexture();
		GlStateManager.depthMask(false);
		BlockPos blockPos = new BlockPos(playerEntity);

		for (BlockPos blockPos2 : BlockPos.iterateBoxPositions(blockPos.add(-6, -6, -6), blockPos.add(6, 6, 6))) {
			BlockState blockState = blockView.getBlockState(blockPos2);
			if (blockState.getBlock() != Blocks.field_10124) {
				VoxelShape voxelShape = blockState.getOutlineShape(blockView, blockPos2);

				for (BoundingBox boundingBox : voxelShape.getBoundingBoxList()) {
					BoundingBox boundingBox2 = boundingBox.offset(blockPos2).expand(0.002).offset(-d, -e, -g);
					double h = boundingBox2.minX;
					double i = boundingBox2.minY;
					double j = boundingBox2.minZ;
					double k = boundingBox2.maxX;
					double m = boundingBox2.maxY;
					double n = boundingBox2.maxZ;
					float o = 1.0F;
					float p = 0.0F;
					float q = 0.0F;
					float r = 0.5F;
					if (Block.isFaceFullSquare(blockState.getCollisionShape(blockView, blockPos2), Direction.WEST)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
						bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder.vertex(h, i, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(h, i, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(h, m, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(h, m, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (Block.isFaceFullSquare(blockState.getCollisionShape(blockView, blockPos2), Direction.SOUTH)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
						bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder.vertex(h, m, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(h, i, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(k, m, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(k, i, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (Block.isFaceFullSquare(blockState.getCollisionShape(blockView, blockPos2), Direction.EAST)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
						bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder.vertex(k, i, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(k, i, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(k, m, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(k, m, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (Block.isFaceFullSquare(blockState.getCollisionShape(blockView, blockPos2), Direction.NORTH)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
						bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder.vertex(k, m, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(k, i, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(h, m, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(h, i, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (Block.isFaceFullSquare(blockState.getCollisionShape(blockView, blockPos2), Direction.DOWN)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
						bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder.vertex(h, i, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(k, i, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(h, i, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(k, i, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (Block.isFaceFullSquare(blockState.getCollisionShape(blockView, blockPos2), Direction.UP)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
						bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder.vertex(h, m, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(h, m, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(k, m, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(k, m, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
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
