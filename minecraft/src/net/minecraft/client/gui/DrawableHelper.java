package net.minecraft.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
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

	public static void fill(int i, int j, int k, int l, int m) {
		if (i < k) {
			int n = i;
			i = k;
			k = n;
		}

		if (j < l) {
			int n = j;
			j = l;
			l = n;
		}

		float f = (float)(m >> 24 & 0xFF) / 255.0F;
		float g = (float)(m >> 16 & 0xFF) / 255.0F;
		float h = (float)(m >> 8 & 0xFF) / 255.0F;
		float o = (float)(m & 0xFF) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.blendFuncSeparate(
			GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO
		);
		RenderSystem.color4f(g, h, o, f);
		bufferBuilder.begin(7, VertexFormats.POSITION);
		bufferBuilder.vertex((double)i, (double)l, 0.0).next();
		bufferBuilder.vertex((double)k, (double)l, 0.0).next();
		bufferBuilder.vertex((double)k, (double)j, 0.0).next();
		bufferBuilder.vertex((double)i, (double)j, 0.0).next();
		tessellator.draw();
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	protected void fillGradient(int i, int j, int k, int l, int m, int n) {
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
		RenderSystem.blendFuncSeparate(
			GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO
		);
		RenderSystem.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex((double)k, (double)j, (double)this.blitOffset).color(g, h, o, f).next();
		bufferBuilder.vertex((double)i, (double)j, (double)this.blitOffset).color(g, h, o, f).next();
		bufferBuilder.vertex((double)i, (double)l, (double)this.blitOffset).color(q, r, s, p).next();
		bufferBuilder.vertex((double)k, (double)l, (double)this.blitOffset).color(q, r, s, p).next();
		tessellator.draw();
		RenderSystem.shadeModel(7424);
		RenderSystem.disableBlend();
		RenderSystem.enableAlphaTest();
		RenderSystem.enableTexture();
	}

	public void drawCenteredString(TextRenderer textRenderer, String string, int i, int j, int k) {
		textRenderer.drawWithShadow(string, (float)(i - textRenderer.getStringWidth(string) / 2), (float)j, k);
	}

	public void drawRightAlignedString(TextRenderer textRenderer, String string, int i, int j, int k) {
		textRenderer.drawWithShadow(string, (float)(i - textRenderer.getStringWidth(string)), (float)j, k);
	}

	public void drawString(TextRenderer textRenderer, String string, int i, int j, int k) {
		textRenderer.drawWithShadow(string, (float)i, (float)j, k);
	}

	public static void blit(int i, int j, int k, int l, int m, Sprite sprite) {
		innerBlit(i, i + l, j, j + m, k, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
	}

	public void blit(int i, int j, int k, int l, int m, int n) {
		blit(i, j, this.blitOffset, (float)k, (float)l, m, n, 256, 256);
	}

	public static void blit(int i, int j, int k, float f, float g, int l, int m, int n, int o) {
		innerBlit(i, i + l, j, j + m, k, l, m, f, g, o, n);
	}

	public static void blit(int i, int j, int k, int l, float f, float g, int m, int n, int o, int p) {
		innerBlit(i, i + k, j, j + l, 0, m, n, f, g, o, p);
	}

	public static void blit(int i, int j, float f, float g, int k, int l, int m, int n) {
		blit(i, j, k, l, f, g, k, l, m, n);
	}

	private static void innerBlit(int i, int j, int k, int l, int m, int n, int o, float f, float g, int p, int q) {
		innerBlit(i, j, k, l, m, (f + 0.0F) / (float)p, (f + (float)n) / (float)p, (g + 0.0F) / (float)q, (g + (float)o) / (float)q);
	}

	protected static void innerBlit(int i, int j, int k, int l, int m, float f, float g, float h, float n) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.begin(7, VertexFormats.POSITION_UV);
		bufferBuilder.vertex((double)i, (double)l, (double)m).texture((double)f, (double)n).next();
		bufferBuilder.vertex((double)j, (double)l, (double)m).texture((double)g, (double)n).next();
		bufferBuilder.vertex((double)j, (double)k, (double)m).texture((double)g, (double)h).next();
		bufferBuilder.vertex((double)i, (double)k, (double)m).texture((double)f, (double)h).next();
		tessellator.draw();
	}
}
