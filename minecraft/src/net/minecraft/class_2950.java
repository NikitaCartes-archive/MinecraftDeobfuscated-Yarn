package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_2950 extends class_3031<class_2951> {
	public class_2950(Function<Dynamic<?>, ? extends class_2951> function) {
		super(function);
	}

	public boolean method_12813(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_2951 arg4) {
		while (arg3.method_10264() > 3) {
			if (!arg.method_8623(arg3.method_10074())) {
				class_2248 lv = arg.method_8320(arg3.method_10074()).method_11614();
				if (lv == class_2246.field_10219 || class_2248.method_9519(lv) || class_2248.method_9608(lv)) {
					break;
				}
			}

			arg3 = arg3.method_10074();
		}

		if (arg3.method_10264() <= 3) {
			return false;
		} else {
			int i = arg4.field_13346;

			for (int j = 0; i >= 0 && j < 3; j++) {
				int k = i + random.nextInt(2);
				int l = i + random.nextInt(2);
				int m = i + random.nextInt(2);
				float f = (float)(k + l + m) * 0.333F + 0.5F;

				for (class_2338 lv2 : class_2338.method_10097(arg3.method_10069(-k, -l, -m), arg3.method_10069(k, l, m))) {
					if (lv2.method_10262(arg3) <= (double)(f * f)) {
						arg.method_8652(lv2, arg4.field_13345, 4);
					}
				}

				arg3 = arg3.method_10069(-(i + 1) + random.nextInt(2 + i * 2), 0 - random.nextInt(2), -(i + 1) + random.nextInt(2 + i * 2));
			}

			return true;
		}
	}
}
