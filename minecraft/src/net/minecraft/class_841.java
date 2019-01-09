package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_841 extends class_840 {
	private static final class_2960 field_4409 = new class_2960("textures/entity/end_gateway_beam.png");

	@Override
	public void method_3591(class_2640 arg, double d, double e, double f, float g, int i) {
		GlStateManager.disableFog();
		class_2643 lv = (class_2643)arg;
		if (lv.method_11420() || lv.method_11421()) {
			GlStateManager.alphaFunc(516, 0.1F);
			this.method_3566(field_4409);
			float h = lv.method_11420() ? lv.method_11417(g) : lv.method_11412(g);
			double j = lv.method_11420() ? 256.0 - e : 50.0;
			h = class_3532.method_15374(h * (float) Math.PI);
			int k = class_3532.method_15357((double)h * j);
			float[] fs = lv.method_11420() ? class_1767.field_7958.method_7787() : class_1767.field_7945.method_7787();
			class_822.method_3545(d, e, f, (double)g, (double)h, lv.method_10997().method_8510(), 0, k, fs, 0.15, 0.175);
			class_822.method_3545(d, e, f, (double)g, (double)h, lv.method_10997().method_8510(), 0, -k, fs, 0.15, 0.175);
		}

		super.method_3591(arg, d, e, f, g, i);
		GlStateManager.enableFog();
	}

	@Override
	protected int method_3592(double d) {
		return super.method_3592(d) + 1;
	}

	@Override
	protected float method_3594() {
		return 1.0F;
	}
}
