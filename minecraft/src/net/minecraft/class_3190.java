package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class class_3190 extends class_2944<class_3111> {
	private static final class_2680 field_13855 = class_2246.field_10037.method_9564();
	private static final class_2680 field_13856 = class_2246.field_9988.method_9564();

	public class_3190(Function<Dynamic<?>, ? extends class_3111> function, boolean bl) {
		super(function, bl);
	}

	@Override
	public boolean method_12775(Set<class_2338> set, class_3747 arg, Random random, class_2338 arg2) {
		int i = random.nextInt(4) + 6;
		int j = 1 + random.nextInt(2);
		int k = i - j;
		int l = 2 + random.nextInt(2);
		boolean bl = true;
		if (arg2.method_10264() >= 1 && arg2.method_10264() + i + 1 <= 256) {
			for (int m = arg2.method_10264(); m <= arg2.method_10264() + 1 + i && bl; m++) {
				int n;
				if (m - arg2.method_10264() < j) {
					n = 0;
				} else {
					n = l;
				}

				class_2338.class_2339 lv = new class_2338.class_2339();

				for (int o = arg2.method_10263() - n; o <= arg2.method_10263() + n && bl; o++) {
					for (int p = arg2.method_10260() - n; p <= arg2.method_10260() + n && bl; p++) {
						if (m >= 0 && m < 256) {
							lv.method_10103(o, m, p);
							if (!method_16420(arg, lv)) {
								bl = false;
							}
						} else {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else if (method_16433(arg, arg2.method_10074()) && arg2.method_10264() < 256 - i - 1) {
				this.method_16427(arg, arg2.method_10074());
				int m = random.nextInt(2);
				int n = 1;
				int q = 0;

				for (int o = 0; o <= k; o++) {
					int px = arg2.method_10264() + i - o;

					for (int r = arg2.method_10263() - m; r <= arg2.method_10263() + m; r++) {
						int s = r - arg2.method_10263();

						for (int t = arg2.method_10260() - m; t <= arg2.method_10260() + m; t++) {
							int u = t - arg2.method_10260();
							if (Math.abs(s) != m || Math.abs(u) != m || m <= 0) {
								class_2338 lv2 = new class_2338(r, px, t);
								if (method_16420(arg, lv2) || method_16425(arg, lv2)) {
									this.method_13153(arg, lv2, field_13856);
								}
							}
						}
					}

					if (m >= n) {
						m = q;
						q = 1;
						if (++n > l) {
							n = l;
						}
					} else {
						m++;
					}
				}

				int o = random.nextInt(3);

				for (int px = 0; px < i - o; px++) {
					if (method_16420(arg, arg2.method_10086(px))) {
						this.method_12773(set, arg, arg2.method_10086(px), field_13855);
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
