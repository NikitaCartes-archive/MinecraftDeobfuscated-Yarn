package net.minecraft;

import java.util.Random;

public class class_2420 extends class_2261 implements class_2256 {
	protected static final class_265 field_11304 = class_2248.method_9541(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);

	public class_2420(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_11304;
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (random.nextInt(25) == 0) {
			int i = 5;
			int j = 4;

			for (class_2338 lv : class_2338.method_10097(arg3.method_10069(-4, -1, -4), arg3.method_10069(4, 1, 4))) {
				if (arg2.method_8320(lv).method_11614() == this) {
					if (--i <= 0) {
						return;
					}
				}
			}

			class_2338 lv2 = arg3.method_10069(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);

			for (int k = 0; k < 4; k++) {
				if (arg2.method_8623(lv2) && arg.method_11591(arg2, lv2)) {
					arg3 = lv2;
				}

				lv2 = arg3.method_10069(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
			}

			if (arg2.method_8623(lv2) && arg.method_11591(arg2, lv2)) {
				arg2.method_8652(lv2, arg, 2);
			}
		}
	}

	@Override
	protected boolean method_9695(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return arg.method_11598(arg2, arg3);
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2338 lv = arg3.method_10074();
		class_2680 lv2 = arg2.method_8320(lv);
		class_2248 lv3 = lv2.method_11614();
		return lv3 != class_2246.field_10402 && lv3 != class_2246.field_10520 ? arg2.method_8624(arg3, 0) < 13 && this.method_9695(lv2, arg2, lv) : true;
	}

	public boolean method_10349(class_1936 arg, class_2338 arg2, class_2680 arg3, Random random) {
		arg.method_8650(arg2, false);
		class_3031<class_3111> lv = null;
		if (this == class_2246.field_10251) {
			lv = class_3031.field_13531;
		} else if (this == class_2246.field_10559) {
			lv = class_3031.field_13571;
		}

		if (lv != null && lv.method_13151(arg, (class_2794<? extends class_2888>)arg.method_8398().method_12129(), random, arg2, class_3037.field_13603)) {
			return true;
		} else {
			arg.method_8652(arg2, arg3, 3);
			return false;
		}
	}

	@Override
	public boolean method_9651(class_1922 arg, class_2338 arg2, class_2680 arg3, boolean bl) {
		return true;
	}

	@Override
	public boolean method_9650(class_1937 arg, Random random, class_2338 arg2, class_2680 arg3) {
		return (double)random.nextFloat() < 0.4;
	}

	@Override
	public void method_9652(class_1937 arg, Random random, class_2338 arg2, class_2680 arg3) {
		this.method_10349(arg, arg2, arg3, random);
	}

	@Override
	public boolean method_9552(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return true;
	}
}
