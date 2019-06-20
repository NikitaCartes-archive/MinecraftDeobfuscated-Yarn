package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_972 extends class_3887<class_742, class_591<class_742>> {
	public class_972(class_3883<class_742, class_591<class_742>> arg) {
		super(arg);
	}

	public void method_4177(class_742 arg, float f, float g, float h, float i, float j, float k, float l) {
		if (arg.method_3125() && !arg.method_5767() && arg.method_7348(class_1664.field_7559) && arg.method_3119() != null) {
			class_1799 lv = arg.method_6118(class_1304.field_6174);
			if (lv.method_7909() != class_1802.field_8833) {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.method_17164(arg.method_3119());
				GlStateManager.pushMatrix();
				GlStateManager.translatef(0.0F, 0.0F, 0.125F);
				double d = class_3532.method_16436((double)h, arg.field_7524, arg.field_7500) - class_3532.method_16436((double)h, arg.field_6014, arg.field_5987);
				double e = class_3532.method_16436((double)h, arg.field_7502, arg.field_7521) - class_3532.method_16436((double)h, arg.field_6036, arg.field_6010);
				double m = class_3532.method_16436((double)h, arg.field_7522, arg.field_7499) - class_3532.method_16436((double)h, arg.field_5969, arg.field_6035);
				float n = arg.field_6220 + (arg.field_6283 - arg.field_6220);
				double o = (double)class_3532.method_15374(n * (float) (Math.PI / 180.0));
				double p = (double)(-class_3532.method_15362(n * (float) (Math.PI / 180.0)));
				float q = (float)e * 10.0F;
				q = class_3532.method_15363(q, -6.0F, 32.0F);
				float r = (float)(d * o + m * p) * 100.0F;
				r = class_3532.method_15363(r, 0.0F, 150.0F);
				float s = (float)(d * p - m * o) * 100.0F;
				s = class_3532.method_15363(s, -20.0F, 20.0F);
				if (r < 0.0F) {
					r = 0.0F;
				}

				float t = class_3532.method_16439(h, arg.field_7505, arg.field_7483);
				q += class_3532.method_15374(class_3532.method_16439(h, arg.field_6039, arg.field_5973) * 6.0F) * 32.0F * t;
				if (arg.method_20231()) {
					q += 25.0F;
				}

				GlStateManager.rotatef(6.0F + r / 2.0F + q, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(s / 2.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotatef(-s / 2.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
				this.method_17165().method_2823(0.0625F);
				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
