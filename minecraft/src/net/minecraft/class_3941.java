package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3941 extends class_827<class_3924> {
	public void method_17581(class_3924 arg, double d, double e, double f, float g, int i) {
		class_2371<class_1799> lv = arg.method_17505();

		for (int j = 0; j < lv.size(); j++) {
			class_1799 lv2 = lv.get(j);
			if (lv2 != class_1799.field_8037) {
				GlStateManager.pushMatrix();
				GlStateManager.translatef((float)d + 0.5F, (float)e + 0.32291666F, (float)f + 0.5F);
				class_2350 lv3 = class_2350.method_10139(j);
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(lv3.method_10144(), 0.0F, 0.0F, 1.0F);
				GlStateManager.translatef(0.0F, -0.33333334F, 0.0F);
				GlStateManager.scalef(0.33333334F, 0.33333334F, 0.33333334F);
				class_310.method_1551().method_1480().method_4009(lv2, class_809.class_811.field_4319);
				GlStateManager.popMatrix();
			}
		}
	}
}
