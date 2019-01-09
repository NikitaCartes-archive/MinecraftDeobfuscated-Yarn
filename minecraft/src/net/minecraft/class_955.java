package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_955 extends class_897<class_1685> {
	public static final class_2960 field_4796 = new class_2960("textures/entity/trident.png");
	private final class_613 field_4797 = new class_613();

	public class_955(class_898 arg) {
		super(arg);
	}

	public void method_4133(class_1685 arg, double d, double e, double f, float g, float h) {
		this.method_3925(arg);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.translatef((float)d, (float)e, (float)f);
		GlStateManager.rotatef(class_3532.method_16439(h, arg.field_5982, arg.field_6031) - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(class_3532.method_16439(h, arg.field_6004, arg.field_5965) + 90.0F, 0.0F, 0.0F, 1.0F);
		this.field_4797.method_2835();
		GlStateManager.popMatrix();
		this.method_4131(arg, d, e, f, g, h);
		super.method_3936(arg, d, e, f, g, h);
		GlStateManager.enableLighting();
	}

	protected class_2960 method_4134(class_1685 arg) {
		return field_4796;
	}

	protected void method_4131(class_1685 arg, double d, double e, double f, float g, float h) {
		class_1297 lv = arg.method_7452();
		if (lv != null && arg.method_7441()) {
			class_289 lv2 = class_289.method_1348();
			class_287 lv3 = lv2.method_1349();
			double i = (double)(class_3532.method_16439(h * 0.5F, lv.field_6031, lv.field_5982) * (float) (Math.PI / 180.0));
			double j = Math.cos(i);
			double k = Math.sin(i);
			double l = class_3532.method_16436((double)h, lv.field_6014, lv.field_5987);
			double m = class_3532.method_16436((double)h, lv.field_6036 + (double)lv.method_5751() * 0.8, lv.field_6010 + (double)lv.method_5751() * 0.8);
			double n = class_3532.method_16436((double)h, lv.field_5969, lv.field_6035);
			double o = j - k;
			double p = k + j;
			double q = class_3532.method_16436((double)h, arg.field_6014, arg.field_5987);
			double r = class_3532.method_16436((double)h, arg.field_6036, arg.field_6010);
			double s = class_3532.method_16436((double)h, arg.field_5969, arg.field_6035);
			double t = (double)((float)(l - q));
			double u = (double)((float)(m - r));
			double v = (double)((float)(n - s));
			double w = Math.sqrt(t * t + u * u + v * v);
			int x = arg.method_5628() + arg.field_6012;
			double y = (double)((float)x + h) * -0.1;
			double z = Math.min(0.5, w / 30.0);
			GlStateManager.disableTexture();
			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 255.0F, 255.0F);
			lv3.method_1328(5, class_290.field_1576);
			int aa = 37;
			int ab = 7 - x % 7;
			double ac = 0.1;

			for (int ad = 0; ad <= 37; ad++) {
				double ae = (double)ad / 37.0;
				float af = 1.0F - (float)((ad + ab) % 7) / 7.0F;
				double ag = ae * 2.0 - 1.0;
				ag = (1.0 - ag * ag) * z;
				double ah = d + t * ae + Math.sin(ae * Math.PI * 8.0 + y) * o * ag;
				double ai = e + u * ae + Math.cos(ae * Math.PI * 8.0 + y) * 0.02 + (0.1 + ag) * 1.0;
				double aj = f + v * ae + Math.sin(ae * Math.PI * 8.0 + y) * p * ag;
				float ak = 0.87F * af + 0.3F * (1.0F - af);
				float al = 0.91F * af + 0.6F * (1.0F - af);
				float am = 0.85F * af + 0.5F * (1.0F - af);
				lv3.method_1315(ah, ai, aj).method_1336(ak, al, am, 1.0F).method_1344();
				lv3.method_1315(ah + 0.1 * ag, ai + 0.1 * ag, aj).method_1336(ak, al, am, 1.0F).method_1344();
				if (ad > arg.field_7649 * 2) {
					break;
				}
			}

			lv2.method_1350();
			lv3.method_1328(5, class_290.field_1576);

			for (int adx = 0; adx <= 37; adx++) {
				double ae = (double)adx / 37.0;
				float af = 1.0F - (float)((adx + ab) % 7) / 7.0F;
				double ag = ae * 2.0 - 1.0;
				ag = (1.0 - ag * ag) * z;
				double ah = d + t * ae + Math.sin(ae * Math.PI * 8.0 + y) * o * ag;
				double ai = e + u * ae + Math.cos(ae * Math.PI * 8.0 + y) * 0.01 + (0.1 + ag) * 1.0;
				double aj = f + v * ae + Math.sin(ae * Math.PI * 8.0 + y) * p * ag;
				float ak = 0.87F * af + 0.3F * (1.0F - af);
				float al = 0.91F * af + 0.6F * (1.0F - af);
				float am = 0.85F * af + 0.5F * (1.0F - af);
				lv3.method_1315(ah, ai, aj).method_1336(ak, al, am, 1.0F).method_1344();
				lv3.method_1315(ah + 0.1 * ag, ai, aj + 0.1 * ag).method_1336(ak, al, am, 1.0F).method_1344();
				if (adx > arg.field_7649 * 2) {
					break;
				}
			}

			lv2.method_1350();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture();
			GlStateManager.enableCull();
		}
	}
}
