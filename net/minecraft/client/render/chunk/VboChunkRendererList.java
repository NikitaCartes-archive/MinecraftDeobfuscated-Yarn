/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.chunk;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.gl.GlBuffer;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.client.render.chunk.ChunkRendererList;

@Environment(value=EnvType.CLIENT)
public class VboChunkRendererList
extends ChunkRendererList {
    @Override
    public void render(BlockRenderLayer blockRenderLayer) {
        if (!this.isCameraPositionSet) {
            return;
        }
        for (ChunkRenderer chunkRenderer : this.chunkRenderers) {
            GlBuffer glBuffer = chunkRenderer.getGlBuffer(blockRenderLayer.ordinal());
            GlStateManager.pushMatrix();
            this.translateToOrigin(chunkRenderer);
            glBuffer.bind();
            this.method_1356();
            glBuffer.draw(7);
            GlStateManager.popMatrix();
        }
        GlBuffer.unbind();
        GlStateManager.clearCurrentColor();
        this.chunkRenderers.clear();
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

