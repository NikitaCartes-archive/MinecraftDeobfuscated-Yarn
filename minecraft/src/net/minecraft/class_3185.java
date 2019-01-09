package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3185 extends class_3031<class_3187> {
	public class_3185(Function<Dynamic<?>, ? extends class_3187> function) {
		super(function);
	}

	public boolean method_13979(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3187 arg4) {
		if (!class_2248.method_9608(arg.method_8320(arg3.method_10084()).method_11614())) {
			return false;
		} else if (!class_2248.method_9608(arg.method_8320(arg3.method_10074()).method_11614())) {
			return false;
		} else {
			class_2680 lv = arg.method_8320(arg3);
			if (!lv.method_11588() && !class_2248.method_9608(lv.method_11614())) {
				return false;
			} else {
				int i = 0;
				int j = 0;
				if (class_2248.method_9608(arg.method_8320(arg3.method_10067()).method_11614())) {
					j++;
				}

				if (class_2248.method_9608(arg.method_8320(arg3.method_10078()).method_11614())) {
					j++;
				}

				if (class_2248.method_9608(arg.method_8320(arg3.method_10095()).method_11614())) {
					j++;
				}

				if (class_2248.method_9608(arg.method_8320(arg3.method_10072()).method_11614())) {
					j++;
				}

				int k = 0;
				if (arg.method_8623(arg3.method_10067())) {
					k++;
				}

				if (arg.method_8623(arg3.method_10078())) {
					k++;
				}

				if (arg.method_8623(arg3.method_10095())) {
					k++;
				}

				if (arg.method_8623(arg3.method_10072())) {
					k++;
				}

				if (j == 3 && k == 1) {
					arg.method_8652(arg3, arg4.field_13850.method_15759(), 2);
					arg.method_8405().method_8676(arg3, arg4.field_13850.method_15772(), 0);
					i++;
				}

				return i > 0;
			}
		}
	}
}
