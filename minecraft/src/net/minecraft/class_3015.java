package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3015 extends class_3031<class_3017> {
	public class_3015(Function<Dynamic<?>, ? extends class_3017> function) {
		super(function);
	}

	public boolean method_13019(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3017 arg4) {
		boolean bl = false;

		for (int i = 0; i < 64; i++) {
			class_2338 lv = arg3.method_10069(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (arg.method_8623(lv) && lv.method_10264() < 254 && arg4.field_13474.method_11591(arg, lv)) {
				((class_2320)arg4.field_13474.method_11614()).method_10021(arg, lv, 2);
				bl = true;
			}
		}

		return bl;
	}
}
