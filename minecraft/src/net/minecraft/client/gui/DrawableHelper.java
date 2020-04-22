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

	protected void drawHorizontalLine(MatrixStack matrixStack, int i, int j, int k, int l) {
		if (j < i) {
			int m = i;
			i = j;
			j = m;
		}

		fill(matrixStack, i, k, j + 1, k + 1, l);
	}

	protected void drawVerticalLine(MatrixStack matrixStack, int i, int j, int k, int l) {
		if (k < j) {
			int m = j;
			j = k;
			k = m;
		}

		fill(matrixStack, i, j + 1, i + 1, k, l);
	}

	public static void fill(MatrixStack matrixStack, int i, int j, int k, int l, int m) {
		fill(matrixStack.peek().getModel(), i, j, k, l, m);
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

	protected void fillGradient(MatrixStack matrixStack, int i, int j, int yEnd, int colorStart, int colorEnd, int k) {
		this.method_27533(matrixStack.peek().getModel(), i, j, yEnd, colorStart, colorEnd, k);
	}

	private void method_27533(Matrix4f matrix4f, int i, int j, int k, int l, int m, int n) {
		float f = (float)(m >> 24 & 0xFF) / 255.0F;
		float g = (float)(m >> 16 & 0xFF) / 255.0F;
		float h = (float)(m >> 8 & 0xFF) / 255.0F;
		float o = (float)(m & 0xFF) / 255.0F;
		float p = (float)(n >> 24 & 0xFF) / 255.0F;
		float q = (float)(n >> 16 & 0xFF) / 255.0F;
		float r = (float)(n >> 8 & 0xFF) / 255.0F;
		float s = (float)(n & 0xFF) / 255.0F;
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.disableAlphaTest();
		RenderSystem.defaultBlendFunc();
		RenderSystem.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix4f, (float)k, (float)j, (float)this.zOffset).color(g, h, o, f).next();
		bufferBuilder.vertex(matrix4f, (float)i, (float)j, (float)this.zOffset).color(g, h, o, f).next();
		bufferBuilder.vertex(matrix4f, (float)i, (float)l, (float)this.zOffset).color(q, r, s, p).next();
		bufferBuilder.vertex(matrix4f, (float)k, (float)l, (float)this.zOffset).color(q, r, s, p).next();
		tessellator.draw();
		RenderSystem.shadeModel(7424);
		RenderSystem.disableBlend();
		RenderSystem.enableAlphaTest();
		RenderSystem.enableTexture();
	}

	public void drawCenteredString(MatrixStack matrixStack, TextRenderer textRenderer, String string, int i, int j, int k) {
		textRenderer.drawWithShadow(matrixStack, string, (float)(i - textRenderer.getWidth(string) / 2), (float)j, k);
	}

	public void method_27534(MatrixStack matrixStack, TextRenderer textRenderer, Text text, int i, int j, int k) {
		textRenderer.drawWithShadow(matrixStack, text, (float)(i - textRenderer.getWidth(text) / 2), (float)j, k);
	}

	public void drawString(MatrixStack matrixStack, TextRenderer textRenderer, String string, int i, int j, int k) {
		textRenderer.drawWithShadow(matrixStack, string, (float)i, (float)j, k);
	}

	public void method_27535(MatrixStack matrixStack, TextRenderer textRenderer, Text text, int i, int j, int k) {
		textRenderer.drawWithShadow(matrixStack, text, (float)i, (float)j, k);
	}

	public static void drawSprite(MatrixStack matrixStack, int i, int j, int k, int l, int m, Sprite sprite) {
		drawTexturedQuad(matrixStack.peek().getModel(), i, i + l, j, j + m, k, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
	}

	public void drawTexture(MatrixStack matrixStack, int i, int j, int k, int l, int m, int n) {
		drawTexture(matrixStack, i, j, this.zOffset, (float)k, (float)l, m, n, 256, 256);
	}

	public static void drawTexture(MatrixStack matrixStack, int i, int j, int k, float f, float g, int l, int m, int n, int o) {
		drawTexture(matrixStack, i, i + l, j, j + m, k, l, m, f, g, o, n);
	}

	public static void drawTexture(MatrixStack matrixStack, int i, int j, int k, int l, float f, float g, int m, int n, int o, int p) {
		drawTexture(matrixStack, i, i + k, j, j + l, 0, m, n, f, g, o, p);
	}

	public static void drawTexture(MatrixStack matrixStack, int i, int j, float f, float g, int k, int l, int m, int n) {
		drawTexture(matrixStack, i, j, k, l, f, g, k, l, m, n);
	}

	private static void drawTexture(MatrixStack matrixStack, int i, int j, int k, int l, int m, int n, int o, float f, float g, int p, int q) {
		drawTexturedQuad(
			matrixStack.peek().getModel(), i, j, k, l, m, (f + 0.0F) / (float)p, (f + (float)n) / (float)p, (g + 0.0F) / (float)q, (g + (float)o) / (float)q
		);
	}

	private static void drawTexturedQuad(Matrix4f matrix4f, int i, int j, int k, int l, int m, float f, float g, float h, float n) {
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(matrix4f, (float)i, (float)l, (float)m).texture(f, n).next();
		bufferBuilder.vertex(matrix4f, (float)j, (float)l, (float)m).texture(g, n).next();
		bufferBuilder.vertex(matrix4f, (float)j, (float)k, (float)m).texture(g, h).next();
		bufferBuilder.vertex(matrix4f, (float)i, (float)k, (float)m).texture(f, h).next();
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
