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
	public void method_3715(long l) {
		class_4184 lv = this.field_4613.field_1773.method_19418();
		class_1936 lv2 = this.field_4613.field_1687;
		double d = lv.method_19326().field_1352;
		double e = lv.method_19326().field_1351;
		double f = lv.method_19326().field_1350;
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_5119.SRC_ALPHA, GlStateManager.class_5118.ONE_MINUS_SRC_ALPHA, GlStateManager.class_5119.ONE, GlStateManager.class_5118.ZERO
		);
		GlStateManager.disableTexture();
		class_2338 lv3 = new class_2338(lv.method_19326().field_1352, 0.0, lv.method_19326().field_1350);
		class_289 lv4 = class_289.method_1348();
		class_287 lv5 = lv4.method_1349();
		lv5.method_1328(5, class_290.field_1576);

		for (class_2338 lv6 : class_2338.method_10097(lv3.method_10069(-40, 0, -40), lv3.method_10069(40, 0, 40))) {
			int i = lv2.method_8589(class_2902.class_2903.field_13194, lv6.method_10263(), lv6.method_10260());
			if (lv2.method_8320(lv6.method_10069(0, i, 0).method_10074()).method_11588()) {
				class_761.method_3253(
					lv5,
					(double)((float)lv6.method_10263() + 0.25F) - d,
					(double)i - e,
					(double)((float)lv6.method_10260() + 0.25F) - f,
					(double)((float)lv6.method_10263() + 0.75F) - d,
					(double)i + 0.09375 - e,
					(double)((float)lv6.method_10260() + 0.75F) - f,
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
					(double)((float)lv6.method_10260() + 0.25F) - f,
					(double)((float)lv6.method_10263() + 0.75F) - d,
					(double)i + 0.09375 - e,
					(double)((float)lv6.method_10260() + 0.75F) - f,
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
