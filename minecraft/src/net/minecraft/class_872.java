package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_872 implements class_863.class_864 {
	private final class_310 field_4629;
	private class_1657 field_4632;
	private double field_4631;
	private double field_4630;
	private double field_4633;

	public class_872(class_310 arg) {
		this.field_4629 = arg;
	}

	@Override
	public void method_3715(float f, long l) {
		this.field_4632 = this.field_4629.field_1724;
		this.field_4631 = class_3532.method_16436((double)f, this.field_4632.field_6038, this.field_4632.field_5987);
		this.field_4630 = class_3532.method_16436((double)f, this.field_4632.field_5971, this.field_4632.field_6010);
		this.field_4633 = class_3532.method_16436((double)f, this.field_4632.field_5989, this.field_4632.field_6035);
		class_2338 lv = this.field_4629.field_1724.method_5704();
		class_1941 lv2 = this.field_4629.field_1724.field_6002;
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
		);
		GlStateManager.color4f(0.0F, 1.0F, 0.0F, 0.75F);
		GlStateManager.disableTexture();
		GlStateManager.lineWidth(6.0F);

		for (class_2338 lv3 : class_2338.method_10097(lv.method_10069(-10, -10, -10), lv.method_10069(10, 10, 10))) {
			class_3610 lv4 = lv2.method_8316(lv3);
			if (lv4.method_15767(class_3486.field_15517)) {
				double d = (double)((float)lv3.method_10264() + lv4.method_15763());
				class_761.method_3261(
					new class_238(
							(double)((float)lv3.method_10263() + 0.01F),
							(double)((float)lv3.method_10264() + 0.01F),
							(double)((float)lv3.method_10260() + 0.01F),
							(double)((float)lv3.method_10263() + 0.99F),
							d,
							(double)((float)lv3.method_10260() + 0.99F)
						)
						.method_989(-this.field_4631, -this.field_4630, -this.field_4633),
					1.0F,
					1.0F,
					1.0F,
					0.2F
				);
			}
		}

		for (class_2338 lv3x : class_2338.method_10097(lv.method_10069(-10, -10, -10), lv.method_10069(10, 10, 10))) {
			class_3610 lv4 = lv2.method_8316(lv3x);
			if (lv4.method_15767(class_3486.field_15517)) {
				class_863.method_3714(
					String.valueOf(lv4.method_15761()),
					(double)lv3x.method_10263() + 0.5,
					(double)((float)lv3x.method_10264() + lv4.method_15763()),
					(double)lv3x.method_10260() + 0.5,
					f,
					-16777216
				);
			}
		}

		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
}
