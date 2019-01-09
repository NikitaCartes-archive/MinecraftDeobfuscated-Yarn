package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class class_2972 extends class_2978 {
	public class_2972(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	@Override
	protected boolean method_12863(class_1936 arg, Random random, class_2338 arg2, class_2680 arg3) {
		if (!this.method_12864(arg, random, arg2, arg3)) {
			return false;
		} else {
			class_2350 lv = class_2350.class_2353.field_11062.method_10183(random);
			int i = random.nextInt(2) + 2;
			List<class_2350> list = Lists.<class_2350>newArrayList(lv, lv.method_10170(), lv.method_10160());
			Collections.shuffle(list, random);

			for (class_2350 lv2 : list.subList(0, i)) {
				class_2338.class_2339 lv3 = new class_2338.class_2339(arg2);
				int j = random.nextInt(2) + 1;
				lv3.method_10098(lv2);
				int k;
				class_2350 lv4;
				if (lv2 == lv) {
					lv4 = lv;
					k = random.nextInt(3) + 2;
				} else {
					lv3.method_10098(class_2350.field_11036);
					class_2350[] lvs = new class_2350[]{lv2, class_2350.field_11036};
					lv4 = lvs[random.nextInt(lvs.length)];
					k = random.nextInt(3) + 3;
				}

				for (int l = 0; l < j && this.method_12864(arg, random, lv3, arg3); l++) {
					lv3.method_10098(lv4);
				}

				lv3.method_10098(lv4.method_10153());
				lv3.method_10098(class_2350.field_11036);

				for (int l = 0; l < k; l++) {
					lv3.method_10098(lv);
					if (!this.method_12864(arg, random, lv3, arg3)) {
						break;
					}

					if (random.nextFloat() < 0.25F) {
						lv3.method_10098(class_2350.field_11036);
					}
				}
			}

			return true;
		}
	}
}
