package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_4276 extends class_3031<class_3111> {
	public class_4276(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public boolean method_20235(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		for (int i = 0; i < 1 + random.nextInt(4); i++) {
			class_2338 lv = arg3.method_10069(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));

			for (class_2680 lv2 = arg.method_8320(lv); lv2.method_11588() && lv.method_10264() > 0; lv2 = arg.method_8320(lv)) {
				lv = lv.method_10074();
			}

			lv = lv.method_10084();
			class_2680 lv3 = class_2246.field_16328.method_9564();
			arg.method_8652(lv, lv3, 2);
			class_2621.method_11287(arg, random, lv, class_39.field_19179);
		}

		return true;
	}
}
