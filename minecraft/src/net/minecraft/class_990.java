package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_990 extends class_3887<class_1440, class_586<class_1440>> {
	public class_990(class_3883<class_1440, class_586<class_1440>> arg) {
		super(arg);
	}

	public void method_4194(class_1440 arg, float f, float g, float h, float i, float j, float k, float l) {
		class_1799 lv = arg.method_6118(class_1304.field_6173);
		if (arg.method_6535() && !lv.method_7960() && !arg.method_6524()) {
			float m = -0.6F;
			float n = 1.4F;
			if (arg.method_6527()) {
				m -= 0.2F * class_3532.method_15374(i * 0.6F) + 0.2F;
				n -= 0.09F * class_3532.method_15374(i * 0.6F);
			}

			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.1F, n, m);
			class_310.method_1551().method_1480().method_4016(lv, arg, class_809.class_811.field_4318, false);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
