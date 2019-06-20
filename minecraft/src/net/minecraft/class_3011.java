package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3011 extends class_3031<class_3013> {
	public class_3011(Function<Dynamic<?>, ? extends class_3013> function) {
		super(function);
	}

	public boolean method_13005(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3013 arg4) {
		if (!arg.method_8316(arg3).method_15767(class_3486.field_15517)) {
			return false;
		} else {
			int i = 0;
			int j = random.nextInt(arg4.field_13472 - 2) + 2;

			for (int k = arg3.method_10263() - j; k <= arg3.method_10263() + j; k++) {
				for (int l = arg3.method_10260() - j; l <= arg3.method_10260() + j; l++) {
					int m = k - arg3.method_10263();
					int n = l - arg3.method_10260();
					if (m * m + n * n <= j * j) {
						for (int o = arg3.method_10264() - arg4.field_13471; o <= arg3.method_10264() + arg4.field_13471; o++) {
							class_2338 lv = new class_2338(k, o, l);
							class_2680 lv2 = arg.method_8320(lv);

							for (class_2680 lv3 : arg4.field_13469) {
								if (lv3.method_11614() == lv2.method_11614()) {
									arg.method_8652(lv, arg4.field_13470, 2);
									i++;
									break;
								}
							}
						}
					}
				}
			}

			return i > 0;
		}
	}
}
