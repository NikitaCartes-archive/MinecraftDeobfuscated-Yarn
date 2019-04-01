package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3209 extends class_3031<class_3203> {
	public class_3209(Function<Dynamic<?>, ? extends class_3203> function) {
		super(function);
	}

	public boolean method_14080(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3203 arg4) {
		for (class_2680 lv = arg.method_8320(arg3);
			(lv.method_11588() || lv.method_11602(class_3481.field_15503)) && arg3.method_10264() > 0;
			lv = arg.method_8320(arg3)
		) {
			arg3 = arg3.method_10074();
		}

		int i = 0;

		for (int j = 0; j < 128; j++) {
			class_2338 lv2 = arg3.method_10069(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (arg.method_8623(lv2) && arg4.field_13892.method_11591(arg, lv2)) {
				arg.method_8652(lv2, arg4.field_13892, 2);
				i++;
			}
		}

		return i > 0;
	}
}
