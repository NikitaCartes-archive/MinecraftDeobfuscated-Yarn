/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Rotation3;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class DrawableHelper {
    public static final Identifier BACKGROUND_LOCATION = new Identifier("textures/gui/options_background.png");
    public static final Identifier STATS_ICON_LOCATION = new Identifier("textures/gui/container/stats_icons.png");
    public static final Identifier GUI_ICONS_LOCATION = new Identifier("textures/gui/icons.png");
    private int zOffset;

    protected void drawHorizontalLine(int x1, int x2, int y, int color) {
        if (x2 < x1) {
            int i = x1;
            x1 = x2;
            x2 = i;
        }
        DrawableHelper.fill(x1, y, x2 + 1, y + 1, color);
    }

    protected void drawVerticalLine(int x, int y1, int y2, int color) {
        if (y2 < y1) {
            int i = y1;
            y1 = y2;
            y2 = i;
        }
        DrawableHelper.fill(x, y1 + 1, x + 1, y2, color);
    }

    public static void fill(int x1, int y1, int x2, int y2, int color) {
        DrawableHelper.fill(Rotation3.identity().getMatrix(), x1, y1, x2, y2, color);
    }

    public static void fill(Matrix4f matrix4f, int x1, int y1, int x2, int y2, int color) {
        int i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }
        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }
        float f = (float)(color >> 24 & 0xFF) / 255.0f;
        float g = (float)(color >> 16 & 0xFF) / 255.0f;
        float h = (float)(color >> 8 & 0xFF) / 255.0f;
        float j = (float)(color & 0xFF) / 255.0f;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix4f, x1, y2, 0.0f).color(g, h, j, f).next();
        bufferBuilder.vertex(matrix4f, x2, y2, 0.0f).color(g, h, j, f).next();
        bufferBuilder.vertex(matrix4f, x2, y1, 0.0f).color(g, h, j, f).next();
        bufferBuilder.vertex(matrix4f, x1, y1, 0.0f).color(g, h, j, f).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    protected void fillGradient(int top, int left, int right, int bottom, int color1, int color2) {
        float f = (float)(color1 >> 24 & 0xFF) / 255.0f;
        float g = (float)(color1 >> 16 & 0xFF) / 255.0f;
        float h = (float)(color1 >> 8 & 0xFF) / 255.0f;
        float i = (float)(color1 & 0xFF) / 255.0f;
        float j = (float)(color2 >> 24 & 0xFF) / 255.0f;
        float k = (float)(color2 >> 16 & 0xFF) / 255.0f;
        float l = (float)(color2 >> 8 & 0xFF) / 255.0f;
        float m = (float)(color2 & 0xFF) / 255.0f;
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(right, left, this.zOffset).color(g, h, i, f).next();
        bufferBuilder.vertex(top, left, this.zOffset).color(g, h, i, f).next();
        bufferBuilder.vertex(top, bottom, this.zOffset).color(k, l, m, j).next();
        bufferBuilder.vertex(right, bottom, this.zOffset).color(k, l, m, j).next();
        tessellator.draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    public void drawCenteredString(TextRenderer textRenderer, String str, int centerX, int y, int color) {
        textRenderer.drawWithShadow(str, centerX - textRenderer.getStringWidth(str) / 2, y, color);
    }

    public void drawString(TextRenderer textRenderer, String str, int x, int y, int color) {
        textRenderer.drawWithShadow(str, x, y, color);
    }

    public static void blit(int x, int y, int z, int width, int height, Sprite sprite) {
        DrawableHelper.innerBlit(x, x + width, y, y + height, z, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
    }

    public void blit(int x, int y, int u, int v, int width, int height) {
        DrawableHelper.blit(x, y, this.zOffset, u, v, width, height, 256, 256);
    }

    public static void blit(int x, int y, int z, float u, float v, int width, int height, int texHeight, int texWidth) {
        DrawableHelper.innerBlit(x, x + width, y, y + height, z, width, height, u, v, texWidth, texHeight);
    }

    public static void blit(int x, int y, int width, int height, float u, float v, int uWidth, int vHeight, int texWidth, int texHeight) {
        DrawableHelper.innerBlit(x, x + width, y, y + height, 0, uWidth, vHeight, u, v, texWidth, texHeight);
    }

    public static void blit(int x, int y, float u, float v, int width, int height, int texWidth, int texHeight) {
        DrawableHelper.blit(x, y, width, height, u, v, width, height, texWidth, texHeight);
    }

    private static void innerBlit(int xStart, int xEnd, int yStart, int yEnd, int z, int width, int height, float u, float v, int texWidth, int texHeight) {
        DrawableHelper.innerBlit(xStart, xEnd, yStart, yEnd, z, (u + 0.0f) / (float)texWidth, (u + (float)width) / (float)texWidth, (v + 0.0f) / (float)texHeight, (v + (float)height) / (float)texHeight);
    }

    protected static void innerBlit(int xStart, int xEnd, int yStart, int yEnd, int z, float uStart, float uEnd, float vStart, float vEnd) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(xStart, yEnd, z).texture(uStart, vEnd).next();
        bufferBuilder.vertex(xEnd, yEnd, z).texture(uEnd, vEnd).next();
        bufferBuilder.vertex(xEnd, yStart, z).texture(uEnd, vStart).next();
        bufferBuilder.vertex(xStart, yStart, z).texture(uStart, vStart).next();
        bufferBuilder.end();
        RenderSystem.enableAlphaTest();
        BufferRenderer.draw(bufferBuilder);
    }

    public int getZOffset() {
        return this.zOffset;
    }

    public void setZOffset(int zOffset) {
        this.zOffset = zOffset;
    }
}

