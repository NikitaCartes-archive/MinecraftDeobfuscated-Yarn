package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3053 extends class_3031<class_3111> {
	public class_3053(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public boolean method_13325(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		for (int i = 0; i < 64; i++) {
			class_2338 lv = arg3.method_10069(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (arg.method_8623(lv) && arg.method_8320(lv.method_10074()).method_11614() == class_2246.field_10515) {
				arg.method_8652(lv, class_2246.field_10036.method_9564(), 2);
			}
		}

		return true;
	}
}
