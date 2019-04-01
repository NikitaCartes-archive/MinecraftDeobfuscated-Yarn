package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3512 extends class_3523<class_3527> {
	protected static final class_2680 field_15640 = class_2246.field_10225.method_9564();
	protected static final class_2680 field_15645 = class_2246.field_10491.method_9564();
	private static final class_2680 field_15643 = class_2246.field_10124.method_9564();
	private static final class_2680 field_15638 = class_2246.field_10255.method_9564();
	private static final class_2680 field_15639 = class_2246.field_10295.method_9564();
	private class_3543 field_15644;
	private class_3543 field_15642;
	private long field_15641;

	public class_3512(Function<Dynamic<?>, ? extends class_3527> function) {
		super(function);
	}

	public void method_15221(
		Random random, class_2791 arg, class_1959 arg2, int i, int j, int k, double d, class_2680 arg3, class_2680 arg4, int l, long m, class_3527 arg5
	) {
		double e = 0.0;
		double f = 0.0;
		class_2338.class_2339 lv = new class_2338.class_2339();
		float g = arg2.method_8707(lv.method_10103(i, 63, j));
		double h = Math.min(Math.abs(d), this.field_15644.method_15437((double)i * 0.1, (double)j * 0.1));
		if (h > 1.8) {
			double n = 0.09765625;
			double o = Math.abs(this.field_15642.method_15437((double)i * 0.09765625, (double)j * 0.09765625));
			e = h * h * 1.2;
			double p = Math.ceil(o * 40.0) + 14.0;
			if (e > p) {
				e = p;
			}

			if (g > 0.1F) {
				e -= 2.0;
			}

			if (e > 2.0) {
				f = (double)l - e - 7.0;
				e += (double)l;
			} else {
				e = 0.0;
			}
		}

		int q = i & 15;
		int r = j & 15;
		class_2680 lv2 = arg2.method_8722().method_15336();
		class_2680 lv3 = arg2.method_8722().method_15337();
		int s = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		int t = -1;
		int u = 0;
		int v = 2 + random.nextInt(4);
		int w = l + 18 + random.nextInt(10);

		for (int x = Math.max(k, (int)e + 1); x >= 0; x--) {
			lv.method_10103(q, x, r);
			if (arg.method_8320(lv).method_11588() && x < (int)e && random.nextDouble() > 0.01) {
				arg.method_12010(lv, field_15640, false);
			} else if (arg.method_8320(lv).method_11620() == class_3614.field_15920 && x > (int)f && x < l && f != 0.0 && random.nextDouble() > 0.15) {
				arg.method_12010(lv, field_15640, false);
			}

			class_2680 lv4 = arg.method_8320(lv);
			if (lv4.method_11588()) {
				t = -1;
			} else if (lv4.method_11614() == arg3.method_11614()) {
				if (t == -1) {
					if (s <= 0) {
						lv3 = field_15643;
						lv2 = arg3;
					} else if (x >= l - 4 && x <= l + 1) {
						lv3 = arg2.method_8722().method_15337();
						lv2 = arg2.method_8722().method_15336();
					}

					if (x < l && (lv3 == null || lv3.method_11588())) {
						if (arg2.method_8707(lv.method_10103(i, x, j)) < 0.15F) {
							lv3 = field_15639;
						} else {
							lv3 = arg4;
						}
					}

					t = s;
					if (x >= l - 1) {
						arg.method_12010(lv, lv3, false);
					} else if (x < l - 7 - s) {
						lv3 = field_15643;
						lv2 = arg3;
						arg.method_12010(lv, field_15638, false);
					} else {
						arg.method_12010(lv, lv2, false);
					}
				} else if (t > 0) {
					t--;
					arg.method_12010(lv, lv2, false);
					if (t == 0 && lv2.method_11614() == class_2246.field_10102 && s > 1) {
						t = random.nextInt(4) + Math.max(0, x - 63);
						lv2 = lv2.method_11614() == class_2246.field_10534 ? class_2246.field_10344.method_9564() : class_2246.field_9979.method_9564();
					}
				}
			} else if (lv4.method_11614() == class_2246.field_10225 && u <= v && x > w) {
				arg.method_12010(lv, field_15645, false);
				u++;
			}
		}
	}

	@Override
	public void method_15306(long l) {
		if (this.field_15641 != l || this.field_15644 == null || this.field_15642 == null) {
			Random random = new class_2919(l);
			this.field_15644 = new class_3543(random, 4);
			this.field_15642 = new class_3543(random, 1);
		}

		this.field_15641 = l;
	}
}
