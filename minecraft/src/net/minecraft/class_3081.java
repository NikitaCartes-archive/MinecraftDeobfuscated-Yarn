package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3081 extends class_3031<class_3111> {
	public class_3081(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public boolean method_13460(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		int i = 0;
		int j = arg.method_8589(class_2902.class_2903.field_13200, arg3.method_10263(), arg3.method_10260());
		class_2338 lv = new class_2338(arg3.method_10263(), j, arg3.method_10260());
		if (arg.method_8320(lv).method_11614() == class_2246.field_10382) {
			class_2680 lv2 = class_2246.field_9993.method_9564();
			class_2680 lv3 = class_2246.field_10463.method_9564();
			int k = 1 + random.nextInt(10);

			for (int l = 0; l <= k; l++) {
				if (arg.method_8320(lv).method_11614() == class_2246.field_10382
					&& arg.method_8320(lv.method_10084()).method_11614() == class_2246.field_10382
					&& lv3.method_11591(arg, lv)) {
					if (l == k) {
						arg.method_8652(lv, lv2.method_11657(class_2393.field_11194, Integer.valueOf(random.nextInt(23))), 2);
						i++;
					} else {
						arg.method_8652(lv, lv3, 2);
					}
				} else if (l > 0) {
					class_2338 lv4 = lv.method_10074();
					if (lv2.method_11591(arg, lv4) && arg.method_8320(lv4.method_10074()).method_11614() != class_2246.field_9993) {
						arg.method_8652(lv4, lv2.method_11657(class_2393.field_11194, Integer.valueOf(random.nextInt(23))), 2);
						i++;
					}
					break;
				}

				lv = lv.method_10084();
			}
		}

		return i > 0;
	}
}
