package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_839 extends class_827<class_2636> {
	public void method_3590(class_2636 arg, double d, double e, double f, float g, int i) {
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)d + 0.5F, (float)e, (float)f + 0.5F);
		method_3589(arg.method_11390(), d, e, f, g);
		GlStateManager.popMatrix();
	}

	public static void method_3589(class_1917 arg, double d, double e, double f, float g) {
		class_1297 lv = arg.method_8283();
		if (lv != null) {
			float h = 0.53125F;
			float i = Math.max(lv.field_5998, lv.field_6019);
			if ((double)i > 1.0) {
				h /= i;
			}

			GlStateManager.translatef(0.0F, 0.4F, 0.0F);
			GlStateManager.rotatef((float)class_3532.method_16436((double)g, arg.method_8279(), arg.method_8278()) * 10.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translatef(0.0F, -0.2F, 0.0F);
			GlStateManager.rotatef(-30.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.scalef(h, h, h);
			lv.method_5808(d, e, f, 0.0F, 0.0F);
			class_310.method_1551().method_1561().method_3954(lv, 0.0, 0.0, 0.0, 0.0F, g, false);
		}
	}
}
