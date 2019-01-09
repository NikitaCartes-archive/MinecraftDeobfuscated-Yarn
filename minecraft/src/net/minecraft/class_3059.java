package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3059 extends class_3031<class_3111> {
	public class_3059(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public boolean method_13362(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
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

				for (int k = 0; k <= 1 + i; k++) {
					int l = k <= 3 ? 0 : 3;

					for (int m = -l; m <= l; m++) {
						for (int n = -l; n <= l; n++) {
							class_2680 lv3 = arg.method_8320(lv2.method_10101(arg3).method_10100(m, k, n));
							if (!lv3.method_11588() && !lv3.method_11602(class_3481.field_15503)) {
								return false;
							}
						}
					}
				}

				class_2680 lv4 = class_2246.field_10580
					.method_9564()
					.method_11657(class_2381.field_11166, Boolean.valueOf(true))
					.method_11657(class_2381.field_11169, Boolean.valueOf(false));
				int l = 3;

				for (int m = -3; m <= 3; m++) {
					for (int nx = -3; nx <= 3; nx++) {
						boolean bl = m == -3;
						boolean bl2 = m == 3;
						boolean bl3 = nx == -3;
						boolean bl4 = nx == 3;
						boolean bl5 = bl || bl2;
						boolean bl6 = bl3 || bl4;
						if (!bl5 || !bl6) {
							lv2.method_10101(arg3).method_10100(m, i, nx);
							if (!arg.method_8320(lv2).method_11598(arg, lv2)) {
								boolean bl7 = bl || bl6 && m == -2;
								boolean bl8 = bl2 || bl6 && m == 2;
								boolean bl9 = bl3 || bl5 && nx == -2;
								boolean bl10 = bl4 || bl5 && nx == 2;
								this.method_13153(
									arg,
									lv2,
									lv4.method_11657(class_2381.field_11167, Boolean.valueOf(bl7))
										.method_11657(class_2381.field_11172, Boolean.valueOf(bl8))
										.method_11657(class_2381.field_11171, Boolean.valueOf(bl9))
										.method_11657(class_2381.field_11170, Boolean.valueOf(bl10))
								);
							}
						}
					}
				}

				class_2680 lv5 = class_2246.field_10556
					.method_9564()
					.method_11657(class_2381.field_11166, Boolean.valueOf(false))
					.method_11657(class_2381.field_11169, Boolean.valueOf(false));

				for (int nxx = 0; nxx < i; nxx++) {
					lv2.method_10101(arg3).method_10104(class_2350.field_11036, nxx);
					if (!arg.method_8320(lv2).method_11598(arg, lv2)) {
						this.method_13153(arg, lv2, lv5);
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}
}
