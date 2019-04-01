package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3168 extends class_3031<class_3163> {
	public class_3168(Function<Dynamic<?>, ? extends class_3163> function) {
		super(function);
	}

	public boolean method_13926(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3163 arg4) {
		int i = 0;

		for (int j = 0; j < arg4.field_13789; j++) {
			int k = random.nextInt(8) - random.nextInt(8);
			int l = random.nextInt(8) - random.nextInt(8);
			int m = arg.method_8589(class_2902.class_2903.field_13200, arg3.method_10263() + k, arg3.method_10260() + l);
			class_2338 lv = new class_2338(arg3.method_10263() + k, m, arg3.method_10260() + l);
			if (arg.method_8320(lv).method_11614() == class_2246.field_10382) {
				boolean bl = random.nextDouble() < arg4.field_13788;
				class_2680 lv2 = bl ? class_2246.field_10238.method_9564() : class_2246.field_10376.method_9564();
				if (lv2.method_11591(arg, lv)) {
					if (bl) {
						class_2680 lv3 = lv2.method_11657(class_2525.field_11616, class_2756.field_12609);
						class_2338 lv4 = lv.method_10084();
						if (arg.method_8320(lv4).method_11614() == class_2246.field_10382) {
							arg.method_8652(lv, lv2, 2);
							arg.method_8652(lv4, lv3, 2);
						}
					} else {
						arg.method_8652(lv, lv2, 2);
					}

					i++;
				}
			}
		}

		return i > 0;
	}
}
