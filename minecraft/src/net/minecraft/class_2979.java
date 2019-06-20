package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class class_2979 extends class_2978 {
	public class_2979(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	@Override
	protected boolean method_12863(class_1936 arg, Random random, class_2338 arg2, class_2680 arg3) {
		class_2338.class_2339 lv = new class_2338.class_2339(arg2);
		int i = random.nextInt(3) + 1;

		for (int j = 0; j < i; j++) {
			if (!this.method_12864(arg, random, lv, arg3)) {
				return true;
			}

			lv.method_10098(class_2350.field_11036);
		}

		class_2338 lv2 = lv.method_10062();
		int k = random.nextInt(3) + 2;
		List<class_2350> list = Lists.<class_2350>newArrayList(class_2350.class_2353.field_11062);
		Collections.shuffle(list, random);

		for (class_2350 lv3 : list.subList(0, k)) {
			lv.method_10101(lv2);
			lv.method_10098(lv3);
			int l = random.nextInt(5) + 2;
			int m = 0;

			for (int n = 0; n < l && this.method_12864(arg, random, lv, arg3); n++) {
				m++;
				lv.method_10098(class_2350.field_11036);
				if (n == 0 || m >= 2 && random.nextFloat() < 0.25F) {
					lv.method_10098(lv3);
					m = 0;
				}
			}
		}

		return true;
	}
}
