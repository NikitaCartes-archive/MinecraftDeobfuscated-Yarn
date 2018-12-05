package net.minecraft.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.FontRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class Drawable {
	public static final Identifier OPTIONS_BG = new Identifier("textures/gui/options_background.png");
	public static final Identifier STAT_ICONS = new Identifier("textures/gui/container/stats_icons.png");
	public static final Identifier ICONS = new Identifier("textures/gui/icons.png");
	protected float zOffset;

	protected void drawHorizontalLine(int i, int j, int k, int l) {
		if (j < i) {
			int m = i;
			i = j;
			j = m;
		}

		drawRect(i, k, j + 1, k + 1, l);
	}

	protected void drawVerticalLine(int i, int j, int k, int l) {
		if (k < j) {
			int m = j;
			j = k;
			k = m;
		}

		drawRect(i, j + 1, i + 1, k, l);
	}

	public static void drawRect(int i, int j, int k, int l, int m) {
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
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SrcBlendFactor.SRC_ALPHA,
			GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
			GlStateManager.SrcBlendFactor.ONE,
			GlStateManager.DstBlendFactor.ZERO
		);
		GlStateManager.color4f(g, h, o, f);
		vertexBuffer.begin(7, VertexFormats.POSITION);
		vertexBuffer.vertex((double)i, (double)l, 0.0).next();
		vertexBuffer.vertex((double)k, (double)l, 0.0).next();
		vertexBuffer.vertex((double)k, (double)j, 0.0).next();
		vertexBuffer.vertex((double)i, (double)j, 0.0).next();
		tessellator.draw();
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}

	protected void drawGradientRect(int i, int j, int k, int l, int m, int n) {
		float f = (float)(m >> 24 & 0xFF) / 255.0F;
		float g = (float)(m >> 16 & 0xFF) / 255.0F;
		float h = (float)(m >> 8 & 0xFF) / 255.0F;
		float o = (float)(m & 0xFF) / 255.0F;
		float p = (float)(n >> 24 & 0xFF) / 255.0F;
		float q = (float)(n >> 16 & 0xFF) / 255.0F;
		float r = (float)(n >> 8 & 0xFF) / 255.0F;
		float s = (float)(n & 0xFF) / 255.0F;
		GlStateManager.disableTexture();
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SrcBlendFactor.SRC_ALPHA,
			GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
			GlStateManager.SrcBlendFactor.ONE,
			GlStateManager.DstBlendFactor.ZERO
		);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
		vertexBuffer.begin(7, VertexFormats.POSITION_COLOR);
		vertexBuffer.vertex((double)k, (double)j, (double)this.zOffset).color(g, h, o, f).next();
		vertexBuffer.vertex((double)i, (double)j, (double)this.zOffset).color(g, h, o, f).next();
		vertexBuffer.vertex((double)i, (double)l, (double)this.zOffset).color(q, r, s, p).next();
		vertexBuffer.vertex((double)k, (double)l, (double)this.zOffset).color(q, r, s, p).next();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
		GlStateManager.enableTexture();
	}

	public void drawStringCentered(FontRenderer fontRenderer, String string, int i, int j, int k) {
		fontRenderer.drawWithShadow(string, (float)(i - fontRenderer.getStringWidth(string) / 2), (float)j, k);
	}

	public void drawString(FontRenderer fontRenderer, String string, int i, int j, int k) {
		fontRenderer.drawWithShadow(string, (float)i, (float)j, k);
	}

	public void drawTexturedRect(int i, int j, int k, int l, int m, int n) {
		float f = 0.00390625F;
		float g = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
		vertexBuffer.begin(7, VertexFormats.POSITION_UV);
		vertexBuffer.vertex((double)(i + 0), (double)(j + n), (double)this.zOffset)
			.texture((double)((float)(k + 0) * 0.00390625F), (double)((float)(l + n) * 0.00390625F))
			.next();
		vertexBuffer.vertex((double)(i + m), (double)(j + n), (double)this.zOffset)
			.texture((double)((float)(k + m) * 0.00390625F), (double)((float)(l + n) * 0.00390625F))
			.next();
		vertexBuffer.vertex((double)(i + m), (double)(j + 0), (double)this.zOffset)
			.texture((double)((float)(k + m) * 0.00390625F), (double)((float)(l + 0) * 0.00390625F))
			.next();
		vertexBuffer.vertex((double)(i + 0), (double)(j + 0), (double)this.zOffset)
			.texture((double)((float)(k + 0) * 0.00390625F), (double)((float)(l + 0) * 0.00390625F))
			.next();
		tessellator.draw();
	}

	public void drawTexturedRect(float f, float g, int i, int j, int k, int l) {
		float h = 0.00390625F;
		float m = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
		vertexBuffer.begin(7, VertexFormats.POSITION_UV);
		vertexBuffer.vertex((double)(f + 0.0F), (double)(g + (float)l), (double)this.zOffset)
			.texture((double)((float)(i + 0) * 0.00390625F), (double)((float)(j + l) * 0.00390625F))
			.next();
		vertexBuffer.vertex((double)(f + (float)k), (double)(g + (float)l), (double)this.zOffset)
			.texture((double)((float)(i + k) * 0.00390625F), (double)((float)(j + l) * 0.00390625F))
			.next();
		vertexBuffer.vertex((double)(f + (float)k), (double)(g + 0.0F), (double)this.zOffset)
			.texture((double)((float)(i + k) * 0.00390625F), (double)((float)(j + 0) * 0.00390625F))
			.next();
		vertexBuffer.vertex((double)(f + 0.0F), (double)(g + 0.0F), (double)this.zOffset)
			.texture((double)((float)(i + 0) * 0.00390625F), (double)((float)(j + 0) * 0.00390625F))
			.next();
		tessellator.draw();
	}

	public void drawTexturedRect(int i, int j, Sprite sprite, int k, int l) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
		vertexBuffer.begin(7, VertexFormats.POSITION_UV);
		vertexBuffer.vertex((double)(i + 0), (double)(j + l), (double)this.zOffset).texture((double)sprite.getMinU(), (double)sprite.getMaxV()).next();
		vertexBuffer.vertex((double)(i + k), (double)(j + l), (double)this.zOffset).texture((double)sprite.getMaxU(), (double)sprite.getMaxV()).next();
		vertexBuffer.vertex((double)(i + k), (double)(j + 0), (double)this.zOffset).texture((double)sprite.getMaxU(), (double)sprite.getMinV()).next();
		vertexBuffer.vertex((double)(i + 0), (double)(j + 0), (double)this.zOffset).texture((double)sprite.getMinU(), (double)sprite.getMinV()).next();
		tessellator.draw();
	}

	public static void drawTexturedRect(int i, int j, float f, float g, int k, int l, float h, float m) {
		float n = 1.0F / h;
		float o = 1.0F / m;
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
		vertexBuffer.begin(7, VertexFormats.POSITION_UV);
		vertexBuffer.vertex((double)i, (double)(j + l), 0.0).texture((double)(f * n), (double)((g + (float)l) * o)).next();
		vertexBuffer.vertex((double)(i + k), (double)(j + l), 0.0).texture((double)((f + (float)k) * n), (double)((g + (float)l) * o)).next();
		vertexBuffer.vertex((double)(i + k), (double)j, 0.0).texture((double)((f + (float)k) * n), (double)(g * o)).next();
		vertexBuffer.vertex((double)i, (double)j, 0.0).texture((double)(f * n), (double)(g * o)).next();
		tessellator.draw();
	}

	public static void drawTexturedRect(int i, int j, float f, float g, int k, int l, int m, int n, float h, float o) {
		float p = 1.0F / h;
		float q = 1.0F / o;
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
		vertexBuffer.begin(7, VertexFormats.POSITION_UV);
		vertexBuffer.vertex((double)i, (double)(j + n), 0.0).texture((double)(f * p), (double)((g + (float)l) * q)).next();
		vertexBuffer.vertex((double)(i + m), (double)(j + n), 0.0).texture((double)((f + (float)k) * p), (double)((g + (float)l) * q)).next();
		vertexBuffer.vertex((double)(i + m), (double)j, 0.0).texture((double)((f + (float)k) * p), (double)(g * q)).next();
		vertexBuffer.vertex((double)i, (double)j, 0.0).texture((double)(f * p), (double)(g * q)).next();
		tessellator.draw();
	}
}
