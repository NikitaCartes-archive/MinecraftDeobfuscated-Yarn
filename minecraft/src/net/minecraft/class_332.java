package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_332 {
	public static final class_2960 field_2051 = new class_2960("textures/gui/options_background.png");
	public static final class_2960 field_2052 = new class_2960("textures/gui/container/stats_icons.png");
	public static final class_2960 field_2053 = new class_2960("textures/gui/icons.png");
	protected float field_2050;

	protected void method_1783(int i, int j, int k, int l) {
		if (j < i) {
			int m = i;
			i = j;
			j = m;
		}

		method_1785(i, k, j + 1, k + 1, l);
	}

	protected void method_1787(int i, int j, int k, int l) {
		if (k < j) {
			int m = j;
			j = k;
			k = m;
		}

		method_1785(i, j + 1, i + 1, k, l);
	}

	public static void method_1785(int i, int j, int k, int l, int m) {
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
			GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
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

	protected void method_1782(int i, int j, int k, int l, int m, int n) {
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
			GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
		);
		GlStateManager.shadeModel(7425);
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		lv2.method_1328(7, class_290.field_1576);
		lv2.method_1315((double)k, (double)j, (double)this.field_2050).method_1336(g, h, o, f).method_1344();
		lv2.method_1315((double)i, (double)j, (double)this.field_2050).method_1336(g, h, o, f).method_1344();
		lv2.method_1315((double)i, (double)l, (double)this.field_2050).method_1336(q, r, s, p).method_1344();
		lv2.method_1315((double)k, (double)l, (double)this.field_2050).method_1336(q, r, s, p).method_1344();
		lv.method_1350();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
		GlStateManager.enableTexture();
	}

	public void method_1789(class_327 arg, String string, int i, int j, int k) {
		arg.method_1720(string, (float)(i - arg.method_1727(string) / 2), (float)j, k);
	}

	public void method_1780(class_327 arg, String string, int i, int j, int k) {
		arg.method_1720(string, (float)i, (float)j, k);
	}

	public void method_1788(int i, int j, int k, int l, int m, int n) {
		float f = 0.00390625F;
		float g = 0.00390625F;
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		lv2.method_1328(7, class_290.field_1585);
		lv2.method_1315((double)(i + 0), (double)(j + n), (double)this.field_2050)
			.method_1312((double)((float)(k + 0) * 0.00390625F), (double)((float)(l + n) * 0.00390625F))
			.method_1344();
		lv2.method_1315((double)(i + m), (double)(j + n), (double)this.field_2050)
			.method_1312((double)((float)(k + m) * 0.00390625F), (double)((float)(l + n) * 0.00390625F))
			.method_1344();
		lv2.method_1315((double)(i + m), (double)(j + 0), (double)this.field_2050)
			.method_1312((double)((float)(k + m) * 0.00390625F), (double)((float)(l + 0) * 0.00390625F))
			.method_1344();
		lv2.method_1315((double)(i + 0), (double)(j + 0), (double)this.field_2050)
			.method_1312((double)((float)(k + 0) * 0.00390625F), (double)((float)(l + 0) * 0.00390625F))
			.method_1344();
		lv.method_1350();
	}

	public void method_1784(float f, float g, int i, int j, int k, int l) {
		float h = 0.00390625F;
		float m = 0.00390625F;
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		lv2.method_1328(7, class_290.field_1585);
		lv2.method_1315((double)(f + 0.0F), (double)(g + (float)l), (double)this.field_2050)
			.method_1312((double)((float)(i + 0) * 0.00390625F), (double)((float)(j + l) * 0.00390625F))
			.method_1344();
		lv2.method_1315((double)(f + (float)k), (double)(g + (float)l), (double)this.field_2050)
			.method_1312((double)((float)(i + k) * 0.00390625F), (double)((float)(j + l) * 0.00390625F))
			.method_1344();
		lv2.method_1315((double)(f + (float)k), (double)(g + 0.0F), (double)this.field_2050)
			.method_1312((double)((float)(i + k) * 0.00390625F), (double)((float)(j + 0) * 0.00390625F))
			.method_1344();
		lv2.method_1315((double)(f + 0.0F), (double)(g + 0.0F), (double)this.field_2050)
			.method_1312((double)((float)(i + 0) * 0.00390625F), (double)((float)(j + 0) * 0.00390625F))
			.method_1344();
		lv.method_1350();
	}

	public void method_1790(int i, int j, class_1058 arg, int k, int l) {
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		lv2.method_1328(7, class_290.field_1585);
		lv2.method_1315((double)(i + 0), (double)(j + l), (double)this.field_2050).method_1312((double)arg.method_4594(), (double)arg.method_4575()).method_1344();
		lv2.method_1315((double)(i + k), (double)(j + l), (double)this.field_2050).method_1312((double)arg.method_4577(), (double)arg.method_4575()).method_1344();
		lv2.method_1315((double)(i + k), (double)(j + 0), (double)this.field_2050).method_1312((double)arg.method_4577(), (double)arg.method_4593()).method_1344();
		lv2.method_1315((double)(i + 0), (double)(j + 0), (double)this.field_2050).method_1312((double)arg.method_4594(), (double)arg.method_4593()).method_1344();
		lv.method_1350();
	}

	public static void method_1781(int i, int j, float f, float g, int k, int l, float h, float m) {
		float n = 1.0F / h;
		float o = 1.0F / m;
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		lv2.method_1328(7, class_290.field_1585);
		lv2.method_1315((double)i, (double)(j + l), 0.0).method_1312((double)(f * n), (double)((g + (float)l) * o)).method_1344();
		lv2.method_1315((double)(i + k), (double)(j + l), 0.0).method_1312((double)((f + (float)k) * n), (double)((g + (float)l) * o)).method_1344();
		lv2.method_1315((double)(i + k), (double)j, 0.0).method_1312((double)((f + (float)k) * n), (double)(g * o)).method_1344();
		lv2.method_1315((double)i, (double)j, 0.0).method_1312((double)(f * n), (double)(g * o)).method_1344();
		lv.method_1350();
	}

	public static void method_1786(int i, int j, float f, float g, int k, int l, int m, int n, float h, float o) {
		float p = 1.0F / h;
		float q = 1.0F / o;
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		lv2.method_1328(7, class_290.field_1585);
		lv2.method_1315((double)i, (double)(j + n), 0.0).method_1312((double)(f * p), (double)((g + (float)l) * q)).method_1344();
		lv2.method_1315((double)(i + m), (double)(j + n), 0.0).method_1312((double)((f + (float)k) * p), (double)((g + (float)l) * q)).method_1344();
		lv2.method_1315((double)(i + m), (double)j, 0.0).method_1312((double)((f + (float)k) * p), (double)(g * q)).method_1344();
		lv2.method_1315((double)i, (double)j, 0.0).method_1312((double)(f * p), (double)(g * q)).method_1344();
		lv.method_1350();
	}
}
