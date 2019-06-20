package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3063 extends class_3031<class_3034> {
	private final class_2248 field_13663 = class_2246.field_10225;

	public class_3063(Function<Dynamic<?>, ? extends class_3034> function) {
		super(function);
	}

	public boolean method_13385(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3034 arg4) {
		while (arg.method_8623(arg3) && arg3.method_10264() > 2) {
			arg3 = arg3.method_10074();
		}

		if (arg.method_8320(arg3).method_11614() != class_2246.field_10491) {
			return false;
		} else {
			int i = random.nextInt(arg4.field_13601) + 2;
			int j = 1;

			for (int k = arg3.method_10263() - i; k <= arg3.method_10263() + i; k++) {
				for (int l = arg3.method_10260() - i; l <= arg3.method_10260() + i; l++) {
					int m = k - arg3.method_10263();
					int n = l - arg3.method_10260();
					if (m * m + n * n <= i * i) {
						for (int o = arg3.method_10264() - 1; o <= arg3.method_10264() + 1; o++) {
							class_2338 lv = new class_2338(k, o, l);
							class_2248 lv2 = arg.method_8320(lv).method_11614();
							if (class_2248.method_9519(lv2) || lv2 == class_2246.field_10491 || lv2 == class_2246.field_10295) {
								arg.method_8652(lv, this.field_13663.method_9564(), 2);
							}
						}
					}
				}
			}

			return true;
		}
	}
}
