package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.gl.GlBuffer;
import net.minecraft.client.render.chunk.ChunkRenderer;

@Environment(EnvType.CLIENT)
public class class_292 extends class_752 {
	@Override
	public void method_3160(BlockRenderLayer blockRenderLayer) {
		if (this.field_3956) {
			for (ChunkRenderer chunkRenderer : this.field_3955) {
				GlBuffer glBuffer = chunkRenderer.method_3656(blockRenderLayer.ordinal());
				GlStateManager.pushMatrix();
				this.method_3157(chunkRenderer);
				chunkRenderer.method_3664();
				glBuffer.bind();
				this.method_1356();
				glBuffer.draw(7);
				GlStateManager.popMatrix();
			}

			GlBuffer.unbind();
			GlStateManager.clearCurrentColor();
			this.field_3955.clear();
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
