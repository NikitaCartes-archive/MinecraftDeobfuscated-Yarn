package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.gl.GlBuffer;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class ChunkRendererList {
	private double cameraX;
	private double cameraY;
	private double cameraZ;
	protected final List<ChunkRenderer> chunkRenderers = Lists.<ChunkRenderer>newArrayListWithCapacity(17424);
	protected boolean isCameraPositionSet;

	public void setCameraPosition(double d, double e, double f) {
		this.isCameraPositionSet = true;
		this.chunkRenderers.clear();
		this.cameraX = d;
		this.cameraY = e;
		this.cameraZ = f;
	}

	public void translateToOrigin(ChunkRenderer chunkRenderer) {
		BlockPos blockPos = chunkRenderer.getOrigin();
		RenderSystem.translatef(
			(float)((double)blockPos.getX() - this.cameraX), (float)((double)blockPos.getY() - this.cameraY), (float)((double)blockPos.getZ() - this.cameraZ)
		);
	}

	public void add(ChunkRenderer chunkRenderer, BlockRenderLayer blockRenderLayer) {
		this.chunkRenderers.add(chunkRenderer);
	}

	public void render(BlockRenderLayer blockRenderLayer) {
		if (this.isCameraPositionSet) {
			for (ChunkRenderer chunkRenderer : this.chunkRenderers) {
				GlBuffer glBuffer = chunkRenderer.getGlBuffer(blockRenderLayer.ordinal());
				RenderSystem.pushMatrix();
				this.translateToOrigin(chunkRenderer);
				glBuffer.bind();
				this.method_22121();
				glBuffer.draw(7);
				RenderSystem.popMatrix();
			}

			GlBuffer.unbind();
			RenderSystem.clearCurrentColor();
			this.chunkRenderers.clear();
		}
	}

	private void method_22121() {
		RenderSystem.vertexPointer(3, 5126, 28, 0);
		RenderSystem.colorPointer(4, 5121, 28, 12);
		RenderSystem.texCoordPointer(2, 5126, 28, 16);
		RenderSystem.glClientActiveTexture(33985);
		RenderSystem.texCoordPointer(2, 5122, 28, 24);
		RenderSystem.glClientActiveTexture(33984);
	}
}
