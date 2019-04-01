package net.minecraft;

import java.util.Random;

public abstract class class_2500 extends class_2493 {
	protected class_2500(class_2248.class_2251 arg) {
		super(arg);
	}

	private static boolean method_10614(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2338 lv = arg3.method_10084();
		class_2680 lv2 = arg2.method_8320(lv);
		int i = class_3558.method_20049(arg2, arg, arg3, lv2, lv, class_2350.field_11036, lv2.method_11581(arg2, lv));
		return i < arg2.method_8315();
	}

	private static boolean method_10613(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2338 lv = arg3.method_10084();
		return method_10614(arg, arg2, arg3) && !arg2.method_8316(lv).method_15767(class_3486.field_15517);
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (!arg2.field_9236) {
			if (!method_10614(arg, arg2, arg3)) {
				arg2.method_8501(arg3, class_2246.field_10566.method_9564());
			} else if (arg2.method_8602(arg3.method_10084()) >= 4) {
				if (arg2.method_8602(arg3.method_10084()) >= 9) {
					class_2680 lv = this.method_9564();

					for (int i = 0; i < 4; i++) {
						class_2338 lv2 = arg3.method_10069(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
						if (arg2.method_8320(lv2).method_11614() == class_2246.field_10566 && method_10613(lv, arg2, lv2)) {
							arg2.method_8501(lv2, lv);
						}
					}
				}
			}
		}
	}
}
