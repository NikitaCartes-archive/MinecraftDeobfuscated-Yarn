package net.minecraft.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5481;
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

	protected void drawHorizontalLine(MatrixStack matrices, int x1, int x2, int y, int color) {
		if (x2 < x1) {
			int i = x1;
			x1 = x2;
			x2 = i;
		}

		fill(matrices, x1, y, x2 + 1, y + 1, color);
	}

	protected void drawVerticalLine(MatrixStack matrices, int x, int y1, int y2, int color) {
		if (y2 < y1) {
			int i = y1;
			y1 = y2;
			y2 = i;
		}

		fill(matrices, x, y1 + 1, x + 1, y2, color);
	}

	public static void fill(MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
		fill(matrices.peek().getModel(), x1, y1, x2, y2, color);
	}

	private static void fill(Matrix4f matrix, int x1, int y1, int x2, int y2, int color) {
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

	protected void fillGradient(MatrixStack matrices, int xStart, int yStart, int xEnd, int yEnd, int colorStart, int colorEnd) {
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.disableAlphaTest();
		RenderSystem.defaultBlendFunc();
		RenderSystem.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		fillGradient(matrices.peek().getModel(), bufferBuilder, xStart, yStart, xEnd, yEnd, this.zOffset, colorStart, colorEnd);
		tessellator.draw();
		RenderSystem.shadeModel(7424);
		RenderSystem.disableBlend();
		RenderSystem.enableAlphaTest();
		RenderSystem.enableTexture();
	}

	protected static void fillGradient(Matrix4f matrix4f, BufferBuilder bufferBuilder, int xStart, int yStart, int xEnd, int yEnd, int i, int j, int k) {
		float f = (float)(j >> 24 & 0xFF) / 255.0F;
		float g = (float)(j >> 16 & 0xFF) / 255.0F;
		float h = (float)(j >> 8 & 0xFF) / 255.0F;
		float l = (float)(j & 0xFF) / 255.0F;
		float m = (float)(k >> 24 & 0xFF) / 255.0F;
		float n = (float)(k >> 16 & 0xFF) / 255.0F;
		float o = (float)(k >> 8 & 0xFF) / 255.0F;
		float p = (float)(k & 0xFF) / 255.0F;
		bufferBuilder.vertex(matrix4f, (float)xEnd, (float)yStart, (float)i).color(g, h, l, f).next();
		bufferBuilder.vertex(matrix4f, (float)xStart, (float)yStart, (float)i).color(g, h, l, f).next();
		bufferBuilder.vertex(matrix4f, (float)xStart, (float)yEnd, (float)i).color(n, o, p, m).next();
		bufferBuilder.vertex(matrix4f, (float)xEnd, (float)yEnd, (float)i).color(n, o, p, m).next();
	}

	public static void drawCenteredString(MatrixStack matrixStack, TextRenderer textRenderer, String string, int i, int j, int k) {
		textRenderer.drawWithShadow(matrixStack, string, (float)(i - textRenderer.getWidth(string) / 2), (float)j, k);
	}

	public static void drawCenteredText(MatrixStack matrixStack, TextRenderer textRenderer, Text text, int i, int j, int k) {
		class_5481 lv = text.method_30937();
		textRenderer.drawWithShadow(matrixStack, lv, (float)(i - textRenderer.method_30880(lv) / 2), (float)j, k);
	}

	public static void drawStringWithShadow(MatrixStack matrixStack, TextRenderer textRenderer, String string, int i, int j, int k) {
		textRenderer.drawWithShadow(matrixStack, string, (float)i, (float)j, k);
	}

	public static void drawTextWithShadow(MatrixStack matrixStack, TextRenderer textRenderer, Text text, int i, int j, int k) {
		textRenderer.method_30881(matrixStack, text, (float)i, (float)j, k);
	}

	public void method_29343(int i, int j, BiConsumer<Integer, Integer> biConsumer) {
		RenderSystem.blendFuncSeparate(
			GlStateManager.SrcFactor.ZERO,
			GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
			GlStateManager.SrcFactor.SRC_ALPHA,
			GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA
		);
		biConsumer.accept(i + 1, j);
		biConsumer.accept(i - 1, j);
		biConsumer.accept(i, j + 1);
		biConsumer.accept(i, j - 1);
		RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
		biConsumer.accept(i, j);
	}

	public static void drawSprite(MatrixStack matrices, int x, int y, int z, int width, int height, Sprite sprite) {
		drawTexturedQuad(matrices.peek().getModel(), x, x + width, y, y + height, z, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
	}

	/**
	 * Draws a textured rectangle from a region in a 256x256 texture.
	 * 
	 * <p>The Z coordinate of the rectangle is {@link #zOffset}.
	 * 
	 * <p>The width and height of the region are the same as
	 * the dimensions of the rectangle.
	 * 
	 * @param matrices the matrix stack used for rendering
	 * @param x the X coordinate of the rectangle
	 * @param y the Y coordinate of the rectangle
	 * @param u the left-most coordinate of the texture region
	 * @param v the top-most coordinate of the texture region
	 * @param width the width
	 * @param height the height
	 */
	public void drawTexture(MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
		drawTexture(matrices, x, y, this.zOffset, (float)u, (float)v, width, height, 256, 256);
	}

	/**
	 * Draws a textured rectangle from a region in a texture.
	 * 
	 * <p>The width and height of the region are the same as
	 * the dimensions of the rectangle.
	 * 
	 * @param matrices the matrix stack used for rendering
	 * @param x the X coordinate of the rectangle
	 * @param y the Y coordinate of the rectangle
	 * @param z the Z coordinate of the rectangle
	 * @param u the left-most coordinate of the texture region
	 * @param v the top-most coordinate of the texture region
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 * @param textureHeight the height of the entire texture
	 * @param textureWidth the width of the entire texture
	 */
	public static void drawTexture(MatrixStack matrices, int x, int y, int z, float u, float v, int width, int height, int textureHeight, int textureWidth) {
		drawTexture(matrices, x, x + width, y, y + height, z, width, height, u, v, textureWidth, textureHeight);
	}

	/**
	 * Draws a textured rectangle from a region in a texture.
	 * 
	 * @param matrices the matrix stack used for rendering
	 * @param x the X coordinate of the rectangle
	 * @param y the Y coordinate of the rectangle
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 * @param u the left-most coordinate of the texture region
	 * @param v the top-most coordinate of the texture region
	 * @param regionWidth the width of the texture region
	 * @param regionHeight the height of the texture region
	 * @param textureWidth the width of the entire texture
	 * @param textureHeight the height of the entire texture
	 */
	public static void drawTexture(
		MatrixStack matrices, int x, int y, int width, int height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight
	) {
		drawTexture(matrices, x, x + width, y, y + height, 0, regionWidth, regionHeight, u, v, textureWidth, textureHeight);
	}

	/**
	 * Draws a textured rectangle from a region in a texture.
	 * 
	 * <p>The width and height of the region are the same as
	 * the dimensions of the rectangle.
	 * 
	 * @param matrices the matrix stack used for rendering
	 * @param x the X coordinate of the rectangle
	 * @param y the Y coordinate of the rectangle
	 * @param u the left-most coordinate of the texture region
	 * @param v the top-most coordinate of the texture region
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 * @param textureWidth the width of the entire texture
	 * @param textureHeight the height of the entire texture
	 */
	public static void drawTexture(MatrixStack matrices, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
		drawTexture(matrices, x, y, width, height, u, v, width, height, textureWidth, textureHeight);
	}

	private static void drawTexture(
		MatrixStack matrices, int x0, int y0, int x1, int y1, int z, int regionWidth, int regionHeight, float u, float v, int textureWidth, int textureHeight
	) {
		drawTexturedQuad(
			matrices.peek().getModel(),
			x0,
			y0,
			x1,
			y1,
			z,
			(u + 0.0F) / (float)textureWidth,
			(u + (float)regionWidth) / (float)textureWidth,
			(v + 0.0F) / (float)textureHeight,
			(v + (float)regionHeight) / (float)textureHeight
		);
	}

	private static void drawTexturedQuad(Matrix4f matrices, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1) {
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(matrices, (float)x0, (float)y1, (float)z).texture(u0, v1).next();
		bufferBuilder.vertex(matrices, (float)x1, (float)y1, (float)z).texture(u1, v1).next();
		bufferBuilder.vertex(matrices, (float)x1, (float)y0, (float)z).texture(u1, v0).next();
		bufferBuilder.vertex(matrices, (float)x0, (float)y0, (float)z).texture(u0, v0).next();
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
