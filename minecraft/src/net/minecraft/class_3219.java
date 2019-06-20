package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3219 extends class_3031<class_3111> {
	private static final class_2350[] field_17396 = class_2350.values();

	public class_3219(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public boolean method_14201(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		class_2338.class_2339 lv = new class_2338.class_2339(arg3);

		for (int i = arg3.method_10264(); i < 256; i++) {
			lv.method_10101(arg3);
			lv.method_10100(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
			lv.method_10099(i);
			if (arg.method_8623(lv)) {
				for (class_2350 lv2 : field_17396) {
					if (lv2 != class_2350.field_11033 && class_2541.method_10821(arg, lv, lv2)) {
						arg.method_8652(lv, class_2246.field_10597.method_9564().method_11657(class_2541.method_10828(lv2), Boolean.valueOf(true)), 2);
						break;
					}
				}
			}
		}

		return true;
	}
}
