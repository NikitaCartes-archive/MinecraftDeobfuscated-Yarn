package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3066 extends class_3031<class_4302> {
	public class_3066(Function<Dynamic<?>, ? extends class_4302> function) {
		super(function);
	}

	public boolean method_13398(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_4302 arg4) {
		int i = random.nextInt(3) + 4;
		if (random.nextInt(12) == 0) {
			i *= 2;
		}

		int j = arg3.method_10264();
		if (j >= 1 && j + i + 1 < 256) {
			class_2248 lv = arg.method_8320(arg3.method_10074()).method_11614();
			if (!class_2248.method_9519(lv) && lv != class_2246.field_10219 && lv != class_2246.field_10402) {
				return false;
			} else {
				class_2338.class_2339 lv2 = new class_2338.class_2339();

				for (int k = 0; k <= i; k++) {
					int l = 0;
					if (k < i && k >= i - 3) {
						l = 2;
					} else if (k == i) {
						l = 1;
					}

					for (int m = -l; m <= l; m++) {
						for (int n = -l; n <= l; n++) {
							class_2680 lv3 = arg.method_8320(lv2.method_10101(arg3).method_10100(m, k, n));
							if (!lv3.method_11588() && !lv3.method_11602(class_3481.field_15503)) {
								return false;
							}
						}
					}
				}

				class_2680 lv4 = class_2246.field_10240.method_9564().method_11657(class_2381.field_11169, Boolean.valueOf(false));

				for (int l = i - 3; l <= i; l++) {
					int m = l < i ? 2 : 1;
					int nx = 0;

					for (int o = -m; o <= m; o++) {
						for (int p = -m; p <= m; p++) {
							boolean bl = o == -m;
							boolean bl2 = o == m;
							boolean bl3 = p == -m;
							boolean bl4 = p == m;
							boolean bl5 = bl || bl2;
							boolean bl6 = bl3 || bl4;
							if (l >= i || bl5 != bl6) {
								lv2.method_10101(arg3).method_10100(o, l, p);
								if (!arg.method_8320(lv2).method_11598(arg, lv2)) {
									this.method_13153(
										arg,
										lv2,
										lv4.method_11657(class_2381.field_11166, Boolean.valueOf(l >= i - 1))
											.method_11657(class_2381.field_11167, Boolean.valueOf(o < 0))
											.method_11657(class_2381.field_11172, Boolean.valueOf(o > 0))
											.method_11657(class_2381.field_11171, Boolean.valueOf(p < 0))
											.method_11657(class_2381.field_11170, Boolean.valueOf(p > 0))
									);
								}
							}
						}
					}
				}

				class_2680 lv5 = class_2246.field_10556
					.method_9564()
					.method_11657(class_2381.field_11166, Boolean.valueOf(false))
					.method_11657(class_2381.field_11169, Boolean.valueOf(false));

				for (int m = 0; m < i; m++) {
					lv2.method_10101(arg3).method_10104(class_2350.field_11036, m);
					if (!arg.method_8320(lv2).method_11598(arg, lv2)) {
						if (arg4.field_19317) {
							arg.method_8652(lv2, lv5, 3);
						} else {
							this.method_13153(arg, lv2, lv5);
						}
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}
}
