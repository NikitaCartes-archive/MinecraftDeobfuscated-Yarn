package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3941 extends class_827<class_3924> {
	public void method_17581(class_3924 arg, double d, double e, double f, float g, int i) {
		class_2350 lv = arg.method_11010().method_11654(class_3922.field_17564);
		class_2371<class_1799> lv2 = arg.method_17505();

		for (int j = 0; j < lv2.size(); j++) {
			class_1799 lv3 = lv2.get(j);
			if (lv3 != class_1799.field_8037) {
				GlStateManager.pushMatrix();
				GlStateManager.translatef((float)d + 0.5F, (float)e + 0.44921875F, (float)f + 0.5F);
				class_2350 lv4 = class_2350.method_10139((j + lv.method_10161()) % 4);
				GlStateManager.rotatef(-lv4.method_10144(), 0.0F, 1.0F, 0.0F);
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.translatef(-0.3125F, -0.3125F, 0.0F);
				GlStateManager.scalef(0.375F, 0.375F, 0.375F);
				class_310.method_1551().method_1480().method_4009(lv3, class_809.class_811.field_4319);
				GlStateManager.popMatrix();
			}
		}
	}
}
