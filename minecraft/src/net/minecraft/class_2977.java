package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_2977 extends class_2978 {
	public class_2977(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	@Override
	protected boolean method_12863(class_1936 arg, Random random, class_2338 arg2, class_2680 arg3) {
		int i = random.nextInt(3) + 3;
		int j = random.nextInt(3) + 3;
		int k = random.nextInt(3) + 3;
		int l = random.nextInt(3) + 1;
		class_2338.class_2339 lv = new class_2338.class_2339(arg2);

		for (int m = 0; m <= j; m++) {
			for (int n = 0; n <= i; n++) {
				for (int o = 0; o <= k; o++) {
					lv.method_10103(m + arg2.method_10263(), n + arg2.method_10264(), o + arg2.method_10260());
					lv.method_10104(class_2350.field_11033, l);
					if ((m != 0 && m != j || n != 0 && n != i)
						&& (o != 0 && o != k || n != 0 && n != i)
						&& (m != 0 && m != j || o != 0 && o != k)
						&& (m == 0 || m == j || n == 0 || n == i || o == 0 || o == k)
						&& !(random.nextFloat() < 0.1F)
						&& !this.method_12864(arg, random, lv, arg3)) {
					}
				}
			}
		}

		return true;
	}
}
