package net.minecraft.client.render.chunk;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class VboChunkRendererList extends ChunkRendererList {
	@Override
	public void render(RenderLayer layer) {
		if (this.isCameraPositionSet) {
			for (ChunkRenderer chunkRenderer : this.chunkRenderers) {
				VertexBuffer vertexBuffer = chunkRenderer.getGlBuffer(layer.ordinal());
				GlStateManager.pushMatrix();
				this.translateToOrigin(chunkRenderer);
				vertexBuffer.bind();
				this.method_1356();
				vertexBuffer.draw(7);
				GlStateManager.popMatrix();
			}

			VertexBuffer.unbind();
			GlStateManager.clearCurrentColor();
			this.chunkRenderers.clear();
		}
	}

	private void method_1356() {
		GlStateManager.vertexPointer(3, 5126, 28, 0);
		GlStateManager.colorPointer(4, 5121, 28, 12);
		GlStateManager.texCoordPointer(2, 5126, 28, 16);
		GLX.glClientActiveTexture(GLX.GL_TEXTURE1);
		GlStateManager.texCoordPointer(2, 5122, 28, 24);
		GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
	}
}
