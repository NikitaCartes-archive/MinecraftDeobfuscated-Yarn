package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public class class_1414 {
	@Nullable
	public static class_243 method_6375(class_1314 arg, int i, int j) {
		return method_6376(arg, i, j, null);
	}

	@Nullable
	public static class_243 method_6378(class_1314 arg, int i, int j) {
		return method_6371(arg, i, j, null, false, 0.0);
	}

	@Nullable
	public static class_243 method_6373(class_1314 arg, int i, int j, class_243 arg2) {
		class_243 lv = arg2.method_1023(arg.field_5987, arg.field_6010, arg.field_6035);
		return method_6376(arg, i, j, lv);
	}

	@Nullable
	public static class_243 method_6377(class_1314 arg, int i, int j, class_243 arg2, double d) {
		class_243 lv = arg2.method_1023(arg.field_5987, arg.field_6010, arg.field_6035);
		return method_6371(arg, i, j, lv, true, d);
	}

	@Nullable
	public static class_243 method_6379(class_1314 arg, int i, int j, class_243 arg2) {
		class_243 lv = new class_243(arg.field_5987, arg.field_6010, arg.field_6035).method_1020(arg2);
		return method_6376(arg, i, j, lv);
	}

	@Nullable
	private static class_243 method_6376(class_1314 arg, int i, int j, @Nullable class_243 arg2) {
		return method_6371(arg, i, j, arg2, true, (float) (Math.PI / 2));
	}

	@Nullable
	private static class_243 method_6371(class_1314 arg, int i, int j, @Nullable class_243 arg2, boolean bl, double d) {
		class_1408 lv = arg.method_5942();
		Random random = arg.method_6051();
		boolean bl2;
		if (arg.method_6151()) {
			double e = arg.method_6141()
					.method_10261(
						(double)class_3532.method_15357(arg.field_5987), (double)class_3532.method_15357(arg.field_6010), (double)class_3532.method_15357(arg.field_6035)
					)
				+ 4.0;
			double f = (double)(arg.method_6143() + (float)i);
			bl2 = e < f * f;
		} else {
			bl2 = false;
		}

		boolean bl3 = false;
		float g = -99999.0F;
		int k = 0;
		int l = 0;
		int m = 0;

		for (int n = 0; n < 10; n++) {
			class_2338 lv2 = method_6374(random, i, j, arg2, d);
			if (lv2 != null) {
				int o = lv2.method_10263();
				int p = lv2.method_10264();
				int q = lv2.method_10260();
				if (arg.method_6151() && i > 1) {
					class_2338 lv3 = arg.method_6141();
					if (arg.field_5987 > (double)lv3.method_10263()) {
						o -= random.nextInt(i / 2);
					} else {
						o += random.nextInt(i / 2);
					}

					if (arg.field_6035 > (double)lv3.method_10260()) {
						q -= random.nextInt(i / 2);
					} else {
						q += random.nextInt(i / 2);
					}
				}

				class_2338 lv3x = new class_2338((double)o + arg.field_5987, (double)p + arg.field_6010, (double)q + arg.field_6035);
				if ((!bl2 || arg.method_6146(lv3x)) && lv.method_6333(lv3x)) {
					if (!bl) {
						lv3x = method_6372(lv3x, arg);
						if (method_6380(lv3x, arg)) {
							continue;
						}
					}

					float h = arg.method_6149(lv3x);
					if (h > g) {
						g = h;
						k = o;
						l = p;
						m = q;
						bl3 = true;
					}
				}
			}
		}

		return bl3 ? new class_243((double)k + arg.field_5987, (double)l + arg.field_6010, (double)m + arg.field_6035) : null;
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
