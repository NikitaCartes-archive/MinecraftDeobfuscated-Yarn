package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3507 extends class_3506 {
	private static final class_2680 field_15629 = class_2246.field_10611.method_9564();
	private static final class_2680 field_15628 = class_2246.field_10184.method_9564();
	private static final class_2680 field_15630 = class_2246.field_10415.method_9564();

	public class_3507(Function<Dynamic<?>, ? extends class_3527> function) {
		super(function);
	}

	@Override
	public void method_15208(
		Random random, class_2791 arg, class_1959 arg2, int i, int j, int k, double d, class_2680 arg3, class_2680 arg4, int l, long m, class_3527 arg5
	) {
		double e = 0.0;
		double f = Math.min(Math.abs(d), this.field_15623.method_15437((double)i * 0.25, (double)j * 0.25));
		if (f > 0.0) {
			double g = 0.001953125;
			double h = Math.abs(this.field_15618.method_15437((double)i * 0.001953125, (double)j * 0.001953125));
			e = f * f * 2.5;
			double n = Math.ceil(h * 50.0) + 14.0;
			if (e > n) {
				e = n;
			}

			e += 64.0;
		}

		int o = i & 15;
		int p = j & 15;
		class_2680 lv = field_15629;
		class_2680 lv2 = arg2.method_8722().method_15336();
		int q = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		boolean bl = Math.cos(d / 3.0 * Math.PI) > 0.0;
		int r = -1;
		boolean bl2 = false;
		class_2338.class_2339 lv3 = new class_2338.class_2339();

		for (int s = Math.max(k, (int)e + 1); s >= 0; s--) {
			lv3.method_10103(o, s, p);
			if (arg.method_8320(lv3).method_11588() && s < (int)e) {
				arg.method_12010(lv3, arg3, false);
			}

			class_2680 lv4 = arg.method_8320(lv3);
			if (lv4.method_11588()) {
				r = -1;
			} else if (lv4.method_11614() == arg3.method_11614()) {
				if (r == -1) {
					bl2 = false;
					if (q <= 0) {
						lv = class_2246.field_10124.method_9564();
						lv2 = arg3;
					} else if (s >= l - 4 && s <= l + 1) {
						lv = field_15629;
						lv2 = arg2.method_8722().method_15336();
					}

					if (s < l && (lv == null || lv.method_11588())) {
						lv = arg4;
					}

					r = q + Math.max(0, s - l);
					if (s >= l - 1) {
						if (s > l + 3 + q) {
							class_2680 lv5;
							if (s < 64 || s > 127) {
								lv5 = field_15628;
							} else if (bl) {
								lv5 = field_15630;
							} else {
								lv5 = this.method_15207(i, s, j);
							}

							arg.method_12010(lv3, lv5, false);
						} else {
							arg.method_12010(lv3, arg2.method_8722().method_15337(), false);
							bl2 = true;
						}
					} else {
						arg.method_12010(lv3, lv2, false);
						class_2248 lv6 = lv2.method_11614();
						if (lv6 == class_2246.field_10611
							|| lv6 == class_2246.field_10184
							|| lv6 == class_2246.field_10015
							|| lv6 == class_2246.field_10325
							|| lv6 == class_2246.field_10143
							|| lv6 == class_2246.field_10014
							|| lv6 == class_2246.field_10444
							|| lv6 == class_2246.field_10349
							|| lv6 == class_2246.field_10590
							|| lv6 == class_2246.field_10235
							|| lv6 == class_2246.field_10570
							|| lv6 == class_2246.field_10409
							|| lv6 == class_2246.field_10123
							|| lv6 == class_2246.field_10526
							|| lv6 == class_2246.field_10328
							|| lv6 == class_2246.field_10626) {
							arg.method_12010(lv3, field_15628, false);
						}
					}
				} else if (r > 0) {
					r--;
					if (bl2) {
						arg.method_12010(lv3, field_15628, false);
					} else {
						arg.method_12010(lv3, this.method_15207(i, s, j), false);
					}
				}
			}
		}
	}
}
