package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

@Environment(EnvType.CLIENT)
public class BlockOutlineDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;

	public BlockOutlineDebugRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void render(long l) {
		Camera camera = this.client.gameRenderer.getCamera();
		double d = camera.getPos().x;
		double e = camera.getPos().y;
		double f = camera.getPos().z;
		BlockView blockView = this.client.player.world;
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.lineWidth(2.0F);
		GlStateManager.disableTexture();
		GlStateManager.depthMask(false);
		BlockPos blockPos = new BlockPos(camera.getPos());

		for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-6, -6, -6), blockPos.add(6, 6, 6))) {
			BlockState blockState = blockView.getBlockState(blockPos2);
			if (blockState.getBlock() != Blocks.field_10124) {
				VoxelShape voxelShape = blockState.getOutlineShape(blockView, blockPos2);

				for (Box box : voxelShape.getBoundingBoxes()) {
					Box box2 = box.offset(blockPos2).expand(0.002).offset(-d, -e, -f);
					double g = box2.minX;
					double h = box2.minY;
					double i = box2.minZ;
					double j = box2.maxX;
					double k = box2.maxY;
					double m = box2.maxZ;
					float n = 1.0F;
					float o = 0.0F;
					float p = 0.0F;
					float q = 0.5F;
					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.field_11039)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
						bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder.vertex(g, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, h, m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, k, m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.field_11035)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
						bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder.vertex(g, k, m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, h, m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, k, m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, h, m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.field_11034)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
						bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder.vertex(j, h, m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, k, m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.field_11043)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
						bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder.vertex(j, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.field_11033)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
						bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder.vertex(g, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, h, m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, h, m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.field_11036)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
						bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
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
