package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_927<T extends class_1308, M extends class_583<T>> extends class_922<T, M> {
	public class_927(class_898 arg, M arg2, float f) {
		super(arg, arg2, f);
	}

	protected boolean method_4071(T arg) {
		return super.method_4055(arg) && (arg.method_5733() || arg.method_16914() && arg == this.field_4676.field_4678);
	}

	public boolean method_4068(T arg, class_856 arg2, double d, double e, double f) {
		if (super.method_3933(arg, arg2, d, e, f)) {
			return true;
		} else {
			class_1297 lv = arg.method_5933();
			return lv != null ? arg2.method_3699(lv.method_5830()) : false;
		}
	}

	public void method_4072(T arg, double d, double e, double f, float g, float h) {
		super.method_4054(arg, d, e, f, g, h);
		if (!this.field_4674) {
			this.method_4073(arg, d, e, f, g, h);
		}
	}

	protected void method_4073(T arg, double d, double e, double f, float g, float h) {
		class_1297 lv = arg.method_5933();
		if (lv != null) {
			e -= (1.6 - (double)arg.method_17682()) * 0.5;
			class_289 lv2 = class_289.method_1348();
			class_287 lv3 = lv2.method_1349();
			double i = (double)(class_3532.method_16439(h * 0.5F, lv.field_6031, lv.field_5982) * (float) (Math.PI / 180.0));
			double j = (double)(class_3532.method_16439(h * 0.5F, lv.field_5965, lv.field_6004) * (float) (Math.PI / 180.0));
			double k = Math.cos(i);
			double l = Math.sin(i);
			double m = Math.sin(j);
			if (lv instanceof class_1530) {
				k = 0.0;
				l = 0.0;
				m = -1.0;
			}

			double n = Math.cos(j);
			double o = class_3532.method_16436((double)h, lv.field_6014, lv.field_5987) - k * 0.7 - l * 0.5 * n;
			double p = class_3532.method_16436((double)h, lv.field_6036 + (double)lv.method_5751() * 0.7, lv.field_6010 + (double)lv.method_5751() * 0.7)
				- m * 0.5
				- 0.25;
			double q = class_3532.method_16436((double)h, lv.field_5969, lv.field_6035) - l * 0.7 + k * 0.5 * n;
			double r = (double)(class_3532.method_16439(h, arg.field_6283, arg.field_6220) * (float) (Math.PI / 180.0)) + (Math.PI / 2);
			k = Math.cos(r) * (double)arg.method_17681() * 0.4;
			l = Math.sin(r) * (double)arg.method_17681() * 0.4;
			double s = class_3532.method_16436((double)h, arg.field_6014, arg.field_5987) + k;
			double t = class_3532.method_16436((double)h, arg.field_6036, arg.field_6010);
			double u = class_3532.method_16436((double)h, arg.field_5969, arg.field_6035) + l;
			d += k;
			f += l;
			double v = (double)((float)(o - s));
			double w = (double)((float)(p - t));
			double x = (double)((float)(q - u));
			GlStateManager.disableTexture();
			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			int y = 24;
			double z = 0.025;
			lv3.method_1328(5, class_290.field_1576);

			for (int aa = 0; aa <= 24; aa++) {
				float ab = 0.5F;
				float ac = 0.4F;
				float ad = 0.3F;
				if (aa % 2 == 0) {
					ab *= 0.7F;
					ac *= 0.7F;
					ad *= 0.7F;
				}

				float ae = (float)aa / 24.0F;
				lv3.method_1315(d + v * (double)ae + 0.0, e + w * (double)(ae * ae + ae) * 0.5 + (double)((24.0F - (float)aa) / 18.0F + 0.125F), f + x * (double)ae)
					.method_1336(ab, ac, ad, 1.0F)
					.method_1344();
				lv3.method_1315(
						d + v * (double)ae + 0.025, e + w * (double)(ae * ae + ae) * 0.5 + (double)((24.0F - (float)aa) / 18.0F + 0.125F) + 0.025, f + x * (double)ae
					)
					.method_1336(ab, ac, ad, 1.0F)
					.method_1344();
			}

			lv2.method_1350();
			lv3.method_1328(5, class_290.field_1576);

			for (int aa = 0; aa <= 24; aa++) {
				float ab = 0.5F;
				float ac = 0.4F;
				float ad = 0.3F;
				if (aa % 2 == 0) {
					ab *= 0.7F;
					ac *= 0.7F;
					ad *= 0.7F;
				}

				float ae = (float)aa / 24.0F;
				lv3.method_1315(d + v * (double)ae + 0.0, e + w * (double)(ae * ae + ae) * 0.5 + (double)((24.0F - (float)aa) / 18.0F + 0.125F) + 0.025, f + x * (double)ae)
					.method_1336(ab, ac, ad, 1.0F)
					.method_1344();
				lv3.method_1315(
						d + v * (double)ae + 0.025, e + w * (double)(ae * ae + ae) * 0.5 + (double)((24.0F - (float)aa) / 18.0F + 0.125F), f + x * (double)ae + 0.025
					)
					.method_1336(ab, ac, ad, 1.0F)
					.method_1344();
			}

			lv2.method_1350();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture();
			GlStateManager.enableCull();
		}
	}
}
