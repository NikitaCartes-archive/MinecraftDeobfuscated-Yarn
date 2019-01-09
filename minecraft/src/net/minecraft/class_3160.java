package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3160 extends class_3031<class_2984> {
	public class_3160(Function<Dynamic<?>, ? extends class_2984> function) {
		super(function);
	}

	public boolean method_13876(class_1936 arg, class_2794<?> arg2, Random random, class_2338 arg3, class_2984 arg4) {
		int i = 0;

		for (int j = 0; j < arg4.field_13385; j++) {
			int k = random.nextInt(8) - random.nextInt(8);
			int l = random.nextInt(8) - random.nextInt(8);
			int m = arg.method_8589(class_2902.class_2903.field_13200, arg3.method_10263() + k, arg3.method_10260() + l);
			class_2338 lv = new class_2338(arg3.method_10263() + k, m, arg3.method_10260() + l);
			class_2680 lv2 = class_2246.field_10476.method_9564().method_11657(class_2472.field_11472, Integer.valueOf(random.nextInt(4) + 1));
			if (arg.method_8320(lv).method_11614() == class_2246.field_10382 && lv2.method_11591(arg, lv)) {
				arg.method_8652(lv, lv2, 2);
				i++;
			}
		}

		return i > 0;
	}
}
