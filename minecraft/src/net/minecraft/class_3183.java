package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3183 extends class_3031<class_3111> {
	public class_3183(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public boolean method_13978(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		class_2338.class_2339 lv = new class_2338.class_2339();
		class_2338.class_2339 lv2 = new class_2338.class_2339();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int k = arg3.method_10263() + i;
				int l = arg3.method_10260() + j;
				int m = arg.method_8589(class_2902.class_2903.field_13197, k, l);
				lv.method_10103(k, m, l);
				lv2.method_10101(lv).method_10104(class_2350.field_11033, 1);
				class_1959 lv3 = arg.method_8310(lv);
				if (lv3.method_8685(arg, lv2, false)) {
					arg.method_8652(lv2, class_2246.field_10295.method_9564(), 2);
				}

				if (lv3.method_8696(arg, lv)) {
					arg.method_8652(lv, class_2246.field_10477.method_9564(), 2);
					class_2680 lv4 = arg.method_8320(lv2);
					if (lv4.method_11570(class_2493.field_11522)) {
						arg.method_8652(lv2, lv4.method_11657(class_2493.field_11522, Boolean.valueOf(true)), 2);
					}
				}
			}
		}

		return true;
	}
}
