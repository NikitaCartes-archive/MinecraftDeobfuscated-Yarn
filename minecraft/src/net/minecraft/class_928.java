package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_928 extends class_897<class_1534> {
	private static final class_2960 field_4749 = new class_2960("textures/painting/paintings_kristoffer_zetterstrand.png");

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

		this.method_4074(arg, lv.method_6945(), lv.method_6946(), lv.method_6942(), lv.method_6943());
		if (this.field_4674) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.method_3936(arg, d, e, f, g, h);
	}

	protected class_2960 method_4077(class_1534 arg) {
		return field_4749;
	}

	private void method_4074(class_1534 arg, int i, int j, int k, int l) {
		float f = (float)(-i) / 2.0F;
		float g = (float)(-j) / 2.0F;
		float h = 0.5F;
		float m = 0.75F;
		float n = 0.8125F;
		float o = 0.0F;
		float p = 0.0625F;
		float q = 0.75F;
		float r = 0.8125F;
		float s = 0.001953125F;
		float t = 0.001953125F;
		float u = 0.7519531F;
		float v = 0.7519531F;
		float w = 0.0F;
		float x = 0.0625F;

		for (int y = 0; y < i / 16; y++) {
			for (int z = 0; z < j / 16; z++) {
				float aa = f + (float)((y + 1) * 16);
				float ab = f + (float)(y * 16);
				float ac = g + (float)((z + 1) * 16);
				float ad = g + (float)(z * 16);
				this.method_4076(arg, (aa + ab) / 2.0F, (ac + ad) / 2.0F);
				float ae = (float)(k + i - y * 16) / 256.0F;
				float af = (float)(k + i - (y + 1) * 16) / 256.0F;
				float ag = (float)(l + j - z * 16) / 256.0F;
				float ah = (float)(l + j - (z + 1) * 16) / 256.0F;
				class_289 lv = class_289.method_1348();
				class_287 lv2 = lv.method_1349();
				lv2.method_1328(7, class_290.field_1589);
				lv2.method_1315((double)aa, (double)ad, -0.5).method_1312((double)af, (double)ag).method_1318(0.0F, 0.0F, -1.0F).method_1344();
				lv2.method_1315((double)ab, (double)ad, -0.5).method_1312((double)ae, (double)ag).method_1318(0.0F, 0.0F, -1.0F).method_1344();
				lv2.method_1315((double)ab, (double)ac, -0.5).method_1312((double)ae, (double)ah).method_1318(0.0F, 0.0F, -1.0F).method_1344();
				lv2.method_1315((double)aa, (double)ac, -0.5).method_1312((double)af, (double)ah).method_1318(0.0F, 0.0F, -1.0F).method_1344();
				lv2.method_1315((double)aa, (double)ac, 0.5).method_1312(0.75, 0.0).method_1318(0.0F, 0.0F, 1.0F).method_1344();
				lv2.method_1315((double)ab, (double)ac, 0.5).method_1312(0.8125, 0.0).method_1318(0.0F, 0.0F, 1.0F).method_1344();
				lv2.method_1315((double)ab, (double)ad, 0.5).method_1312(0.8125, 0.0625).method_1318(0.0F, 0.0F, 1.0F).method_1344();
				lv2.method_1315((double)aa, (double)ad, 0.5).method_1312(0.75, 0.0625).method_1318(0.0F, 0.0F, 1.0F).method_1344();
				lv2.method_1315((double)aa, (double)ac, -0.5).method_1312(0.75, 0.001953125).method_1318(0.0F, 1.0F, 0.0F).method_1344();
				lv2.method_1315((double)ab, (double)ac, -0.5).method_1312(0.8125, 0.001953125).method_1318(0.0F, 1.0F, 0.0F).method_1344();
				lv2.method_1315((double)ab, (double)ac, 0.5).method_1312(0.8125, 0.001953125).method_1318(0.0F, 1.0F, 0.0F).method_1344();
				lv2.method_1315((double)aa, (double)ac, 0.5).method_1312(0.75, 0.001953125).method_1318(0.0F, 1.0F, 0.0F).method_1344();
				lv2.method_1315((double)aa, (double)ad, 0.5).method_1312(0.75, 0.001953125).method_1318(0.0F, -1.0F, 0.0F).method_1344();
				lv2.method_1315((double)ab, (double)ad, 0.5).method_1312(0.8125, 0.001953125).method_1318(0.0F, -1.0F, 0.0F).method_1344();
				lv2.method_1315((double)ab, (double)ad, -0.5).method_1312(0.8125, 0.001953125).method_1318(0.0F, -1.0F, 0.0F).method_1344();
				lv2.method_1315((double)aa, (double)ad, -0.5).method_1312(0.75, 0.001953125).method_1318(0.0F, -1.0F, 0.0F).method_1344();
				lv2.method_1315((double)aa, (double)ac, 0.5).method_1312(0.7519531F, 0.0).method_1318(-1.0F, 0.0F, 0.0F).method_1344();
				lv2.method_1315((double)aa, (double)ad, 0.5).method_1312(0.7519531F, 0.0625).method_1318(-1.0F, 0.0F, 0.0F).method_1344();
				lv2.method_1315((double)aa, (double)ad, -0.5).method_1312(0.7519531F, 0.0625).method_1318(-1.0F, 0.0F, 0.0F).method_1344();
				lv2.method_1315((double)aa, (double)ac, -0.5).method_1312(0.7519531F, 0.0).method_1318(-1.0F, 0.0F, 0.0F).method_1344();
				lv2.method_1315((double)ab, (double)ac, -0.5).method_1312(0.7519531F, 0.0).method_1318(1.0F, 0.0F, 0.0F).method_1344();
				lv2.method_1315((double)ab, (double)ad, -0.5).method_1312(0.7519531F, 0.0625).method_1318(1.0F, 0.0F, 0.0F).method_1344();
				lv2.method_1315((double)ab, (double)ad, 0.5).method_1312(0.7519531F, 0.0625).method_1318(1.0F, 0.0F, 0.0F).method_1344();
				lv2.method_1315((double)ab, (double)ac, 0.5).method_1312(0.7519531F, 0.0).method_1318(1.0F, 0.0F, 0.0F).method_1344();
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
