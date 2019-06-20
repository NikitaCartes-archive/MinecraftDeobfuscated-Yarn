package net.minecraft;

import java.util.List;
import java.util.Random;

public class class_2372 extends class_2500 implements class_2256 {
	public class_2372(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public boolean method_9651(class_1922 arg, class_2338 arg2, class_2680 arg3, boolean bl) {
		return arg.method_8320(arg2.method_10084()).method_11588();
	}

	@Override
	public boolean method_9650(class_1937 arg, Random random, class_2338 arg2, class_2680 arg3) {
		return true;
	}

	@Override
	public void method_9652(class_1937 arg, Random random, class_2338 arg2, class_2680 arg3) {
		class_2338 lv = arg2.method_10084();
		class_2680 lv2 = class_2246.field_10479.method_9564();

		label48:
		for (int i = 0; i < 128; i++) {
			class_2338 lv3 = lv;

			for (int j = 0; j < i / 16; j++) {
				lv3 = lv3.method_10069(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
				if (arg.method_8320(lv3.method_10074()).method_11614() != this || method_9614(arg.method_8320(lv3).method_11628(arg, lv3))) {
					continue label48;
				}
			}

			class_2680 lv4 = arg.method_8320(lv3);
			if (lv4.method_11614() == lv2.method_11614() && random.nextInt(10) == 0) {
				((class_2256)lv2.method_11614()).method_9652(arg, random, lv3, lv4);
			}

			if (lv4.method_11588()) {
				class_2680 lv5;
				if (random.nextInt(8) == 0) {
					List<class_2975<?>> list = arg.method_8310(lv3).method_8718();
					if (list.isEmpty()) {
						continue;
					}

					lv5 = ((class_3038)((class_2986)((class_2975)list.get(0)).field_13375).field_13399.field_13376).method_13175(random, lv3);
				} else {
					lv5 = lv2;
				}

				if (lv5.method_11591(arg, lv3)) {
					arg.method_8652(lv3, lv5, 3);
				}
			}
		}
	}

	@Override
	public boolean method_9601(class_2680 arg) {
		return true;
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9175;
	}
}
