package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public abstract class class_3038 extends class_3031<class_3111> {
	public class_3038(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function, false);
	}

	public boolean method_13176(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		class_2680 lv = this.method_13175(random, arg3);
		int i = 0;

		for (int j = 0; j < 64; j++) {
			class_2338 lv2 = arg3.method_10069(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (arg.method_8623(lv2) && lv2.method_10264() < 255 && lv.method_11591(arg, lv2)) {
				arg.method_8652(lv2, lv, 2);
				i++;
			}
		}

		return i > 0;
	}

	public abstract class_2680 method_13175(Random random, class_2338 arg);
}
