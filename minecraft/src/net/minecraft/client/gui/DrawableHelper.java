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
		fill(Rotation3.identity().getMatrix(), i, j, k, l, m);
	}

	public static void fill(Matrix4f matrix4f, int i, int j, int k, int l, int m) {
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
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBufferBuilder();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix4f, (float)i, (float)l, 0.0F).color(g, h, o, f).next();
		bufferBuilder.vertex(matrix4f, (float)k, (float)l, 0.0F).color(g, h, o, f).next();
		bufferBuilder.vertex(matrix4f, (float)k, (float)j, 0.0F).color(g, h, o, f).next();
		bufferBuilder.vertex(matrix4f, (float)i, (float)j, 0.0F).color(g, h, o, f).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
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
		RenderSystem.defaultBlendFunc();
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
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBufferBuilder();
		bufferBuilder.begin(7, VertexFormats.POSITION_UV);
		bufferBuilder.vertex((double)i, (double)l, (double)m).texture(f, n).next();
		bufferBuilder.vertex((double)j, (double)l, (double)m).texture(g, n).next();
		bufferBuilder.vertex((double)j, (double)k, (double)m).texture(g, h).next();
		bufferBuilder.vertex((double)i, (double)k, (double)m).texture(f, h).next();
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
