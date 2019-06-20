package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3529 extends class_3523<class_3527> {
	public class_3529(Function<Dynamic<?>, ? extends class_3527> function) {
		super(function);
	}

	public void method_15333(
		Random random, class_2791 arg, class_1959 arg2, int i, int j, int k, double d, class_2680 arg3, class_2680 arg4, int l, long m, class_3527 arg5
	) {
		double e = class_1959.field_9324.method_15437((double)i * 0.25, (double)j * 0.25);
		if (e > 0.0) {
			int n = i & 15;
			int o = j & 15;
			class_2338.class_2339 lv = new class_2338.class_2339();

			for (int p = k; p >= 0; p--) {
				lv.method_10103(n, p, o);
				if (!arg.method_8320(lv).method_11588()) {
					if (p == 62 && arg.method_8320(lv).method_11614() != arg4.method_11614()) {
						arg.method_12010(lv, arg4, false);
					}
					break;
				}
			}
		}

		class_3523.field_15701.method_15305(random, arg, arg2, i, j, k, d, arg3, arg4, l, m, arg5);
	}
}
