package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3535 extends class_3506 {
	private static final class_2680 field_15741 = class_2246.field_10611.method_9564();
	private static final class_2680 field_15740 = class_2246.field_10184.method_9564();
	private static final class_2680 field_15742 = class_2246.field_10415.method_9564();

	public class_3535(Function<Dynamic<?>, ? extends class_3527> function) {
		super(function);
	}

	@Override
	public void method_15208(
		Random random, class_2791 arg, class_1959 arg2, int i, int j, int k, double d, class_2680 arg3, class_2680 arg4, int l, long m, class_3527 arg5
	) {
		int n = i & 15;
		int o = j & 15;
		class_2680 lv = field_15741;
		class_2680 lv2 = arg2.method_8722().method_15336();
		int p = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		boolean bl = Math.cos(d / 3.0 * Math.PI) > 0.0;
		int q = -1;
		boolean bl2 = false;
		int r = 0;
		class_2338.class_2339 lv3 = new class_2338.class_2339();

		for (int s = k; s >= 0; s--) {
			if (r < 15) {
				lv3.method_10103(n, s, o);
				class_2680 lv4 = arg.method_8320(lv3);
				if (lv4.method_11588()) {
					q = -1;
				} else if (lv4.method_11614() == arg3.method_11614()) {
					if (q == -1) {
						bl2 = false;
						if (p <= 0) {
							lv = class_2246.field_10124.method_9564();
							lv2 = arg3;
						} else if (s >= l - 4 && s <= l + 1) {
							lv = field_15741;
							lv2 = arg2.method_8722().method_15336();
						}

						if (s < l && (lv == null || lv.method_11588())) {
							lv = arg4;
						}

						q = p + Math.max(0, s - l);
						if (s < l - 1) {
							arg.method_12010(lv3, lv2, false);
							if (lv2 == field_15741) {
								arg.method_12010(lv3, field_15740, false);
							}
						} else if (s > 86 + p * 2) {
							if (bl) {
								arg.method_12010(lv3, class_2246.field_10253.method_9564(), false);
							} else {
								arg.method_12010(lv3, class_2246.field_10219.method_9564(), false);
							}
						} else if (s <= l + 3 + p) {
							arg.method_12010(lv3, arg2.method_8722().method_15337(), false);
							bl2 = true;
						} else {
							class_2680 lv5;
							if (s < 64 || s > 127) {
								lv5 = field_15740;
							} else if (bl) {
								lv5 = field_15742;
							} else {
								lv5 = this.method_15207(i, s, j);
							}

							arg.method_12010(lv3, lv5, false);
						}
					} else if (q > 0) {
						q--;
						if (bl2) {
							arg.method_12010(lv3, field_15740, false);
						} else {
							arg.method_12010(lv3, this.method_15207(i, s, j), false);
						}
					}

					r++;
				}
			}
		}
	}
}
