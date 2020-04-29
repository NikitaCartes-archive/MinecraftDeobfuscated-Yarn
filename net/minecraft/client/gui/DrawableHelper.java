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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public abstract class DrawableHelper {
    /**
     * The texture used by options for background.
     */
    public static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/options_background.png");
    /**
     * The texture of icons used in the stats screen.
     */
    public static final Identifier STATS_ICON_TEXTURE = new Identifier("textures/gui/container/stats_icons.png");
    /**
     * The texture of various icons and widgets used for rendering ingame indicators.
     */
    public static final Identifier GUI_ICONS_TEXTURE = new Identifier("textures/gui/icons.png");
    /**
     * The z offset used by {@link DrawableHelper}.
     */
    private int zOffset;

    protected void drawHorizontalLine(MatrixStack matrices, int x1, int x2, int y, int color) {
        if (x2 < x1) {
            int i = x1;
            x1 = x2;
            x2 = i;
        }
        DrawableHelper.fill(matrices, x1, y, x2 + 1, y + 1, color);
    }

    protected void drawVerticalLine(MatrixStack matrices, int x, int y1, int y2, int color) {
        if (y2 < y1) {
            int i = y1;
            y1 = y2;
            y2 = i;
        }
        DrawableHelper.fill(matrices, x, y1 + 1, x + 1, y2, color);
    }

    public static void fill(MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
        DrawableHelper.fill(matrices.peek().getModel(), x1, y1, x2, y2, color);
    }

    private static void fill(Matrix4f matrix, int x1, int y1, int x2, int y2, int color) {
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
        bufferBuilder.vertex(matrix, x1, y2, 0.0f).color(g, h, j, f).next();
        bufferBuilder.vertex(matrix, x2, y2, 0.0f).color(g, h, j, f).next();
        bufferBuilder.vertex(matrix, x2, y1, 0.0f).color(g, h, j, f).next();
        bufferBuilder.vertex(matrix, x1, y1, 0.0f).color(g, h, j, f).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    protected void fillGradient(MatrixStack matrices, int xStart, int yStart, int xEnd, int yEnd, int colorStart, int colorEnd) {
        this.fillGradient(matrices.peek().getModel(), xStart, yStart, xEnd, yEnd, colorStart, colorEnd);
    }

    private void fillGradient(Matrix4f matrix, int xStart, int yStart, int xEnd, int yEnd, int colorStart, int colorEnd) {
        float f = (float)(colorStart >> 24 & 0xFF) / 255.0f;
        float g = (float)(colorStart >> 16 & 0xFF) / 255.0f;
        float h = (float)(colorStart >> 8 & 0xFF) / 255.0f;
        float i = (float)(colorStart & 0xFF) / 255.0f;
        float j = (float)(colorEnd >> 24 & 0xFF) / 255.0f;
        float k = (float)(colorEnd >> 16 & 0xFF) / 255.0f;
        float l = (float)(colorEnd >> 8 & 0xFF) / 255.0f;
        float m = (float)(colorEnd & 0xFF) / 255.0f;
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, xEnd, yStart, this.zOffset).color(g, h, i, f).next();
        bufferBuilder.vertex(matrix, xStart, yStart, this.zOffset).color(g, h, i, f).next();
        bufferBuilder.vertex(matrix, xStart, yEnd, this.zOffset).color(k, l, m, j).next();
        bufferBuilder.vertex(matrix, xEnd, yEnd, this.zOffset).color(k, l, m, j).next();
        tessellator.draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    public void drawCenteredString(MatrixStack matrices, TextRenderer textRenderer, String text, int x, int y, int i) {
        textRenderer.drawWithShadow(matrices, text, (float)(x - textRenderer.getWidth(text) / 2), (float)y, i);
    }

    public void drawStringWithShadow(MatrixStack matrices, TextRenderer textRenderer, Text text, int i, int j, int k) {
        textRenderer.drawWithShadow(matrices, text, (float)(i - textRenderer.getStringWidth(text) / 2), (float)j, k);
    }

    public void drawString(MatrixStack matrices, TextRenderer textRenderer, String text, int i, int j, int k) {
        textRenderer.drawWithShadow(matrices, text, (float)i, (float)j, k);
    }

    public void method_27535(MatrixStack matrices, TextRenderer textRenderer, Text text, int i, int j, int k) {
        textRenderer.drawWithShadow(matrices, text, (float)i, (float)j, k);
    }

    public static void drawSprite(MatrixStack matrices, int i, int j, int k, int l, int m, Sprite sprite) {
        DrawableHelper.drawTexturedQuad(matrices.peek().getModel(), i, i + l, j, j + m, k, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
    }

    public void drawTexture(MatrixStack matrices, int x, int y, int i, int j, int k, int l) {
        DrawableHelper.drawTexture(matrices, x, y, this.zOffset, i, j, k, l, 256, 256);
    }

    public static void drawTexture(MatrixStack matrices, int i, int j, int k, float f, float g, int l, int m, int n, int o) {
        DrawableHelper.drawTexture(matrices, i, i + l, j, j + m, k, l, m, f, g, o, n);
    }

    public static void drawTexture(MatrixStack matrices, int i, int j, int k, int l, float f, float g, int m, int n, int o, int p) {
        DrawableHelper.drawTexture(matrices, i, i + k, j, j + l, 0, m, n, f, g, o, p);
    }

    public static void drawTexture(MatrixStack matrices, int i, int j, float f, float g, int k, int l, int m, int n) {
        DrawableHelper.drawTexture(matrices, i, j, k, l, f, g, k, l, m, n);
    }

    private static void drawTexture(MatrixStack matrices, int i, int j, int k, int l, int m, int n, int o, float f, float g, int p, int q) {
        DrawableHelper.drawTexturedQuad(matrices.peek().getModel(), i, j, k, l, m, (f + 0.0f) / (float)p, (f + (float)n) / (float)p, (g + 0.0f) / (float)q, (g + (float)o) / (float)q);
    }

    private static void drawTexturedQuad(Matrix4f matrices, int i, int j, int k, int l, int m, float f, float g, float h, float n) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrices, i, l, m).texture(f, n).next();
        bufferBuilder.vertex(matrices, j, l, m).texture(g, n).next();
        bufferBuilder.vertex(matrices, j, k, m).texture(g, h).next();
        bufferBuilder.vertex(matrices, i, k, m).texture(f, h).next();
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

