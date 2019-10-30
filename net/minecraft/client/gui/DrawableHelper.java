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
    private int blitOffset;

    protected void hLine(int i, int j, int k, int l) {
        if (j < i) {
            int m = i;
            i = j;
            j = m;
        }
        DrawableHelper.fill(i, k, j + 1, k + 1, l);
    }

    protected void vLine(int i, int j, int k, int l) {
        if (k < j) {
            int m = j;
            j = k;
            k = m;
        }
        DrawableHelper.fill(i, j + 1, i + 1, k, l);
    }

    public static void fill(int i, int j, int k, int l, int m) {
        DrawableHelper.fill(Rotation3.identity().getMatrix(), i, j, k, l, m);
    }

    public static void fill(Matrix4f matrix4f, int i, int j, int k, int l, int m) {
        int n;
        if (i < k) {
            n = i;
            i = k;
            k = n;
        }
        if (j < l) {
            n = j;
            j = l;
            l = n;
        }
        float f = (float)(m >> 24 & 0xFF) / 255.0f;
        float g = (float)(m >> 16 & 0xFF) / 255.0f;
        float h = (float)(m >> 8 & 0xFF) / 255.0f;
        float o = (float)(m & 0xFF) / 255.0f;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix4f, i, l, 0.0f).color(g, h, o, f).next();
        bufferBuilder.vertex(matrix4f, k, l, 0.0f).color(g, h, o, f).next();
        bufferBuilder.vertex(matrix4f, k, j, 0.0f).color(g, h, o, f).next();
        bufferBuilder.vertex(matrix4f, i, j, 0.0f).color(g, h, o, f).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    protected void fillGradient(int i, int j, int k, int l, int m, int n) {
        float f = (float)(m >> 24 & 0xFF) / 255.0f;
        float g = (float)(m >> 16 & 0xFF) / 255.0f;
        float h = (float)(m >> 8 & 0xFF) / 255.0f;
        float o = (float)(m & 0xFF) / 255.0f;
        float p = (float)(n >> 24 & 0xFF) / 255.0f;
        float q = (float)(n >> 16 & 0xFF) / 255.0f;
        float r = (float)(n >> 8 & 0xFF) / 255.0f;
        float s = (float)(n & 0xFF) / 255.0f;
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(k, j, this.blitOffset).color(g, h, o, f).next();
        bufferBuilder.vertex(i, j, this.blitOffset).color(g, h, o, f).next();
        bufferBuilder.vertex(i, l, this.blitOffset).color(q, r, s, p).next();
        bufferBuilder.vertex(k, l, this.blitOffset).color(q, r, s, p).next();
        tessellator.draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    public void drawCenteredString(TextRenderer textRenderer, String string, int i, int j, int k) {
        textRenderer.drawWithShadow(string, i - textRenderer.getStringWidth(string) / 2, j, k);
    }

    public void drawRightAlignedString(TextRenderer textRenderer, String string, int i, int j, int k) {
        textRenderer.drawWithShadow(string, i - textRenderer.getStringWidth(string), j, k);
    }

    public void drawString(TextRenderer textRenderer, String string, int i, int j, int k) {
        textRenderer.drawWithShadow(string, i, j, k);
    }

    public static void blit(int i, int j, int k, int l, int m, Sprite sprite) {
        DrawableHelper.innerBlit(i, i + l, j, j + m, k, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
    }

    public void blit(int i, int j, int k, int l, int m, int n) {
        DrawableHelper.blit(i, j, this.blitOffset, k, l, m, n, 256, 256);
    }

    public static void blit(int i, int j, int k, float f, float g, int l, int m, int n, int o) {
        DrawableHelper.innerBlit(i, i + l, j, j + m, k, l, m, f, g, o, n);
    }

    public static void blit(int i, int j, int k, int l, float f, float g, int m, int n, int o, int p) {
        DrawableHelper.innerBlit(i, i + k, j, j + l, 0, m, n, f, g, o, p);
    }

    public static void blit(int i, int j, float f, float g, int k, int l, int m, int n) {
        DrawableHelper.blit(i, j, k, l, f, g, k, l, m, n);
    }

    private static void innerBlit(int i, int j, int k, int l, int m, int n, int o, float f, float g, int p, int q) {
        DrawableHelper.innerBlit(i, j, k, l, m, (f + 0.0f) / (float)p, (f + (float)n) / (float)p, (g + 0.0f) / (float)q, (g + (float)o) / (float)q);
    }

    protected static void innerBlit(int i, int j, int k, int l, int m, float f, float g, float h, float n) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(i, l, m).texture(f, n).next();
        bufferBuilder.vertex(j, l, m).texture(g, n).next();
        bufferBuilder.vertex(j, k, m).texture(g, h).next();
        bufferBuilder.vertex(i, k, m).texture(f, h).next();
        bufferBuilder.end();
        RenderSystem.enableAlphaTest();
        BufferRenderer.draw(bufferBuilder);
    }

    public int getBlitOffset() {
        return this.blitOffset;
    }

    public void setBlitOffset(int i) {
        this.blitOffset = i;
    }
}

