/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.chunk;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.client.render.chunk.ChunkRendererList;
import net.minecraft.client.render.chunk.DisplayListChunkRenderer;

@Environment(value=EnvType.CLIENT)
public class DisplayListChunkRendererList
extends ChunkRendererList {
    @Override
    public void render(RenderLayer renderLayer) {
        if (!this.isCameraPositionSet) {
            return;
        }
        for (ChunkRenderer chunkRenderer : this.chunkRenderers) {
            DisplayListChunkRenderer displayListChunkRenderer = (DisplayListChunkRenderer)chunkRenderer;
            GlStateManager.pushMatrix();
            this.translateToOrigin(chunkRenderer);
            GlStateManager.callList(displayListChunkRenderer.method_3639(renderLayer, displayListChunkRenderer.getData()));
            GlStateManager.popMatrix();
        }
        GlStateManager.clearCurrentColor();
        this.chunkRenderers.clear();
    }
}

