package net.minecraft;

import java.util.Random;

public class class_1264 {
	private static final Random field_5797 = new Random();

	public static void method_5451(class_1937 arg, class_2338 arg2, class_1263 arg3) {
		method_5450(arg, (double)arg2.method_10263(), (double)arg2.method_10264(), (double)arg2.method_10260(), arg3);
	}

	public static void method_5452(class_1937 arg, class_1297 arg2, class_1263 arg3) {
		method_5450(arg, arg2.field_5987, arg2.field_6010, arg2.field_6035, arg3);
	}

	private static void method_5450(class_1937 arg, double d, double e, double f, class_1263 arg2) {
		for (int i = 0; i < arg2.method_5439(); i++) {
			method_5449(arg, d, e, f, arg2.method_5438(i));
		}
	}

	public static void method_17349(class_1937 arg, class_2338 arg2, class_2371<class_1799> arg3) {
		arg3.forEach(arg3x -> method_5449(arg, (double)arg2.method_10263(), (double)arg2.method_10264(), (double)arg2.method_10260(), arg3x));
	}

	public static void method_5449(class_1937 arg, double d, double e, double f, class_1799 arg2) {
		double g = (double)class_1299.field_6052.method_17685();
		double h = 1.0 - g;
		double i = g / 2.0;
		double j = Math.floor(d) + field_5797.nextDouble() * h + i;
		double k = Math.floor(e) + field_5797.nextDouble() * h;
		double l = Math.floor(f) + field_5797.nextDouble() * h + i;

		while (!arg2.method_7960()) {
			class_1542 lv = new class_1542(arg, j, k, l, arg2.method_7971(field_5797.nextInt(21) + 10));
			float m = 0.05F;
			lv.method_18800(field_5797.nextGaussian() * 0.05F, field_5797.nextGaussian() * 0.05F + 0.2F, field_5797.nextGaussian() * 0.05F);
			arg.method_8649(lv);
		}
	}
}
