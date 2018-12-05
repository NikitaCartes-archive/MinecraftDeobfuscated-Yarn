package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.debug.RenderDebug;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

@Environment(EnvType.CLIENT)
public class class_871 implements RenderDebug.DebugRenderer {
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
		Iterable<BlockPos> iterable = BlockPos.iterateBoxPositions(
			MathHelper.floor(playerEntity.x - 6.0),
			MathHelper.floor(playerEntity.y - 6.0),
			MathHelper.floor(playerEntity.z - 6.0),
			MathHelper.floor(playerEntity.x + 6.0),
			MathHelper.floor(playerEntity.y + 6.0),
			MathHelper.floor(playerEntity.z + 6.0)
		);
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SrcBlendFactor.SRC_ALPHA,
			GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
			GlStateManager.SrcBlendFactor.ONE,
			GlStateManager.DstBlendFactor.ZERO
		);
		GlStateManager.lineWidth(2.0F);
		GlStateManager.disableTexture();
		GlStateManager.depthMask(false);

		for (BlockPos blockPos : iterable) {
			BlockState blockState = blockView.getBlockState(blockPos);
			if (blockState.getBlock() != Blocks.field_10124) {
				VoxelShape voxelShape = blockState.getBoundingShape(blockView, blockPos);

				for (BoundingBox boundingBox : voxelShape.getBoundingBoxList()) {
					BoundingBox boundingBox2 = boundingBox.offset(blockPos).expand(0.002).offset(-d, -e, -g);
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
					if (Block.method_9501(blockState.method_11628(blockView, blockPos), Direction.WEST)) {
						Tessellator tessellator = Tessellator.getInstance();
						VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
						vertexBuffer.begin(5, VertexFormats.POSITION_COLOR);
						vertexBuffer.vertex(h, i, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						vertexBuffer.vertex(h, i, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						vertexBuffer.vertex(h, m, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						vertexBuffer.vertex(h, m, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (Block.method_9501(blockState.method_11628(blockView, blockPos), Direction.SOUTH)) {
						Tessellator tessellator = Tessellator.getInstance();
						VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
						vertexBuffer.begin(5, VertexFormats.POSITION_COLOR);
						vertexBuffer.vertex(h, m, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						vertexBuffer.vertex(h, i, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						vertexBuffer.vertex(k, m, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						vertexBuffer.vertex(k, i, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (Block.method_9501(blockState.method_11628(blockView, blockPos), Direction.EAST)) {
						Tessellator tessellator = Tessellator.getInstance();
						VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
						vertexBuffer.begin(5, VertexFormats.POSITION_COLOR);
						vertexBuffer.vertex(k, i, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						vertexBuffer.vertex(k, i, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						vertexBuffer.vertex(k, m, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						vertexBuffer.vertex(k, m, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (Block.method_9501(blockState.method_11628(blockView, blockPos), Direction.NORTH)) {
						Tessellator tessellator = Tessellator.getInstance();
						VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
						vertexBuffer.begin(5, VertexFormats.POSITION_COLOR);
						vertexBuffer.vertex(k, m, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						vertexBuffer.vertex(k, i, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						vertexBuffer.vertex(h, m, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						vertexBuffer.vertex(h, i, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (Block.method_9501(blockState.method_11628(blockView, blockPos), Direction.DOWN)) {
						Tessellator tessellator = Tessellator.getInstance();
						VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
						vertexBuffer.begin(5, VertexFormats.POSITION_COLOR);
						vertexBuffer.vertex(h, i, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						vertexBuffer.vertex(k, i, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						vertexBuffer.vertex(h, i, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						vertexBuffer.vertex(k, i, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (Block.method_9501(blockState.method_11628(blockView, blockPos), Direction.UP)) {
						Tessellator tessellator = Tessellator.getInstance();
						VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
						vertexBuffer.begin(5, VertexFormats.POSITION_COLOR);
						vertexBuffer.vertex(h, m, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						vertexBuffer.vertex(h, m, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						vertexBuffer.vertex(k, m, j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						vertexBuffer.vertex(k, m, n).color(1.0F, 0.0F, 0.0F, 0.5F).next();
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
