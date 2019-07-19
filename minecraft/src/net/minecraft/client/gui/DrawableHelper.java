package net.minecraft.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class DrawableHelper {
	public static final Identifier BACKGROUND_LOCATION = new Identifier("textures/gui/options_background.png");
	public static final Identifier STATS_ICON_LOCATION = new Identifier("textures/gui/container/stats_icons.png");
	public static final Identifier GUI_ICONS_LOCATION = new Identifier("textures/gui/icons.png");
	protected int blitOffset;

	protected void hLine(int i, int j, int k, int l) {
		if (j < i) {
			int m = i;
			i = j;
			j = m;
		}

		fill(i, k, j + 1, k + 1, l);
	}

	protected void vLine(int i, int j, int k, int l) {
		if (k < j) {
			int m = j;
			j = k;
			k = m;
		}

		fill(i, j + 1, i + 1, k, l);
	}

	public static void fill(int x1, int y1, int x2, int y2, int color) {
		if (x1 < x2) {
			int i = x1;
			x1 = x2;
			x2 = i;
		}

		if (y1 < y2) {
			int i = y1;
			y1 = y2;
			y2 = i;
		}

		float f = (float)(color >> 24 & 0xFF) / 255.0F;
		float g = (float)(color >> 16 & 0xFF) / 255.0F;
		float h = (float)(color >> 8 & 0xFF) / 255.0F;
		float j = (float)(color & 0xFF) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.color4f(g, h, j, f);
		bufferBuilder.begin(7, VertexFormats.POSITION);
		bufferBuilder.vertex((double)x1, (double)y2, 0.0).next();
		bufferBuilder.vertex((double)x2, (double)y2, 0.0).next();
		bufferBuilder.vertex((double)x2, (double)y1, 0.0).next();
		bufferBuilder.vertex((double)x1, (double)y1, 0.0).next();
		tessellator.draw();
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}

	protected void fillGradient(int top, int left, int right, int bottom, int color1, int color2) {
		float f = (float)(color1 >> 24 & 0xFF) / 255.0F;
		float g = (float)(color1 >> 16 & 0xFF) / 255.0F;
		float h = (float)(color1 >> 8 & 0xFF) / 255.0F;
		float i = (float)(color1 & 0xFF) / 255.0F;
		float j = (float)(color2 >> 24 & 0xFF) / 255.0F;
		float k = (float)(color2 >> 16 & 0xFF) / 255.0F;
		float l = (float)(color2 >> 8 & 0xFF) / 255.0F;
		float m = (float)(color2 & 0xFF) / 255.0F;
		GlStateManager.disableTexture();
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex((double)right, (double)left, (double)this.blitOffset).color(g, h, i, f).next();
		bufferBuilder.vertex((double)top, (double)left, (double)this.blitOffset).color(g, h, i, f).next();
		bufferBuilder.vertex((double)top, (double)bottom, (double)this.blitOffset).color(k, l, m, j).next();
		bufferBuilder.vertex((double)right, (double)bottom, (double)this.blitOffset).color(k, l, m, j).next();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
		GlStateManager.enableTexture();
	}

	public void drawCenteredString(TextRenderer textRenderer, String str, int centerX, int y, int color) {
		textRenderer.drawWithShadow(str, (float)(centerX - textRenderer.getStringWidth(str) / 2), (float)y, color);
	}

	public void drawRightAlignedString(TextRenderer textRenderer, String str, int rightX, int y, int color) {
		textRenderer.drawWithShadow(str, (float)(rightX - textRenderer.getStringWidth(str)), (float)y, color);
	}

	public void drawString(TextRenderer textRenderer, String str, int x, int y, int color) {
		textRenderer.drawWithShadow(str, (float)x, (float)y, color);
	}

	public static void blit(int x, int y, int z, int width, int height, Sprite sprite) {
		innerBlit(x, x + width, y, y + height, z, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
	}

	public void blit(int x, int y, int u, int v, int width, int height) {
		blit(x, y, this.blitOffset, (float)u, (float)v, width, height, 256, 256);
	}

	public static void blit(int x, int y, int z, float u, float v, int width, int height, int texHeight, int texWidth) {
		innerBlit(x, x + width, y, y + height, z, width, height, u, v, texWidth, texHeight);
	}

	public static void blit(int x, int y, int width, int height, float u, float v, int uWidth, int vHeight, int texWidth, int texHeight) {
		innerBlit(x, x + width, y, y + height, 0, uWidth, vHeight, u, v, texWidth, texHeight);
	}

	public static void blit(int x, int y, float u, float v, int width, int height, int texWidth, int texHeight) {
		blit(x, y, width, height, u, v, width, height, texWidth, texHeight);
	}

	private static void innerBlit(int xStart, int xEnd, int yStart, int yEnd, int z, int width, int height, float u, float v, int texWidth, int texHeight) {
		innerBlit(
			xStart,
			xEnd,
			yStart,
			yEnd,
			z,
			(u + 0.0F) / (float)texWidth,
			(u + (float)width) / (float)texWidth,
			(v + 0.0F) / (float)texHeight,
			(v + (float)height) / (float)texHeight
		);
	}

	protected static void innerBlit(int xStart, int xEnd, int yStart, int yEnd, int z, float uStart, float uEnd, float vStart, float vEnd) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex((double)xStart, (double)yEnd, (double)z).texture((double)uStart, (double)vEnd).next();
		bufferBuilder.vertex((double)xEnd, (double)yEnd, (double)z).texture((double)uEnd, (double)vEnd).next();
		bufferBuilder.vertex((double)xEnd, (double)yStart, (double)z).texture((double)uEnd, (double)vStart).next();
		bufferBuilder.vertex((double)xStart, (double)yStart, (double)z).texture((double)uStart, (double)vStart).next();
		tessellator.draw();
	}
}
