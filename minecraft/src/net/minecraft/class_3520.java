package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3520 extends class_3523<class_3527> {
	private static final class_2680 field_15660 = class_2246.field_10543.method_9564();
	private static final class_2680 field_15658 = class_2246.field_10515.method_9564();
	private static final class_2680 field_15659 = class_2246.field_10255.method_9564();
	private static final class_2680 field_15662 = class_2246.field_10114.method_9564();
	protected long field_15661;
	protected class_3537 field_15663;

	public class_3520(Function<Dynamic<?>, ? extends class_3527> function) {
		super(function);
	}

	public void method_15300(
		Random random, class_2791 arg, class_1959 arg2, int i, int j, int k, double d, class_2680 arg3, class_2680 arg4, int l, long m, class_3527 arg5
	) {
		int n = l + 1;
		int o = i & 15;
		int p = j & 15;
		double e = 0.03125;
		boolean bl = this.field_15663.method_15416((double)i * 0.03125, (double)j * 0.03125, 0.0) + random.nextDouble() * 0.2 > 0.0;
		boolean bl2 = this.field_15663.method_15416((double)i * 0.03125, 109.0, (double)j * 0.03125) + random.nextDouble() * 0.2 > 0.0;
		int q = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		class_2338.class_2339 lv = new class_2338.class_2339();
		int r = -1;
		class_2680 lv2 = field_15658;
		class_2680 lv3 = field_15658;

		for (int s = 127; s >= 0; s--) {
			lv.method_10103(o, s, p);
			class_2680 lv4 = arg.method_8320(lv);
			if (lv4.method_11614() != null && !lv4.method_11588()) {
				if (lv4.method_11614() == arg3.method_11614()) {
					if (r == -1) {
						if (q <= 0) {
							lv2 = field_15660;
							lv3 = field_15658;
						} else if (s >= n - 4 && s <= n + 1) {
							lv2 = field_15658;
							lv3 = field_15658;
							if (bl2) {
								lv2 = field_15659;
								lv3 = field_15658;
							}

							if (bl) {
								lv2 = field_15662;
								lv3 = field_15662;
							}
						}

						if (s < n && (lv2 == null || lv2.method_11588())) {
							lv2 = arg4;
						}

						r = q;
						if (s >= n - 1) {
							arg.method_12010(lv, lv2, false);
						} else {
							arg.method_12010(lv, lv3, false);
						}
					} else if (r > 0) {
						r--;
						arg.method_12010(lv, lv3, false);
					}
				}
			} else {
				r = -1;
			}
		}
	}

	@Override
	public void method_15306(long l) {
		if (this.field_15661 != l || this.field_15663 == null) {
			this.field_15663 = new class_3537(new class_2919(l), 4);
		}

		this.field_15661 = l;
	}
}
