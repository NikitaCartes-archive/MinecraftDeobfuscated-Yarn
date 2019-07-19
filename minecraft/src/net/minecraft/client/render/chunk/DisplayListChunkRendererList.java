package net.minecraft.client.render.chunk;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class DisplayListChunkRendererList extends ChunkRendererList {
	@Override
	public void render(RenderLayer layer) {
		if (this.isCameraPositionSet) {
			for (ChunkRenderer chunkRenderer : this.chunkRenderers) {
				DisplayListChunkRenderer displayListChunkRenderer = (DisplayListChunkRenderer)chunkRenderer;
				GlStateManager.pushMatrix();
				this.translateToOrigin(chunkRenderer);
				GlStateManager.callList(displayListChunkRenderer.method_3639(layer, displayListChunkRenderer.getData()));
				GlStateManager.popMatrix();
			}

			GlStateManager.clearCurrentColor();
			this.chunkRenderers.clear();
		}
	}
}
