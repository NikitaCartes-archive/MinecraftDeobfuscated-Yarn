package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class class_2947 extends class_2944<class_3111> {
	private static final class_2680 field_13340 = class_2246.field_10511.method_9564();
	private static final class_2680 field_13341 = class_2246.field_10539.method_9564();
	private final boolean field_13339;

	public class_2947(Function<Dynamic<?>, ? extends class_3111> function, boolean bl, boolean bl2) {
		super(function, bl);
		this.field_13339 = bl2;
	}

	@Override
	public boolean method_12775(Set<class_2338> set, class_3747 arg, Random random, class_2338 arg2) {
		int i = random.nextInt(3) + 5;
		if (this.field_13339) {
			i += random.nextInt(7);
		}

		boolean bl = true;
		if (arg2.method_10264() >= 1 && arg2.method_10264() + i + 1 <= 256) {
			for (int j = arg2.method_10264(); j <= arg2.method_10264() + 1 + i; j++) {
				int k = 1;
				if (j == arg2.method_10264()) {
					k = 0;
				}

				if (j >= arg2.method_10264() + 1 + i - 2) {
					k = 2;
				}

				class_2338.class_2339 lv = new class_2338.class_2339();

				for (int l = arg2.method_10263() - k; l <= arg2.method_10263() + k && bl; l++) {
					for (int m = arg2.method_10260() - k; m <= arg2.method_10260() + k && bl; m++) {
						if (j < 0 || j >= 256) {
							bl = false;
						} else if (!method_16432(arg, lv.method_10103(l, j, m))) {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else if (method_16433(arg, arg2.method_10074()) && arg2.method_10264() < 256 - i - 1) {
				this.method_16427(arg, arg2.method_10074());

				for (int j = arg2.method_10264() - 3 + i; j <= arg2.method_10264() + i; j++) {
					int kx = j - (arg2.method_10264() + i);
					int n = 1 - kx / 2;

					for (int l = arg2.method_10263() - n; l <= arg2.method_10263() + n; l++) {
						int mx = l - arg2.method_10263();

						for (int o = arg2.method_10260() - n; o <= arg2.method_10260() + n; o++) {
							int p = o - arg2.method_10260();
							if (Math.abs(mx) != n || Math.abs(p) != n || random.nextInt(2) != 0 && kx != 0) {
								class_2338 lv2 = new class_2338(l, j, o);
								if (method_16420(arg, lv2)) {
									this.method_13153(arg, lv2, field_13341);
								}
							}
						}
					}
				}

				for (int j = 0; j < i; j++) {
					if (method_16420(arg, arg2.method_10086(j))) {
						this.method_12773(set, arg, arg2.method_10086(j), field_13340);
					}
				}

				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
