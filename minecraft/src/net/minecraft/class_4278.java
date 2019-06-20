package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_4278 extends class_3031<class_4279> {
	public class_4278(Function<Dynamic<?>, ? extends class_4279> function) {
		super(function);
	}

	public boolean method_20312(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_4279 arg4) {
		class_2338.class_2339 lv = new class_2338.class_2339();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int k = arg3.method_10263() + i;
				int l = arg3.method_10260() + j;
				int m = arg4.field_19202;
				lv.method_10103(k, m, l);
				if (arg.method_8320(lv).method_11588()) {
					arg.method_8652(lv, arg4.field_19203, 2);
				}
			}
		}

		return true;
	}
}
