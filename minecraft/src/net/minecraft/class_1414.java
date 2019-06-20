package net.minecraft;

import java.util.Random;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;

public class class_1414 {
	@Nullable
	public static class_243 method_6375(class_1314 arg, int i, int j) {
		return method_6376(arg, i, j, null);
	}

	@Nullable
	public static class_243 method_6378(class_1314 arg, int i, int j) {
		return method_19108(arg, i, j, arg::method_6149);
	}

	@Nullable
	public static class_243 method_19108(class_1314 arg, int i, int j, ToDoubleFunction<class_2338> toDoubleFunction) {
		return method_6371(arg, i, j, null, false, 0.0, toDoubleFunction);
	}

	@Nullable
	public static class_243 method_6373(class_1314 arg, int i, int j, class_243 arg2) {
		class_243 lv = arg2.method_1023(arg.field_5987, arg.field_6010, arg.field_6035);
		return method_6376(arg, i, j, lv);
	}

	@Nullable
	public static class_243 method_6377(class_1314 arg, int i, int j, class_243 arg2, double d) {
		class_243 lv = arg2.method_1023(arg.field_5987, arg.field_6010, arg.field_6035);
		return method_6371(arg, i, j, lv, true, d, arg::method_6149);
	}

	@Nullable
	public static class_243 method_20658(class_1314 arg, int i, int j, class_243 arg2) {
		class_243 lv = new class_243(arg.field_5987, arg.field_6010, arg.field_6035).method_1020(arg2);
		return method_6371(arg, i, j, lv, false, (float) (Math.PI / 2), arg::method_6149);
	}

	@Nullable
	public static class_243 method_6379(class_1314 arg, int i, int j, class_243 arg2) {
		class_243 lv = new class_243(arg.field_5987, arg.field_6010, arg.field_6035).method_1020(arg2);
		return method_6376(arg, i, j, lv);
	}

	@Nullable
	private static class_243 method_6376(class_1314 arg, int i, int j, @Nullable class_243 arg2) {
		return method_6371(arg, i, j, arg2, true, (float) (Math.PI / 2), arg::method_6149);
	}

	@Nullable
	private static class_243 method_6371(
		class_1314 arg, int i, int j, @Nullable class_243 arg2, boolean bl, double d, ToDoubleFunction<class_2338> toDoubleFunction
	) {
		class_1408 lv = arg.method_5942();
		Random random = arg.method_6051();
		boolean bl2;
		if (arg.method_18410()) {
			bl2 = arg.method_18412().method_19769(arg.method_19538(), (double)(arg.method_18413() + (float)i) + 1.0);
		} else {
			bl2 = false;
		}

		boolean bl3 = false;
		double e = Double.NEGATIVE_INFINITY;
		class_2338 lv2 = new class_2338(arg);

		for (int k = 0; k < 10; k++) {
			class_2338 lv3 = method_6374(random, i, j, arg2, d);
			if (lv3 != null) {
				int l = lv3.method_10263();
				int m = lv3.method_10264();
				int n = lv3.method_10260();
				if (arg.method_18410() && i > 1) {
					class_2338 lv4 = arg.method_18412();
					if (arg.field_5987 > (double)lv4.method_10263()) {
						l -= random.nextInt(i / 2);
					} else {
						l += random.nextInt(i / 2);
					}

					if (arg.field_6035 > (double)lv4.method_10260()) {
						n -= random.nextInt(i / 2);
					} else {
						n += random.nextInt(i / 2);
					}
				}

				class_2338 lv4x = new class_2338((double)l + arg.field_5987, (double)m + arg.field_6010, (double)n + arg.field_6035);
				if ((!bl2 || arg.method_18407(lv4x)) && lv.method_6333(lv4x)) {
					if (!bl) {
						lv4x = method_6372(lv4x, arg);
						if (method_6380(lv4x, arg)) {
							continue;
						}
					}

					double f = toDoubleFunction.applyAsDouble(lv4x);
					if (f > e) {
						e = f;
						lv2 = lv4x;
						bl3 = true;
					}
				}
			}
		}

		return bl3 ? new class_243(lv2) : null;
	}

	@Nullable
	private static class_2338 method_6374(Random random, int i, int j, @Nullable class_243 arg, double d) {
		if (arg != null && !(d >= Math.PI)) {
			double e = class_3532.method_15349(arg.field_1350, arg.field_1352) - (float) (Math.PI / 2);
			double f = e + (double)(2.0F * random.nextFloat() - 1.0F) * d;
			double g = Math.sqrt(random.nextDouble()) * (double)class_3532.field_15724 * (double)i;
			double h = -g * Math.sin(f);
			double n = g * Math.cos(f);
			if (!(Math.abs(h) > (double)i) && !(Math.abs(n) > (double)i)) {
				int o = random.nextInt(2 * j + 1) - j;
				return new class_2338(h, (double)o, n);
			} else {
				return null;
			}
		} else {
			int k = random.nextInt(2 * i + 1) - i;
			int l = random.nextInt(2 * j + 1) - j;
			int m = random.nextInt(2 * i + 1) - i;
			return new class_2338(k, l, m);
		}
	}

	private static class_2338 method_6372(class_2338 arg, class_1314 arg2) {
		if (!arg2.field_6002.method_8320(arg).method_11620().method_15799()) {
			return arg;
		} else {
			class_2338 lv = arg.method_10084();

			while (lv.method_10264() < arg2.field_6002.method_8322() && arg2.field_6002.method_8320(lv).method_11620().method_15799()) {
				lv = lv.method_10084();
			}

			return lv;
		}
	}

	private static boolean method_6380(class_2338 arg, class_1314 arg2) {
		return arg2.field_6002.method_8316(arg).method_15767(class_3486.field_15517);
	}
}
