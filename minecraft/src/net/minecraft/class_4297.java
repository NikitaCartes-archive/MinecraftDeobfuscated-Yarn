package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4297 extends class_827<class_3719> {
	public void method_20282(class_3719 arg, double d, double e, double f, float g, int i) {
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)d + 0.5F, (float)e, (float)f + 0.5F);
		this.method_3566(class_1059.field_5275);
		class_310 lv = class_310.method_1551();
		class_1297 lv2 = lv.method_1560();
		class_241 lv3 = class_241.field_1340;
		if (lv2 != null) {
			lv3 = lv2.method_5802();
		}

		GlStateManager.rotatef(-lv3.field_1342, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
		class_1058 lv4 = lv.method_1549().method_4608(new class_2960("block/barrel_side"));
		class_1058 lv5 = lv.method_1549().method_4608(new class_2960("block/fire_0"));
		class_289 lv6 = class_289.method_1348();
		class_287 lv7 = lv6.method_1349();
		lv7.method_1328(7, class_290.field_1575);
		lv7.method_1315(-0.5, 0.0, 0.0).method_1312((double)lv4.method_4594(), (double)lv4.method_4575()).method_1323(255, 255, 255, 255).method_1344();
		lv7.method_1315(0.5, 0.0, 0.0).method_1312((double)lv4.method_4577(), (double)lv4.method_4575()).method_1323(255, 255, 255, 255).method_1344();
		lv7.method_1315(0.5, -1.0, 0.0).method_1312((double)lv4.method_4577(), (double)lv4.method_4593()).method_1323(255, 255, 255, 255).method_1344();
		lv7.method_1315(-0.5, -1.0, 0.0).method_1312((double)lv4.method_4594(), (double)lv4.method_4593()).method_1323(255, 255, 255, 255).method_1344();
		lv7.method_1331(0.0, -1.0, 0.0);
		lv7.method_1315(-0.5, 0.0, 0.0).method_1312((double)lv5.method_4594(), (double)lv5.method_4575()).method_1323(255, 255, 255, 255).method_1344();
		lv7.method_1315(0.5, 0.0, 0.0).method_1312((double)lv5.method_4577(), (double)lv5.method_4575()).method_1323(255, 255, 255, 255).method_1344();
		lv7.method_1315(0.5, -1.0, 0.0).method_1312((double)lv5.method_4577(), (double)lv5.method_4593()).method_1323(255, 255, 255, 255).method_1344();
		lv7.method_1315(-0.5, -1.0, 0.0).method_1312((double)lv5.method_4594(), (double)lv5.method_4593()).method_1323(255, 255, 255, 255).method_1344();
		lv7.method_1331(0.0, 0.0, 0.0);
		lv6.method_1350();
		GlStateManager.popMatrix();
	}
}
