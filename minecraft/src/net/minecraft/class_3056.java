package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class class_3056 extends class_2944<class_3111> {
	private final class_2680 field_13646;
	private final class_2680 field_13647;

	public class_3056(Function<Dynamic<?>, ? extends class_3111> function, class_2680 arg, class_2680 arg2) {
		super(function, false);
		this.field_13647 = arg;
		this.field_13646 = arg2;
	}

	@Override
	public boolean method_12775(Set<class_2338> set, class_3747 arg, Random random, class_2338 arg2, class_3341 arg3) {
		arg2 = arg.method_8598(class_2902.class_2903.field_13203, arg2).method_10074();
		if (method_16430(arg, arg2)) {
			arg2 = arg2.method_10084();
			this.method_12773(set, arg, arg2, this.field_13647, arg3);

			for (int i = arg2.method_10264(); i <= arg2.method_10264() + 2; i++) {
				int j = i - arg2.method_10264();
				int k = 2 - j;

				for (int l = arg2.method_10263() - k; l <= arg2.method_10263() + k; l++) {
					int m = l - arg2.method_10263();

					for (int n = arg2.method_10260() - k; n <= arg2.method_10260() + k; n++) {
						int o = n - arg2.method_10260();
						if (Math.abs(m) != k || Math.abs(o) != k || random.nextInt(2) != 0) {
							class_2338 lv = new class_2338(l, i, n);
							if (method_16420(arg, lv)) {
								this.method_12773(set, arg, lv, this.field_13646, arg3);
							}
						}
					}
				}
			}
		}

		return true;
	}
}
