package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_332 {
	public static final class_2960 BACKGROUND_LOCATION = new class_2960("textures/gui/options_background.png");
	public static final class_2960 STATS_ICON_LOCATION = new class_2960("textures/gui/container/stats_icons.png");
	public static final class_2960 GUI_ICONS_LOCATION = new class_2960("textures/gui/icons.png");
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
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_5119.SRC_ALPHA, GlStateManager.class_5118.ONE_MINUS_SRC_ALPHA, GlStateManager.class_5119.ONE, GlStateManager.class_5118.ZERO
		);
		GlStateManager.color4f(g, h, o, f);
		lv2.method_1328(7, class_290.field_1592);
		lv2.method_1315((double)i, (double)l, 0.0).method_1344();
		lv2.method_1315((double)k, (double)l, 0.0).method_1344();
		lv2.method_1315((double)k, (double)j, 0.0).method_1344();
		lv2.method_1315((double)i, (double)j, 0.0).method_1344();
		lv.method_1350();
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
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
		GlStateManager.disableTexture();
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_5119.SRC_ALPHA, GlStateManager.class_5118.ONE_MINUS_SRC_ALPHA, GlStateManager.class_5119.ONE, GlStateManager.class_5118.ZERO
		);
		GlStateManager.shadeModel(7425);
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		lv2.method_1328(7, class_290.field_1576);
		lv2.method_1315((double)k, (double)j, (double)this.blitOffset).method_1336(g, h, o, f).method_1344();
		lv2.method_1315((double)i, (double)j, (double)this.blitOffset).method_1336(g, h, o, f).method_1344();
		lv2.method_1315((double)i, (double)l, (double)this.blitOffset).method_1336(q, r, s, p).method_1344();
		lv2.method_1315((double)k, (double)l, (double)this.blitOffset).method_1336(q, r, s, p).method_1344();
		lv.method_1350();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
		GlStateManager.enableTexture();
	}

	public void drawCenteredString(class_327 arg, String string, int i, int j, int k) {
		arg.method_1720(string, (float)(i - arg.method_1727(string) / 2), (float)j, k);
	}

	public void drawRightAlignedString(class_327 arg, String string, int i, int j, int k) {
		arg.method_1720(string, (float)(i - arg.method_1727(string)), (float)j, k);
	}

	public void drawString(class_327 arg, String string, int i, int j, int k) {
		arg.method_1720(string, (float)i, (float)j, k);
	}

	public static void blit(int i, int j, int k, int l, int m, class_1058 arg) {
		innerBlit(i, i + l, j, j + m, k, arg.method_4594(), arg.method_4577(), arg.method_4593(), arg.method_4575());
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
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		lv2.method_1328(7, class_290.field_1585);
		lv2.method_1315((double)i, (double)l, (double)m).method_1312((double)f, (double)n).method_1344();
		lv2.method_1315((double)j, (double)l, (double)m).method_1312((double)g, (double)n).method_1344();
		lv2.method_1315((double)j, (double)k, (double)m).method_1312((double)g, (double)h).method_1344();
		lv2.method_1315((double)i, (double)k, (double)m).method_1312((double)f, (double)h).method_1344();
		lv.method_1350();
	}
}
