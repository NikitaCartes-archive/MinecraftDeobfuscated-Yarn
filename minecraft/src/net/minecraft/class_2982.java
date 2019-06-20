package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_2982 extends class_3031<class_3111> {
	private static final class_2311 field_13382 = (class_2311)class_2246.field_10428;

	public class_2982(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public boolean method_12869(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		for (class_2680 lv = arg.method_8320(arg3);
			(lv.method_11588() || lv.method_11602(class_3481.field_15503)) && arg3.method_10264() > 0;
			lv = arg.method_8320(arg3)
		) {
			arg3 = arg3.method_10074();
		}

		class_2680 lv2 = field_13382.method_9564();

		for (int i = 0; i < 4; i++) {
			class_2338 lv3 = arg3.method_10069(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (arg.method_8623(lv3) && lv2.method_11591(arg, lv3)) {
				arg.method_8652(lv3, lv2, 2);
			}
		}

		return true;
	}
}
