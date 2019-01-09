package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_295 {
	private static class_243 field_1610 = new class_243(0.0, 0.0, 0.0);
	private static float field_1608;
	private static float field_1607;
	private static float field_1606;
	private static float field_1605;
	private static float field_1611;

	public static void method_1373(class_1657 arg, boolean bl, float f, class_857 arg2) {
		class_1159 lv = new class_1159(arg2.field_4500);
		lv.method_4941();
		float g = 0.05F;
		float h = f * class_3532.field_15724;
		class_1162 lv2 = new class_1162(0.0F, 0.0F, -2.0F * h * 0.05F / (h + 0.05F), 1.0F);
		lv2.method_4960(lv);
		field_1610 = new class_243((double)lv2.method_4953(), (double)lv2.method_4956(), (double)lv2.method_4957());
		float i = arg.field_5965;
		float j = arg.field_6031;
		int k = bl ? -1 : 1;
		field_1608 = class_3532.method_15362(j * (float) (Math.PI / 180.0)) * (float)k;
		field_1606 = class_3532.method_15374(j * (float) (Math.PI / 180.0)) * (float)k;
		field_1605 = -field_1606 * class_3532.method_15374(i * (float) (Math.PI / 180.0)) * (float)k;
		field_1611 = field_1608 * class_3532.method_15374(i * (float) (Math.PI / 180.0)) * (float)k;
		field_1607 = class_3532.method_15362(i * (float) (Math.PI / 180.0));
	}

	public static class_243 method_1379(class_1297 arg, double d) {
		double e = class_3532.method_16436(d, arg.field_6014, arg.field_5987);
		double f = class_3532.method_16436(d, arg.field_6036, arg.field_6010);
		double g = class_3532.method_16436(d, arg.field_5969, arg.field_6035);
		double h = e + field_1610.field_1352;
		double i = f + field_1610.field_1351;
		double j = g + field_1610.field_1350;
		return new class_243(h, i, j);
	}

	public static class_2680 method_1376(class_1922 arg, class_1297 arg2, float f) {
		class_243 lv = method_1379(arg2, (double)f);
		class_2338 lv2 = new class_2338(lv);
		class_2680 lv3 = arg.method_8320(lv2);
		class_3610 lv4 = arg.method_8316(lv2);
		if (!lv4.method_15769()) {
			float g = (float)lv2.method_10264() + lv4.method_15763() + 0.11111111F;
			if (lv.field_1351 >= (double)g) {
				lv3 = arg.method_8320(lv2.method_10084());
			}
		}

		return lv3;
	}

	public static class_3610 method_1374(class_1922 arg, class_1297 arg2, float f) {
		class_243 lv = method_1379(arg2, (double)f);
		class_2338 lv2 = new class_2338(lv);
		class_3610 lv3 = arg.method_8316(lv2);
		if (!lv3.method_15769()) {
			float g = (float)lv2.method_10264() + lv3.method_15763() + 0.11111111F;
			if (lv.field_1351 >= (double)g) {
				lv3 = arg.method_8316(lv2.method_10084());
			}
		}

		return lv3;
	}

	public static float method_1375() {
		return field_1608;
	}

	public static float method_1377() {
		return field_1607;
	}

	public static float method_1380() {
		return field_1606;
	}

	public static float method_1381() {
		return field_1605;
	}

	public static float method_1378() {
		return field_1611;
	}
}
