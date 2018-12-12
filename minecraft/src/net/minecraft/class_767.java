package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.render.chunk.ChunkRenderer;

@Environment(EnvType.CLIENT)
public class class_767 extends class_752 {
	@Override
	public void method_3160(BlockRenderLayer blockRenderLayer) {
		if (this.field_3956) {
			for (ChunkRenderer chunkRenderer : this.field_3955) {
				class_848 lv = (class_848)chunkRenderer;
				GlStateManager.pushMatrix();
				this.method_3157(chunkRenderer);
				GlStateManager.callList(lv.method_3639(blockRenderLayer, lv.getChunkRenderData()));
				GlStateManager.popMatrix();
			}

			GlStateManager.clearCurrentColor();
			this.field_3955.clear();
		}
	}
}
