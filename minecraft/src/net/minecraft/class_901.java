package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_901 extends class_897<class_1540> {
	public class_901(class_898 arg) {
		super(arg);
		this.field_4673 = 0.5F;
	}

	public void method_3965(class_1540 arg, double d, double e, double f, float g, float h) {
		class_2680 lv = arg.method_6962();
		if (lv.method_11610() == class_2464.field_11458) {
			class_1937 lv2 = arg.method_6966();
			if (lv != lv2.method_8320(new class_2338(arg)) && lv.method_11610() != class_2464.field_11455) {
				this.method_3924(class_1059.field_5275);
				GlStateManager.pushMatrix();
				GlStateManager.disableLighting();
				class_289 lv3 = class_289.method_1348();
				class_287 lv4 = lv3.method_1349();
				if (this.field_4674) {
					GlStateManager.enableColorMaterial();
					GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(arg));
				}

				lv4.method_1328(7, class_290.field_1582);
				class_2338 lv5 = new class_2338(arg.field_5987, arg.method_5829().field_1325, arg.field_6035);
				GlStateManager.translatef(
					(float)(d - (double)lv5.method_10263() - 0.5), (float)(e - (double)lv5.method_10264()), (float)(f - (double)lv5.method_10260() - 0.5)
				);
				class_776 lv6 = class_310.method_1551().method_1541();
				lv6.method_3350().method_3374(lv2, lv6.method_3349(lv), lv, lv5, lv4, false, new Random(), lv.method_11617(arg.method_6964()));
				lv3.method_1350();
				if (this.field_4674) {
					GlStateManager.tearDownSolidRenderingTextureCombine();
					GlStateManager.disableColorMaterial();
				}

				GlStateManager.enableLighting();
				GlStateManager.popMatrix();
				super.method_3936(arg, d, e, f, g, h);
			}
		}
	}

	protected class_2960 method_3964(class_1540 arg) {
		return class_1059.field_5275;
	}
}
