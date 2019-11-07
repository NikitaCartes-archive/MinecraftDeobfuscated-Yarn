/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public class ChunkBorderDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;

    public ChunkBorderDebugRenderer(MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, double d, double e, double f, long l) {
        int k;
        RenderSystem.enableDepthTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        Entity entity = this.client.gameRenderer.getCamera().getFocusedEntity();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        double g = 0.0 - e;
        double h = 256.0 - e;
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        double i = (double)(entity.chunkX << 4) - d;
        double j = (double)(entity.chunkZ << 4) - f;
        RenderSystem.lineWidth(1.0f);
        bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
        for (k = -16; k <= 32; k += 16) {
            for (int m = -16; m <= 32; m += 16) {
                bufferBuilder.vertex(i + (double)k, g, j + (double)m).color(1.0f, 0.0f, 0.0f, 0.0f).next();
                bufferBuilder.vertex(i + (double)k, g, j + (double)m).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                bufferBuilder.vertex(i + (double)k, h, j + (double)m).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                bufferBuilder.vertex(i + (double)k, h, j + (double)m).color(1.0f, 0.0f, 0.0f, 0.0f).next();
            }
        }
        for (k = 2; k < 16; k += 2) {
            bufferBuilder.vertex(i + (double)k, g, j).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            bufferBuilder.vertex(i + (double)k, g, j).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i + (double)k, h, j).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i + (double)k, h, j).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            bufferBuilder.vertex(i + (double)k, g, j + 16.0).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            bufferBuilder.vertex(i + (double)k, g, j + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i + (double)k, h, j + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i + (double)k, h, j + 16.0).color(1.0f, 1.0f, 0.0f, 0.0f).next();
        }
        for (k = 2; k < 16; k += 2) {
            bufferBuilder.vertex(i, g, j + (double)k).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            bufferBuilder.vertex(i, g, j + (double)k).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i, h, j + (double)k).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i, h, j + (double)k).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            bufferBuilder.vertex(i + 16.0, g, j + (double)k).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            bufferBuilder.vertex(i + 16.0, g, j + (double)k).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i + 16.0, h, j + (double)k).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i + 16.0, h, j + (double)k).color(1.0f, 1.0f, 0.0f, 0.0f).next();
        }
        for (k = 0; k <= 256; k += 2) {
            double n = (double)k - e;
            bufferBuilder.vertex(i, n, j).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            bufferBuilder.vertex(i, n, j).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i, n, j + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i + 16.0, n, j + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i + 16.0, n, j).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i, n, j).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder.vertex(i, n, j).color(1.0f, 1.0f, 0.0f, 0.0f).next();
        }
        tessellator.draw();
        RenderSystem.lineWidth(2.0f);
        bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
        for (k = 0; k <= 16; k += 16) {
            for (int m = 0; m <= 16; m += 16) {
                bufferBuilder.vertex(i + (double)k, g, j + (double)m).color(0.25f, 0.25f, 1.0f, 0.0f).next();
                bufferBuilder.vertex(i + (double)k, g, j + (double)m).color(0.25f, 0.25f, 1.0f, 1.0f).next();
                bufferBuilder.vertex(i + (double)k, h, j + (double)m).color(0.25f, 0.25f, 1.0f, 1.0f).next();
                bufferBuilder.vertex(i + (double)k, h, j + (double)m).color(0.25f, 0.25f, 1.0f, 0.0f).next();
            }
        }
        for (k = 0; k <= 256; k += 16) {
            double n = (double)k - e;
            bufferBuilder.vertex(i, n, j).color(0.25f, 0.25f, 1.0f, 0.0f).next();
            bufferBuilder.vertex(i, n, j).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            bufferBuilder.vertex(i, n, j + 16.0).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            bufferBuilder.vertex(i + 16.0, n, j + 16.0).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            bufferBuilder.vertex(i + 16.0, n, j).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            bufferBuilder.vertex(i, n, j).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            bufferBuilder.vertex(i, n, j).color(0.25f, 0.25f, 1.0f, 0.0f).next();
        }
        tessellator.draw();
        RenderSystem.lineWidth(1.0f);
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
    }
}

