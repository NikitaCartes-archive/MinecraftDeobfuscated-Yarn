package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_872 implements class_863.class_864 {
	private final class_310 field_4629;

	public class_872(class_310 arg) {
		this.field_4629 = arg;
	}

	@Override
	public void method_3715(long l) {
		class_4184 lv = this.field_4629.field_1773.method_19418();
		double d = lv.method_19326().field_1352;
		double e = lv.method_19326().field_1351;
		double f = lv.method_19326().field_1350;
		class_2338 lv2 = this.field_4629.field_1724.method_5704();
		class_1941 lv3 = this.field_4629.field_1724.field_6002;
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.color4f(0.0F, 1.0F, 0.0F, 0.75F);
		GlStateManager.disableTexture();
		GlStateManager.lineWidth(6.0F);

		for (class_2338 lv4 : class_2338.method_10097(lv2.method_10069(-10, -10, -10), lv2.method_10069(10, 10, 10))) {
			class_3610 lv5 = lv3.method_8316(lv4);
			if (lv5.method_15767(class_3486.field_15517)) {
				double g = (double)((float)lv4.method_10264() + lv5.method_15763(lv3, lv4));
				class_863.method_19695(
					new class_238(
							(double)((float)lv4.method_10263() + 0.01F),
							(double)((float)lv4.method_10264() + 0.01F),
							(double)((float)lv4.method_10260() + 0.01F),
							(double)((float)lv4.method_10263() + 0.99F),
							g,
							(double)((float)lv4.method_10260() + 0.99F)
						)
						.method_989(-d, -e, -f),
					1.0F,
					1.0F,
					1.0F,
					0.2F
				);
			}
		}

		for (class_2338 lv4x : class_2338.method_10097(lv2.method_10069(-10, -10, -10), lv2.method_10069(10, 10, 10))) {
			class_3610 lv5 = lv3.method_8316(lv4x);
			if (lv5.method_15767(class_3486.field_15517)) {
				class_863.method_3714(
					String.valueOf(lv5.method_15761()),
					(double)lv4x.method_10263() + 0.5,
					(double)((float)lv4x.method_10264() + lv5.method_15763(lv3, lv4x)),
					(double)lv4x.method_10260() + 0.5,
					-16777216
				);
			}
		}

		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
}
