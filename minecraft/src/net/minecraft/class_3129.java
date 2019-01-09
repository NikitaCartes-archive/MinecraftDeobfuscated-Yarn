package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class class_3129 extends class_2944<class_3111> {
	private static final class_2680 field_13732 = class_2246.field_10037.method_9564();
	private static final class_2680 field_13733 = class_2246.field_9988.method_9564();

	public class_3129(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function, false);
	}

	@Override
	public boolean method_12775(Set<class_2338> set, class_3747 arg, Random random, class_2338 arg2) {
		int i = random.nextInt(5) + 7;
		int j = i - random.nextInt(2) - 3;
		int k = i - j;
		int l = 1 + random.nextInt(k + 1);
		if (arg2.method_10264() >= 1 && arg2.method_10264() + i + 1 <= 256) {
			boolean bl = true;

			for (int m = arg2.method_10264(); m <= arg2.method_10264() + 1 + i && bl; m++) {
				int n = 1;
				if (m - arg2.method_10264() < j) {
					n = 0;
				} else {
					n = l;
				}

				class_2338.class_2339 lv = new class_2338.class_2339();

				for (int o = arg2.method_10263() - n; o <= arg2.method_10263() + n && bl; o++) {
					for (int p = arg2.method_10260() - n; p <= arg2.method_10260() + n && bl; p++) {
						if (m < 0 || m >= 256) {
							bl = false;
						} else if (!method_16432(arg, lv.method_10103(o, m, p))) {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else if (method_16430(arg, arg2.method_10074()) && arg2.method_10264() < 256 - i - 1) {
				this.method_16427(arg, arg2.method_10074());
				int m = 0;

				for (int n = arg2.method_10264() + i; n >= arg2.method_10264() + j; n--) {
					for (int q = arg2.method_10263() - m; q <= arg2.method_10263() + m; q++) {
						int o = q - arg2.method_10263();

						for (int px = arg2.method_10260() - m; px <= arg2.method_10260() + m; px++) {
							int r = px - arg2.method_10260();
							if (Math.abs(o) != m || Math.abs(r) != m || m <= 0) {
								class_2338 lv2 = new class_2338(q, n, px);
								if (method_16420(arg, lv2)) {
									this.method_13153(arg, lv2, field_13733);
								}
							}
						}
					}

					if (m >= 1 && n == arg2.method_10264() + j + 1) {
						m--;
					} else if (m < l) {
						m++;
					}
				}

				for (int n = 0; n < i - 1; n++) {
					if (method_16420(arg, arg2.method_10086(n))) {
						this.method_12773(set, arg, arg2.method_10086(n), field_13732);
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
