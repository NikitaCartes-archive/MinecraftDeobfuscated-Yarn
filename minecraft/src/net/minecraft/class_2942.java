package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_2942 extends class_3031<class_3133> {
	private static final class_2680 field_13308 = class_2246.field_10211
		.method_9564()
		.method_11657(class_2211.field_9914, Integer.valueOf(1))
		.method_11657(class_2211.field_9917, class_2737.field_12469)
		.method_11657(class_2211.field_9916, Integer.valueOf(0));
	private static final class_2680 field_13311 = field_13308.method_11657(class_2211.field_9917, class_2737.field_12468)
		.method_11657(class_2211.field_9916, Integer.valueOf(1));
	private static final class_2680 field_13310 = field_13308.method_11657(class_2211.field_9917, class_2737.field_12468);
	private static final class_2680 field_13309 = field_13308.method_11657(class_2211.field_9917, class_2737.field_12466);

	public class_2942(Function<Dynamic<?>, ? extends class_3133> function) {
		super(function);
	}

	public boolean method_12718(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3133 arg4) {
		int i = 0;
		class_2338.class_2339 lv = new class_2338.class_2339(arg3);
		class_2338.class_2339 lv2 = new class_2338.class_2339(arg3);
		if (arg.method_8623(lv)) {
			if (class_2246.field_10211.method_9564().method_11591(arg, lv)) {
				int j = random.nextInt(12) + 5;
				if (random.nextFloat() < arg4.field_13738) {
					int k = random.nextInt(4) + 1;

					for (int l = arg3.method_10263() - k; l <= arg3.method_10263() + k; l++) {
						for (int m = arg3.method_10260() - k; m <= arg3.method_10260() + k; m++) {
							int n = l - arg3.method_10263();
							int o = m - arg3.method_10260();
							if (n * n + o * o <= k * k) {
								lv2.method_10103(l, arg3.method_10264() - 1, m);
								if (arg.method_8320(lv2).method_11614().method_9525(class_3481.field_15464)) {
									arg.method_8652(lv2, class_2246.field_10520.method_9564(), 2);
								}
							}
						}
					}
				}

				for (int k = 0; k < j && arg.method_8623(lv); k++) {
					arg.method_8652(lv, field_13308, 2);
					lv.method_10104(class_2350.field_11036, 1);
				}

				if (lv.method_10264() - arg3.method_10264() >= 3) {
					arg.method_8652(lv, field_13311, 2);
					arg.method_8652(lv.method_10104(class_2350.field_11033, 1), field_13310, 2);
					arg.method_8652(lv.method_10104(class_2350.field_11033, 1), field_13309, 2);
				}
			}

			i++;
		}

		return i > 0;
	}
}
