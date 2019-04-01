package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_906 extends class_897<class_1536> {
	private static final class_2960 field_4707 = new class_2960("textures/entity/fishing_hook.png");

	public class_906(class_898 arg) {
		super(arg);
	}

	public void method_3974(class_1536 arg, double d, double e, double f, float g, float h) {
		class_1657 lv = arg.method_6947();
		if (lv != null && !this.field_4674) {
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)d, (float)e, (float)f);
			GlStateManager.enableRescaleNormal();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			this.method_3925(arg);
			class_289 lv2 = class_289.method_1348();
			class_287 lv3 = lv2.method_1349();
			float i = 1.0F;
			float j = 0.5F;
			float k = 0.5F;
			GlStateManager.rotatef(180.0F - this.field_4676.field_4679, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef((float)(this.field_4676.field_4692.field_1850 == 2 ? -1 : 1) * -this.field_4676.field_4677, 1.0F, 0.0F, 0.0F);
			if (this.field_4674) {
				GlStateManager.enableColorMaterial();
				GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(arg));
			}

			lv3.method_1328(7, class_290.field_1589);
			lv3.method_1315(-0.5, -0.5, 0.0).method_1312(0.0, 1.0).method_1318(0.0F, 1.0F, 0.0F).method_1344();
			lv3.method_1315(0.5, -0.5, 0.0).method_1312(1.0, 1.0).method_1318(0.0F, 1.0F, 0.0F).method_1344();
			lv3.method_1315(0.5, 0.5, 0.0).method_1312(1.0, 0.0).method_1318(0.0F, 1.0F, 0.0F).method_1344();
			lv3.method_1315(-0.5, 0.5, 0.0).method_1312(0.0, 0.0).method_1318(0.0F, 1.0F, 0.0F).method_1344();
			lv2.method_1350();
			if (this.field_4674) {
				GlStateManager.tearDownSolidRenderingTextureCombine();
				GlStateManager.disableColorMaterial();
			}

			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			int l = lv.method_6068() == class_1306.field_6183 ? 1 : -1;
			class_1799 lv4 = lv.method_6047();
			if (lv4.method_7909() != class_1802.field_8378) {
				l = -l;
			}

			float m = lv.method_6055(h);
			float n = class_3532.method_15374(class_3532.method_15355(m) * (float) Math.PI);
			float o = class_3532.method_16439(h, lv.field_6220, lv.field_6283) * (float) (Math.PI / 180.0);
			double p = (double)class_3532.method_15374(o);
			double q = (double)class_3532.method_15362(o);
			double r = (double)l * 0.35;
			double s = 0.8;
			double t;
			double u;
			double v;
			double w;
			if ((this.field_4676.field_4692 == null || this.field_4676.field_4692.field_1850 <= 0) && lv == class_310.method_1551().field_1724) {
				double x = this.field_4676.field_4692.field_1826;
				x /= 100.0;
				class_243 lv5 = new class_243((double)l * -0.36 * x, -0.045 * x, 0.4);
				lv5 = lv5.method_1037(-class_3532.method_16439(h, lv.field_6004, lv.field_5965) * (float) (Math.PI / 180.0));
				lv5 = lv5.method_1024(-class_3532.method_16439(h, lv.field_5982, lv.field_6031) * (float) (Math.PI / 180.0));
				lv5 = lv5.method_1024(n * 0.5F);
				lv5 = lv5.method_1037(-n * 0.7F);
				t = class_3532.method_16436((double)h, lv.field_6014, lv.field_5987) + lv5.field_1352;
				u = class_3532.method_16436((double)h, lv.field_6036, lv.field_6010) + lv5.field_1351;
				v = class_3532.method_16436((double)h, lv.field_5969, lv.field_6035) + lv5.field_1350;
				w = (double)lv.method_5751();
			} else {
				t = class_3532.method_16436((double)h, lv.field_6014, lv.field_5987) - q * r - p * 0.8;
				u = lv.field_6036 + (double)lv.method_5751() + (lv.field_6010 - lv.field_6036) * (double)h - 0.45;
				v = class_3532.method_16436((double)h, lv.field_5969, lv.field_6035) - p * r + q * 0.8;
				w = lv.method_5715() ? -0.1875 : 0.0;
			}

			double x = class_3532.method_16436((double)h, arg.field_6014, arg.field_5987);
			double y = class_3532.method_16436((double)h, arg.field_6036, arg.field_6010) + 0.25;
			double z = class_3532.method_16436((double)h, arg.field_5969, arg.field_6035);
			double aa = (double)((float)(t - x));
			double ab = (double)((float)(u - y)) + w;
			double ac = (double)((float)(v - z));
			GlStateManager.disableTexture();
			GlStateManager.disableLighting();
			lv3.method_1328(3, class_290.field_1576);
			int ad = 16;

			for (int ae = 0; ae <= 16; ae++) {
				float af = (float)ae / 16.0F;
				lv3.method_1315(d + aa * (double)af, e + ab * (double)(af * af + af) * 0.5 + 0.25, f + ac * (double)af).method_1323(0, 0, 0, 255).method_1344();
			}

			lv2.method_1350();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture();
			super.method_3936(arg, d, e, f, g, h);
		}
	}

	protected class_2960 method_3975(class_1536 arg) {
		return field_4707;
	}
}
