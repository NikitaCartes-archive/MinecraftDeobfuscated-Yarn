package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3217 extends class_3031<class_3111> {
	public class_3217(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public boolean method_14165(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		class_2338 lv = arg.method_8395();
		int i = 16;
		double d = lv.method_10262(arg3.method_10069(8, lv.method_10264(), 8));
		if (d > 1024.0) {
			return true;
		} else {
			class_2338 lv2 = new class_2338(lv.method_10263() - 16, Math.max(lv.method_10264(), 4) - 1, lv.method_10260() - 16);
			class_2338 lv3 = new class_2338(lv.method_10263() + 16, Math.max(lv.method_10264(), 4) - 1, lv.method_10260() + 16);
			class_2338.class_2339 lv4 = new class_2338.class_2339(lv2);

			for (int j = arg3.method_10260(); j < arg3.method_10260() + 16; j++) {
				for (int k = arg3.method_10263(); k < arg3.method_10263() + 16; k++) {
					if (j >= lv2.method_10260() && j <= lv3.method_10260() && k >= lv2.method_10263() && k <= lv3.method_10263()) {
						lv4.method_10103(k, lv4.method_10264(), j);
						if (lv.method_10263() == k && lv.method_10260() == j) {
							arg.method_8652(lv4, class_2246.field_10445.method_9564(), 2);
						} else {
							arg.method_8652(lv4, class_2246.field_10340.method_9564(), 2);
						}
					}
				}
			}

			return true;
		}
	}
}
