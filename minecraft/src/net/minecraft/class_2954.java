package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_2954 extends class_3031<class_3111> {
	public class_2954(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public boolean method_12818(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		if (arg3.method_10264() > arg.method_8615() - 1) {
			return false;
		} else if (arg.method_8320(arg3).method_11614() != class_2246.field_10382 && arg.method_8320(arg3.method_10074()).method_11614() != class_2246.field_10382) {
			return false;
		} else {
			boolean bl = false;

			for (class_2350 lv : class_2350.values()) {
				if (lv != class_2350.field_11033 && arg.method_8320(arg3.method_10093(lv)).method_11614() == class_2246.field_10225) {
					bl = true;
					break;
				}
			}

			if (!bl) {
				return false;
			} else {
				arg.method_8652(arg3, class_2246.field_10384.method_9564(), 2);

				for (int i = 0; i < 200; i++) {
					int j = random.nextInt(5) - random.nextInt(6);
					int k = 3;
					if (j < 2) {
						k += j / 2;
					}

					if (k >= 1) {
						class_2338 lv2 = arg3.method_10069(random.nextInt(k) - random.nextInt(k), j, random.nextInt(k) - random.nextInt(k));
						class_2680 lv3 = arg.method_8320(lv2);
						class_2248 lv4 = lv3.method_11614();
						if (lv3.method_11620() == class_3614.field_15959 || lv4 == class_2246.field_10382 || lv4 == class_2246.field_10225 || lv4 == class_2246.field_10295) {
							for (class_2350 lv5 : class_2350.values()) {
								class_2248 lv6 = arg.method_8320(lv2.method_10093(lv5)).method_11614();
								if (lv6 == class_2246.field_10384) {
									arg.method_8652(lv2, class_2246.field_10384.method_9564(), 2);
									break;
								}
							}
						}
					}
				}

				return true;
			}
		}
	}
}
