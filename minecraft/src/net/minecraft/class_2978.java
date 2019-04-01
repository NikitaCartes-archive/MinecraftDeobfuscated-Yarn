package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public abstract class class_2978 extends class_3031<class_3111> {
	public class_2978(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public boolean method_12865(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		class_2680 lv = class_3481.field_15461.method_15142(random).method_9564();
		return this.method_12863(arg, random, arg3, lv);
	}

	protected abstract boolean method_12863(class_1936 arg, Random random, class_2338 arg2, class_2680 arg3);

	protected boolean method_12864(class_1936 arg, Random random, class_2338 arg2, class_2680 arg3) {
		class_2338 lv = arg2.method_10084();
		class_2680 lv2 = arg.method_8320(arg2);
		if ((lv2.method_11614() == class_2246.field_10382 || lv2.method_11602(class_3481.field_15488))
			&& arg.method_8320(lv).method_11614() == class_2246.field_10382) {
			arg.method_8652(arg2, arg3, 3);
			if (random.nextFloat() < 0.25F) {
				arg.method_8652(lv, class_3481.field_15488.method_15142(random).method_9564(), 2);
			} else if (random.nextFloat() < 0.05F) {
				arg.method_8652(lv, class_2246.field_10476.method_9564().method_11657(class_2472.field_11472, Integer.valueOf(random.nextInt(4) + 1)), 2);
			}

			for (class_2350 lv3 : class_2350.class_2353.field_11062) {
				if (random.nextFloat() < 0.2F) {
					class_2338 lv4 = arg2.method_10093(lv3);
					if (arg.method_8320(lv4).method_11614() == class_2246.field_10382) {
						class_2680 lv5 = class_3481.field_15476.method_15142(random).method_9564().method_11657(class_2222.field_9933, lv3);
						arg.method_8652(lv4, lv5, 2);
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}
}
