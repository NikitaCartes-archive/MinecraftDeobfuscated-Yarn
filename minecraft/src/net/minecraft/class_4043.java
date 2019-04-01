package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4043 extends class_3887<class_4019, class_4041<class_4019>> {
	public class_4043(class_3883<class_4019, class_4041<class_4019>> arg) {
		super(arg);
	}

	public void method_18335(class_4019 arg, float f, float g, float h, float i, float j, float k, float l) {
		class_1799 lv = arg.method_6118(class_1304.field_6173);
		if (!lv.method_7960()) {
			boolean bl = arg.method_6113();
			boolean bl2 = arg.method_6109();
			float m = -0.14F;
			float n = -0.7F;
			float o = 1.3F;
			if (bl2) {
				m = -0.01F;
				o = 1.355F;
				n = -0.37F;
				if (arg.method_18272()) {
					o = 1.19F;
					n = -0.4F;
				} else if (bl) {
					m = 0.55F;
					o = 1.5F;
					n = 0.125F;
				} else if (arg.method_18277()) {
					m = -0.08F;
					o = 1.32F;
				}
			} else if (arg.method_18272()) {
				o = 0.9F;
				n = -0.52F;
			} else if (bl) {
				m = 0.6F;
				n = 0.0F;
				o = 1.5F;
			} else if (arg.method_18277()) {
				m = -0.225F;
				o = 1.25F;
			}

			if (arg.method_18276()) {
				o += arg.method_18300(h) / (float)(bl2 ? 22 : 16);
			}

			GlStateManager.pushMatrix();
			GlStateManager.translatef(m, o, n);
			GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
			if (bl) {
				GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
			}

			GlStateManager.rotatef(arg.method_18298(h) * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
			class_310.method_1551().method_1480().method_4016(lv, arg, class_809.class_811.field_4318, false);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
