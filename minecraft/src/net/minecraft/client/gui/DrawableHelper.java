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

@Environment(EnvType.CLIENT)
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

	protected void drawHorizontalLine(int x1, int x2, int y, int color) {
		if (x2 < x1) {
			int i = x1;
			x1 = x2;
			x2 = i;
		}

		fill(x1, y, x2 + 1, y + 1, color);
	}

	protected void drawVerticalLine(int x, int y1, int y2, int color) {
		if (y2 < y1) {
			int i = y1;
			y1 = y2;
			y2 = i;
		}

		fill(x, y1 + 1, x + 1, y2, color);
	}

	public static void fill(int x1, int y1, int x2, int y2, int color) {
		fill(Rotation3.identity().getMatrix(), x1, y1, x2, y2, color);
	}

	public static void fill(Matrix4f matrix, int x1, int y1, int x2, int y2, int color) {
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
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix, (float)x1, (float)y2, 0.0F).color(g, h, j, f).next();
		bufferBuilder.vertex(matrix, (float)x2, (float)y2, 0.0F).color(g, h, j, f).next();
		bufferBuilder.vertex(matrix, (float)x2, (float)y1, 0.0F).color(g, h, j, f).next();
		bufferBuilder.vertex(matrix, (float)x1, (float)y1, 0.0F).color(g, h, j, f).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	protected void fillGradient(int xStart, int yStart, int xEnd, int yEnd, int colorStart, int colorEnd) {
		float f = (float)(colorStart >> 24 & 0xFF) / 255.0F;
		float g = (float)(colorStart >> 16 & 0xFF) / 255.0F;
		float h = (float)(colorStart >> 8 & 0xFF) / 255.0F;
		float i = (float)(colorStart & 0xFF) / 255.0F;
		float j = (float)(colorEnd >> 24 & 0xFF) / 255.0F;
		float k = (float)(colorEnd >> 16 & 0xFF) / 255.0F;
		float l = (float)(colorEnd >> 8 & 0xFF) / 255.0F;
		float m = (float)(colorEnd & 0xFF) / 255.0F;
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.disableAlphaTest();
		RenderSystem.defaultBlendFunc();
		RenderSystem.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex((double)xEnd, (double)yStart, (double)this.zOffset).color(g, h, i, f).next();
		bufferBuilder.vertex((double)xStart, (double)yStart, (double)this.zOffset).color(g, h, i, f).next();
		bufferBuilder.vertex((double)xStart, (double)yEnd, (double)this.zOffset).color(k, l, m, j).next();
		bufferBuilder.vertex((double)xEnd, (double)yEnd, (double)this.zOffset).color(k, l, m, j).next();
		tessellator.draw();
		RenderSystem.shadeModel(7424);
		RenderSystem.disableBlend();
		RenderSystem.enableAlphaTest();
		RenderSystem.enableTexture();
	}

	public void drawCenteredString(TextRenderer textRenderer, String str, int centerX, int y, int color) {
		textRenderer.drawWithShadow(str, (float)(centerX - textRenderer.getStringWidth(str) / 2), (float)y, color);
	}

	public void drawString(TextRenderer textRenderer, String str, int x, int y, int color) {
		textRenderer.drawWithShadow(str, (float)x, (float)y, color);
	}

	public static void drawSprite(int x, int y, int z, int width, int height, Sprite sprite) {
		drawTexturedQuad(x, x + width, y, y + height, z, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
	}

	public void drawTexture(int x, int y, int u, int v, int width, int height) {
		drawTexture(x, y, this.zOffset, (float)u, (float)v, width, height, 256, 256);
	}

	public static void drawTexture(int x, int y, int z, float u, float v, int width, int height, int textureHeight, int textureWidth) {
		drawTexture(x, x + width, y, y + height, z, width, height, u, v, textureWidth, textureHeight);
	}

	public static void drawTexture(int x, int y, int width, int height, float u, float v, int uWidth, int vHeight, int textureWidth, int textureHeight) {
		drawTexture(x, x + width, y, y + height, 0, uWidth, vHeight, u, v, textureWidth, textureHeight);
	}

	public static void drawTexture(int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
		drawTexture(x, y, width, height, u, v, width, height, textureWidth, textureHeight);
	}

	private static void drawTexture(
		int xStart, int xEnd, int yStart, int yEnd, int z, int uWidth, int vHeight, float u, float v, int textureWidth, int textureHeight
	) {
		drawTexturedQuad(
			xStart,
			xEnd,
			yStart,
			yEnd,
			z,
			(u + 0.0F) / (float)textureWidth,
			(u + (float)uWidth) / (float)textureWidth,
			(v + 0.0F) / (float)textureHeight,
			(v + (float)vHeight) / (float)textureHeight
		);
	}

	protected static void drawTexturedQuad(int xStart, int xEnd, int yStart, int yEnd, int z, float uStart, float uEnd, float vStart, float vEnd) {
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex((double)xStart, (double)yEnd, (double)z).texture(uStart, vEnd).next();
		bufferBuilder.vertex((double)xEnd, (double)yEnd, (double)z).texture(uEnd, vEnd).next();
		bufferBuilder.vertex((double)xEnd, (double)yStart, (double)z).texture(uEnd, vStart).next();
		bufferBuilder.vertex((double)xStart, (double)yStart, (double)z).texture(uStart, vStart).next();
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
