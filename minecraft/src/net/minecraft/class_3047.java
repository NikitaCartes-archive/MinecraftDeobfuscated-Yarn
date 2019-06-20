package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3047 extends class_3031<class_3111> {
	public class_3047(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public boolean method_13239(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		if (!arg.method_8623(arg3)) {
			return false;
		} else if (arg.method_8320(arg3.method_10084()).method_11614() != class_2246.field_10515) {
			return false;
		} else {
			arg.method_8652(arg3, class_2246.field_10171.method_9564(), 2);

			for (int i = 0; i < 1500; i++) {
				class_2338 lv = arg3.method_10069(random.nextInt(8) - random.nextInt(8), -random.nextInt(12), random.nextInt(8) - random.nextInt(8));
				if (arg.method_8320(lv).method_11588()) {
					int j = 0;

					for (class_2350 lv2 : class_2350.values()) {
						if (arg.method_8320(lv.method_10093(lv2)).method_11614() == class_2246.field_10171) {
							j++;
						}

						if (j > 1) {
							break;
						}
					}

					if (j == 1) {
						arg.method_8652(lv, class_2246.field_10171.method_9564(), 2);
					}
				}
			}

			return true;
		}
	}
}
