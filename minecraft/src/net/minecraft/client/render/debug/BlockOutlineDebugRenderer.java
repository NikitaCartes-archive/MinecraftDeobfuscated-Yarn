package net.minecraft.client.render.debug;

import com.mojang.blaze3d.systems.RenderSystem;
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
	public void render(long limitTime) {
		Camera camera = this.client.gameRenderer.getCamera();
		double d = camera.getPos().x;
		double e = camera.getPos().y;
		double f = camera.getPos().z;
		BlockView blockView = this.client.player.world;
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.lineWidth(2.0F);
		RenderSystem.disableTexture();
		RenderSystem.depthMask(false);
		BlockPos blockPos = new BlockPos(camera.getPos());

		for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-6, -6, -6), blockPos.add(6, 6, 6))) {
			BlockState blockState = blockView.getBlockState(blockPos2);
			if (blockState.getBlock() != Blocks.AIR) {
				VoxelShape voxelShape = blockState.getOutlineShape(blockView, blockPos2);

				for (Box box : voxelShape.getBoundingBoxes()) {
					Box box2 = box.offset(blockPos2).expand(0.002).offset(-d, -e, -f);
					double g = box2.x1;
					double h = box2.y1;
					double i = box2.z1;
					double j = box2.x2;
					double k = box2.y2;
					double l = box2.z2;
					float m = 1.0F;
					float n = 0.0F;
					float o = 0.0F;
					float p = 0.5F;
					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.WEST)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBuffer();
						bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder.vertex(g, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, h, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, k, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.SOUTH)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBuffer();
						bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder.vertex(g, k, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, h, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, k, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, h, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.EAST)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBuffer();
						bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder.vertex(j, h, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, k, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.NORTH)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBuffer();
						bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder.vertex(j, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.DOWN)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBuffer();
						bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder.vertex(g, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, h, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, h, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.UP)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBuffer();
						bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder.vertex(g, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, k, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(j, k, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}
				}
			}
		}

		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}
}
