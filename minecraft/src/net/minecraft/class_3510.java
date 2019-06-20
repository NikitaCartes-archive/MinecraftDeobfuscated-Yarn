package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3510 extends class_3523<class_3527> {
	public class_3510(Function<Dynamic<?>, ? extends class_3527> function) {
		super(function);
	}

	public void method_15219(
		Random random, class_2791 arg, class_1959 arg2, int i, int j, int k, double d, class_2680 arg3, class_2680 arg4, int l, long m, class_3527 arg5
	) {
		this.method_15218(random, arg, arg2, i, j, k, d, arg3, arg4, arg5.method_15337(), arg5.method_15336(), arg5.method_15330(), l);
	}

	protected void method_15218(
		Random random,
		class_2791 arg,
		class_1959 arg2,
		int i,
		int j,
		int k,
		double d,
		class_2680 arg3,
		class_2680 arg4,
		class_2680 arg5,
		class_2680 arg6,
		class_2680 arg7,
		int l
	) {
		class_2680 lv = arg5;
		class_2680 lv2 = arg6;
		class_2338.class_2339 lv3 = new class_2338.class_2339();
		int m = -1;
		int n = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		int o = i & 15;
		int p = j & 15;

		for (int q = k; q >= 0; q--) {
			lv3.method_10103(o, q, p);
			class_2680 lv4 = arg.method_8320(lv3);
			if (lv4.method_11588()) {
				m = -1;
			} else if (lv4.method_11614() == arg3.method_11614()) {
				if (m == -1) {
					if (n <= 0) {
						lv = class_2246.field_10124.method_9564();
						lv2 = arg3;
					} else if (q >= l - 4 && q <= l + 1) {
						lv = arg5;
						lv2 = arg6;
					}

					if (q < l && (lv == null || lv.method_11588())) {
						if (arg2.method_8707(lv3.method_10103(i, q, j)) < 0.15F) {
							lv = class_2246.field_10295.method_9564();
						} else {
							lv = arg4;
						}

						lv3.method_10103(o, q, p);
					}

					m = n;
					if (q >= l - 1) {
						arg.method_12010(lv3, lv, false);
					} else if (q < l - 7 - n) {
						lv = class_2246.field_10124.method_9564();
						lv2 = arg3;
						arg.method_12010(lv3, arg7, false);
					} else {
						arg.method_12010(lv3, lv2, false);
					}
				} else if (m > 0) {
					m--;
					arg.method_12010(lv3, lv2, false);
					if (m == 0 && lv2.method_11614() == class_2246.field_10102 && n > 1) {
						m = random.nextInt(4) + Math.max(0, q - 63);
						lv2 = lv2.method_11614() == class_2246.field_10534 ? class_2246.field_10344.method_9564() : class_2246.field_9979.method_9564();
					}
				}
			}
		}
	}
}
