package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3220 extends class_3031<class_3111> {
	public class_3220(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public boolean method_14202(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		class_2338 lv = arg3;

		while (lv.method_10264() > 0) {
			class_2338 lv2 = lv.method_10074();
			if (!arg.method_8623(lv2)) {
				break;
			}

			lv = lv2;
		}

		for (int i = 0; i < 10; i++) {
			class_2338 lv3 = arg3.method_10069(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			class_2680 lv4 = class_2246.field_10588.method_9564();
			if (arg.method_8623(lv3) && lv4.method_11591(arg, lv3)) {
				arg.method_8652(lv3, lv4, 2);
			}
		}

		return true;
	}
}
