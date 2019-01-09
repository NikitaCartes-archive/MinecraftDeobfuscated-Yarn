package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3206 extends class_3031<class_3111> {
	public class_3206(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public class_2680 method_14060(Random random) {
		return random.nextInt(5) > 0 ? class_2246.field_10112.method_9564() : class_2246.field_10479.method_9564();
	}

	public boolean method_14061(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		class_2680 lv = this.method_14060(random);

		for (class_2680 lv2 = arg.method_8320(arg3);
			(lv2.method_11588() || lv2.method_11602(class_3481.field_15503)) && arg3.method_10264() > 0;
			lv2 = arg.method_8320(arg3)
		) {
			arg3 = arg3.method_10074();
		}

		int i = 0;

		for (int j = 0; j < 128; j++) {
			class_2338 lv3 = arg3.method_10069(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (arg.method_8623(lv3) && lv.method_11591(arg, lv3)) {
				arg.method_8652(lv3, lv, 2);
				i++;
			}
		}

		return i > 0;
	}
}
