package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_2962 extends class_3031<class_2963> {
	public class_2962(Function<Dynamic<?>, ? extends class_2963> function) {
		super(function);
	}

	public boolean method_12841(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_2963 arg4) {
		int i = 0;
		class_2680 lv = arg4.field_13356;

		for (int j = 0; j < 64; j++) {
			class_2338 lv2 = arg3.method_10069(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (arg.method_8623(lv2) && (!arg.method_8597().method_12467() || lv2.method_10264() < 255) && lv.method_11591(arg, lv2)) {
				arg.method_8652(lv2, lv, 2);
				i++;
			}
		}

		return i > 0;
	}
}
