package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_895 extends class_927<class_1510, class_625> {
	public static final class_2960 field_4668 = new class_2960("textures/entity/end_crystal/end_crystal_beam.png");
	private static final class_2960 field_4669 = new class_2960("textures/entity/enderdragon/dragon_exploding.png");
	private static final class_2960 field_4670 = new class_2960("textures/entity/enderdragon/dragon.png");

	public class_895(class_898 arg) {
		super(arg, new class_625(0.0F), 0.5F);
		this.method_4046(new class_981(this));
		this.method_4046(new class_982(this));
	}

	protected void method_3915(class_1510 arg, float f, float g, float h) {
		float i = (float)arg.method_6817(7, h)[0];
		float j = (float)(arg.method_6817(5, h)[1] - arg.method_6817(10, h)[1]);
		GlStateManager.rotatef(-i, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(j * 10.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.translatef(0.0F, 0.0F, 1.0F);
		if (arg.field_6213 > 0) {
			float k = ((float)arg.field_6213 + h - 1.0F) / 20.0F * 1.6F;
			k = class_3532.method_15355(k);
			if (k > 1.0F) {
				k = 1.0F;
			}

			GlStateManager.rotatef(k * this.method_4039(arg), 0.0F, 0.0F, 1.0F);
		}
	}

	protected void method_3916(class_1510 arg, float f, float g, float h, float i, float j, float k) {
		if (arg.field_7031 > 0) {
			float l = (float)arg.field_7031 / 200.0F;
			GlStateManager.depthFunc(515);
			GlStateManager.enableAlphaTest();
			GlStateManager.alphaFunc(516, l);
			this.method_3924(field_4669);
			this.field_4737.method_17137(arg, f, g, h, i, j, k);
			GlStateManager.alphaFunc(516, 0.1F);
			GlStateManager.depthFunc(514);
		}

		this.method_3925(arg);
		this.field_4737.method_17137(arg, f, g, h, i, j, k);
		if (arg.field_6235 > 0) {
			GlStateManager.depthFunc(514);
			GlStateManager.disableTexture();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color4f(1.0F, 0.0F, 0.0F, 0.5F);
			this.field_4737.method_17137(arg, f, g, h, i, j, k);
			GlStateManager.enableTexture();
			GlStateManager.disableBlend();
			GlStateManager.depthFunc(515);
		}
	}

	public void method_3918(class_1510 arg, double d, double e, double f, float g, float h) {
		super.method_4072(arg, d, e, f, g, h);
		if (arg.field_7024 != null) {
			this.method_3924(field_4668);
			float i = class_3532.method_15374(((float)arg.field_7024.field_6012 + h) * 0.2F) / 2.0F + 0.5F;
			i = (i * i + i) * 0.2F;
			method_3917(
				d,
				e,
				f,
				h,
				class_3532.method_16436((double)(1.0F - h), arg.field_5987, arg.field_6014),
				class_3532.method_16436((double)(1.0F - h), arg.field_6010, arg.field_6036),
				class_3532.method_16436((double)(1.0F - h), arg.field_6035, arg.field_5969),
				arg.field_6012,
				arg.field_7024.field_5987,
				(double)i + arg.field_7024.field_6010,
				arg.field_7024.field_6035
			);
		}
	}

	public static void method_3917(double d, double e, double f, float g, double h, double i, double j, int k, double l, double m, double n) {
		float o = (float)(l - h);
		float p = (float)(m - 1.0 - i);
		float q = (float)(n - j);
		float r = class_3532.method_15355(o * o + q * q);
		float s = class_3532.method_15355(o * o + p * p + q * q);
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)d, (float)e + 2.0F, (float)f);
		GlStateManager.rotatef((float)(-Math.atan2((double)q, (double)o)) * (180.0F / (float)Math.PI) - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef((float)(-Math.atan2((double)r, (double)p)) * (180.0F / (float)Math.PI) - 90.0F, 1.0F, 0.0F, 0.0F);
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		class_308.method_1450();
		GlStateManager.disableCull();
		GlStateManager.shadeModel(7425);
		float t = 0.0F - ((float)k + g) * 0.01F;
		float u = class_3532.method_15355(o * o + p * p + q * q) / 32.0F - ((float)k + g) * 0.01F;
		lv2.method_1328(5, class_290.field_1575);
		int v = 8;

		for (int w = 0; w <= 8; w++) {
			float x = class_3532.method_15374((float)(w % 8) * (float) (Math.PI * 2) / 8.0F) * 0.75F;
			float y = class_3532.method_15362((float)(w % 8) * (float) (Math.PI * 2) / 8.0F) * 0.75F;
			float z = (float)(w % 8) / 8.0F;
			lv2.method_1315((double)(x * 0.2F), (double)(y * 0.2F), 0.0).method_1312((double)z, (double)t).method_1323(0, 0, 0, 255).method_1344();
			lv2.method_1315((double)x, (double)y, (double)s).method_1312((double)z, (double)u).method_1323(255, 255, 255, 255).method_1344();
		}

		lv.method_1350();
		GlStateManager.enableCull();
		GlStateManager.shadeModel(7424);
		class_308.method_1452();
		GlStateManager.popMatrix();
	}

	protected class_2960 method_3914(class_1510 arg) {
		return field_4670;
	}
}
