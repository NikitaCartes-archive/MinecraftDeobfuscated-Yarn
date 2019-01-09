package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_867 implements class_863.class_864 {
	private final class_310 field_4613;

	public class_867(class_310 arg) {
		this.field_4613 = arg;
	}

	@Override
	public void method_3715(float f, long l) {
		class_1657 lv = this.field_4613.field_1724;
		class_1936 lv2 = this.field_4613.field_1687;
		double d = class_3532.method_16436((double)f, lv.field_6038, lv.field_5987);
		double e = class_3532.method_16436((double)f, lv.field_5971, lv.field_6010);
		double g = class_3532.method_16436((double)f, lv.field_5989, lv.field_6035);
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
		);
		GlStateManager.disableTexture();
		class_2338 lv3 = new class_2338(lv.field_5987, 0.0, lv.field_6035);
		Iterable<class_2338> iterable = class_2338.method_10097(lv3.method_10069(-40, 0, -40), lv3.method_10069(40, 0, 40));
		class_289 lv4 = class_289.method_1348();
		class_287 lv5 = lv4.method_1349();
		lv5.method_1328(5, class_290.field_1576);

		for (class_2338 lv6 : iterable) {
			int i = lv2.method_8589(class_2902.class_2903.field_13194, lv6.method_10263(), lv6.method_10260());
			if (lv2.method_8320(lv6.method_10069(0, i, 0).method_10074()).method_11588()) {
				class_761.method_3253(
					lv5,
					(double)((float)lv6.method_10263() + 0.25F) - d,
					(double)i - e,
					(double)((float)lv6.method_10260() + 0.25F) - g,
					(double)((float)lv6.method_10263() + 0.75F) - d,
					(double)i + 0.09375 - e,
					(double)((float)lv6.method_10260() + 0.75F) - g,
					0.0F,
					0.0F,
					1.0F,
					0.5F
				);
			} else {
				class_761.method_3253(
					lv5,
					(double)((float)lv6.method_10263() + 0.25F) - d,
					(double)i - e,
					(double)((float)lv6.method_10260() + 0.25F) - g,
					(double)((float)lv6.method_10263() + 0.75F) - d,
					(double)i + 0.09375 - e,
					(double)((float)lv6.method_10260() + 0.75F) - g,
					0.0F,
					1.0F,
					0.0F,
					0.5F
				);
			}
		}

		lv4.method_1350();
		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}
}
