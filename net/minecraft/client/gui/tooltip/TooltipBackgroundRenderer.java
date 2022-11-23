/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.tooltip;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import org.joml.Matrix4f;

/**
 * A class for rendering a background box for a tooltip.
 */
@Environment(value=EnvType.CLIENT)
public class TooltipBackgroundRenderer {
    public static final int field_41688 = 12;
    private static final int field_41693 = 3;
    public static final int field_41689 = 3;
    public static final int field_41690 = 3;
    public static final int field_41691 = 3;
    public static final int field_41692 = 3;
    private static final int BACKGROUND_COLOR = -267386864;
    private static final int START_Y_BORDER_COLOR = 0x505000FF;
    private static final int END_Y_BORDER_COLOR = 1344798847;

    public static void render(RectangleRenderer renderer, Matrix4f matrix, BufferBuilder buffer, int x, int y, int width, int height, int z) {
        int i = x - 3;
        int j = y - 3;
        int k = width + 3 + 3;
        int l = height + 3 + 3;
        TooltipBackgroundRenderer.renderHorizontalLine(renderer, matrix, buffer, i, j - 1, k, z, -267386864);
        TooltipBackgroundRenderer.renderHorizontalLine(renderer, matrix, buffer, i, j + l, k, z, -267386864);
        TooltipBackgroundRenderer.renderRectangle(renderer, matrix, buffer, i, j, k, l, z, -267386864);
        TooltipBackgroundRenderer.renderVerticalLine(renderer, matrix, buffer, i - 1, j, l, z, -267386864);
        TooltipBackgroundRenderer.renderVerticalLine(renderer, matrix, buffer, i + k, j, l, z, -267386864);
        TooltipBackgroundRenderer.renderBorder(renderer, matrix, buffer, i, j + 1, k, l, z, 0x505000FF, 1344798847);
    }

    private static void renderBorder(RectangleRenderer renderer, Matrix4f matrix, BufferBuilder buffer, int x, int y, int width, int height, int z, int startYColor, int endYColor) {
        TooltipBackgroundRenderer.renderVerticalLine(renderer, matrix, buffer, x, y, height - 2, z, startYColor, endYColor);
        TooltipBackgroundRenderer.renderVerticalLine(renderer, matrix, buffer, x + width - 1, y, height - 2, z, startYColor, endYColor);
        TooltipBackgroundRenderer.renderHorizontalLine(renderer, matrix, buffer, x, y - 1, width, z, startYColor);
        TooltipBackgroundRenderer.renderHorizontalLine(renderer, matrix, buffer, x, y - 1 + height - 1, width, z, endYColor);
    }

    private static void renderVerticalLine(RectangleRenderer renderer, Matrix4f matrix, BufferBuilder buffer, int x, int y, int height, int z, int color) {
        renderer.blit(matrix, buffer, x, y, x + 1, y + height, z, color, color);
    }

    private static void renderVerticalLine(RectangleRenderer renderer, Matrix4f matrix, BufferBuilder buffer, int x, int y, int height, int z, int startColor, int endColor) {
        renderer.blit(matrix, buffer, x, y, x + 1, y + height, z, startColor, endColor);
    }

    private static void renderHorizontalLine(RectangleRenderer renderer, Matrix4f matrix, BufferBuilder buffer, int x, int y, int width, int z, int color) {
        renderer.blit(matrix, buffer, x, y, x + width, y + 1, z, color, color);
    }

    private static void renderRectangle(RectangleRenderer renderer, Matrix4f matrix, BufferBuilder buffer, int x, int y, int width, int height, int z, int color) {
        renderer.blit(matrix, buffer, x, y, x + width, y + height, z, color, color);
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface RectangleRenderer {
        public void blit(Matrix4f var1, BufferBuilder var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9);
    }
}

