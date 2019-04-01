package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_2953 extends class_3031<class_3111> {
	public class_2953(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public boolean method_12817(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		for (class_2680 lv = arg.method_8320(arg3);
			(lv.method_11588() || lv.method_11602(class_3481.field_15503)) && arg3.method_10264() > 1;
			lv = arg.method_8320(arg3)
		) {
			arg3 = arg3.method_10074();
		}

		if (arg3.method_10264() < 1) {
			return false;
		} else {
			arg3 = arg3.method_10084();

			for (int i = 0; i < 4; i++) {
				class_2338 lv2 = arg3.method_10069(random.nextInt(4) - random.nextInt(4), random.nextInt(3) - random.nextInt(3), random.nextInt(4) - random.nextInt(4));
				if (arg.method_8623(lv2)) {
					arg.method_8652(lv2, class_2246.field_10034.method_9564(), 2);
					class_2621.method_11287(arg, random, lv2, class_39.field_850);
					class_2680 lv3 = class_2246.field_10336.method_9564();

					for (class_2350 lv4 : class_2350.class_2353.field_11062) {
						class_2338 lv5 = lv2.method_10093(lv4);
						if (lv3.method_11591(arg, lv5)) {
							arg.method_8652(lv5, lv3, 2);
						}
					}

					return true;
				}
			}

			return false;
		}
	}
}
