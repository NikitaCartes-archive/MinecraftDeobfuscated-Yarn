package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_928 extends class_897<class_1534> {
	public class_928(class_898 arg) {
		super(arg);
	}

	public void method_4075(class_1534 arg, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.translated(d, e, f);
		GlStateManager.rotatef(180.0F - g, 0.0F, 1.0F, 0.0F);
		GlStateManager.enableRescaleNormal();
		this.method_3925(arg);
		class_1535 lv = arg.field_7134;
		float i = 0.0625F;
		GlStateManager.scalef(0.0625F, 0.0625F, 0.0625F);
		if (this.field_4674) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(arg));
		}

		class_4044 lv2 = class_310.method_1551().method_18321();
		this.method_4074(arg, lv.method_6945(), lv.method_6943(), lv2.method_18345(lv), lv2.method_18342());
		if (this.field_4674) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.method_3936(arg, d, e, f, g, h);
	}

	protected class_2960 method_4077(class_1534 arg) {
		return class_1059.field_18031;
	}

	private void method_4074(class_1534 arg, int i, int j, class_1058 arg2, class_1058 arg3) {
		float f = (float)(-i) / 2.0F;
		float g = (float)(-j) / 2.0F;
		float h = 0.5F;
		float k = arg3.method_4594();
		float l = arg3.method_4577();
		float m = arg3.method_4593();
		float n = arg3.method_4575();
		float o = arg3.method_4594();
		float p = arg3.method_4577();
		float q = arg3.method_4593();
		float r = arg3.method_4570(1.0);
		float s = arg3.method_4594();
		float t = arg3.method_4580(1.0);
		float u = arg3.method_4593();
		float v = arg3.method_4575();
		int w = i / 16;
		int x = j / 16;
		double d = 16.0 / (double)w;
		double e = 16.0 / (double)x;

		for (int y = 0; y < w; y++) {
			for (int z = 0; z < x; z++) {
				float aa = f + (float)((y + 1) * 16);
				float ab = f + (float)(y * 16);
				float ac = g + (float)((z + 1) * 16);
				float ad = g + (float)(z * 16);
				this.method_4076(arg, (aa + ab) / 2.0F, (ac + ad) / 2.0F);
				float ae = arg2.method_4580(d * (double)(w - y));
				float af = arg2.method_4580(d * (double)(w - (y + 1)));
				float ag = arg2.method_4570(e * (double)(x - z));
				float ah = arg2.method_4570(e * (double)(x - (z + 1)));
				class_289 lv = class_289.method_1348();
				class_287 lv2 = lv.method_1349();
				lv2.method_1328(7, class_290.field_1589);
				lv2.method_1315((double)aa, (double)ad, -0.5).method_1312((double)af, (double)ag).method_1318(0.0F, 0.0F, -1.0F).method_1344();
				lv2.method_1315((double)ab, (double)ad, -0.5).method_1312((double)ae, (double)ag).method_1318(0.0F, 0.0F, -1.0F).method_1344();
				lv2.method_1315((double)ab, (double)ac, -0.5).method_1312((double)ae, (double)ah).method_1318(0.0F, 0.0F, -1.0F).method_1344();
				lv2.method_1315((double)aa, (double)ac, -0.5).method_1312((double)af, (double)ah).method_1318(0.0F, 0.0F, -1.0F).method_1344();
				lv2.method_1315((double)aa, (double)ac, 0.5).method_1312((double)k, (double)m).method_1318(0.0F, 0.0F, 1.0F).method_1344();
				lv2.method_1315((double)ab, (double)ac, 0.5).method_1312((double)l, (double)m).method_1318(0.0F, 0.0F, 1.0F).method_1344();
				lv2.method_1315((double)ab, (double)ad, 0.5).method_1312((double)l, (double)n).method_1318(0.0F, 0.0F, 1.0F).method_1344();
				lv2.method_1315((double)aa, (double)ad, 0.5).method_1312((double)k, (double)n).method_1318(0.0F, 0.0F, 1.0F).method_1344();
				lv2.method_1315((double)aa, (double)ac, -0.5).method_1312((double)o, (double)q).method_1318(0.0F, 1.0F, 0.0F).method_1344();
				lv2.method_1315((double)ab, (double)ac, -0.5).method_1312((double)p, (double)q).method_1318(0.0F, 1.0F, 0.0F).method_1344();
				lv2.method_1315((double)ab, (double)ac, 0.5).method_1312((double)p, (double)r).method_1318(0.0F, 1.0F, 0.0F).method_1344();
				lv2.method_1315((double)aa, (double)ac, 0.5).method_1312((double)o, (double)r).method_1318(0.0F, 1.0F, 0.0F).method_1344();
				lv2.method_1315((double)aa, (double)ad, 0.5).method_1312((double)o, (double)q).method_1318(0.0F, -1.0F, 0.0F).method_1344();
				lv2.method_1315((double)ab, (double)ad, 0.5).method_1312((double)p, (double)q).method_1318(0.0F, -1.0F, 0.0F).method_1344();
				lv2.method_1315((double)ab, (double)ad, -0.5).method_1312((double)p, (double)r).method_1318(0.0F, -1.0F, 0.0F).method_1344();
				lv2.method_1315((double)aa, (double)ad, -0.5).method_1312((double)o, (double)r).method_1318(0.0F, -1.0F, 0.0F).method_1344();
				lv2.method_1315((double)aa, (double)ac, 0.5).method_1312((double)t, (double)u).method_1318(-1.0F, 0.0F, 0.0F).method_1344();
				lv2.method_1315((double)aa, (double)ad, 0.5).method_1312((double)t, (double)v).method_1318(-1.0F, 0.0F, 0.0F).method_1344();
				lv2.method_1315((double)aa, (double)ad, -0.5).method_1312((double)s, (double)v).method_1318(-1.0F, 0.0F, 0.0F).method_1344();
				lv2.method_1315((double)aa, (double)ac, -0.5).method_1312((double)s, (double)u).method_1318(-1.0F, 0.0F, 0.0F).method_1344();
				lv2.method_1315((double)ab, (double)ac, -0.5).method_1312((double)t, (double)u).method_1318(1.0F, 0.0F, 0.0F).method_1344();
				lv2.method_1315((double)ab, (double)ad, -0.5).method_1312((double)t, (double)v).method_1318(1.0F, 0.0F, 0.0F).method_1344();
				lv2.method_1315((double)ab, (double)ad, 0.5).method_1312((double)s, (double)v).method_1318(1.0F, 0.0F, 0.0F).method_1344();
				lv2.method_1315((double)ab, (double)ac, 0.5).method_1312((double)s, (double)u).method_1318(1.0F, 0.0F, 0.0F).method_1344();
				lv.method_1350();
			}
		}
	}

	private void method_4076(class_1534 arg, float f, float g) {
		int i = class_3532.method_15357(arg.field_5987);
		int j = class_3532.method_15357(arg.field_6010 + (double)(g / 16.0F));
		int k = class_3532.method_15357(arg.field_6035);
		class_2350 lv = arg.field_7099;
		if (lv == class_2350.field_11043) {
			i = class_3532.method_15357(arg.field_5987 + (double)(f / 16.0F));
		}

		if (lv == class_2350.field_11039) {
			k = class_3532.method_15357(arg.field_6035 - (double)(f / 16.0F));
		}

		if (lv == class_2350.field_11035) {
			i = class_3532.method_15357(arg.field_5987 - (double)(f / 16.0F));
		}

		if (lv == class_2350.field_11034) {
			k = class_3532.method_15357(arg.field_6035 + (double)(f / 16.0F));
		}

		int l = this.field_4676.field_4684.method_8313(new class_2338(i, j, k), 0);
		int m = l % 65536;
		int n = l / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)m, (float)n);
		GlStateManager.color3f(1.0F, 1.0F, 1.0F);
	}
}
