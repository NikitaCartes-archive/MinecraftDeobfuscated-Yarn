package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3130 extends class_3031<class_3111> {
	protected final class_2680 field_17003;

	public class_3130(Function<Dynamic<?>, ? extends class_3111> function, class_2680 arg) {
		super(function);
		this.field_17003 = arg;
	}

	public boolean method_13651(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		int i = 0;

		for (int j = 0; j < 64; j++) {
			class_2338 lv = arg3.method_10069(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (arg.method_8623(lv) && arg.method_8320(lv.method_10074()).method_11614() == class_2246.field_10219) {
				arg.method_8652(lv, this.field_17003, 2);
				i++;
			}
		}

		return i > 0;
	}
}
