package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_2971 extends class_3031<class_3111> {
	public class_2971(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public boolean method_12853(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		for (int i = 0; i < 10; i++) {
			class_2338 lv = arg3.method_10069(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (arg.method_8623(lv)) {
				int j = 1 + random.nextInt(random.nextInt(3) + 1);

				for (int k = 0; k < j; k++) {
					if (class_2246.field_10029.method_9564().method_11591(arg, lv)) {
						arg.method_8652(lv.method_10086(k), class_2246.field_10029.method_9564(), 2);
					}
				}
			}
		}

		return true;
	}
}
