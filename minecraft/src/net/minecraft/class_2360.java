package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2360 extends class_2386 {
	public static final class_2758 field_11097 = class_2741.field_12497;

	public class_2360(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11097, Integer.valueOf(0)));
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if ((random.nextInt(3) == 0 || this.method_10202(arg2, arg3, 4))
			&& arg2.method_8602(arg3) > 11 - (Integer)arg.method_11654(field_11097) - arg.method_11581(arg2, arg3)
			&& this.method_10201(arg, arg2, arg3)) {
			try (class_2338.class_2340 lv = class_2338.class_2340.method_10109()) {
				for (class_2350 lv2 : class_2350.values()) {
					lv.method_10114(arg3).method_10118(lv2);
					class_2680 lv3 = arg2.method_8320(lv);
					if (lv3.method_11614() == this && !this.method_10201(lv3, arg2, lv)) {
						arg2.method_8397().method_8676(lv, this, class_3532.method_15395(random, 20, 40));
					}
				}
			}
		} else {
			arg2.method_8397().method_8676(arg3, this, class_3532.method_15395(random, 20, 40));
		}
	}

	private boolean method_10201(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		int i = (Integer)arg.method_11654(field_11097);
		if (i < 3) {
			arg2.method_8652(arg3, arg.method_11657(field_11097, Integer.valueOf(i + 1)), 2);
			return false;
		} else {
			this.method_10275(arg, arg2, arg3);
			return true;
		}
	}

	@Override
	public void method_9612(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4, class_2338 arg5, boolean bl) {
		if (arg4 == this && this.method_10202(arg2, arg3, 2)) {
			this.method_10275(arg, arg2, arg3);
		}

		super.method_9612(arg, arg2, arg3, arg4, arg5, bl);
	}

	private boolean method_10202(class_1922 arg, class_2338 arg2, int i) {
		int j = 0;

		try (class_2338.class_2340 lv = class_2338.class_2340.method_10109()) {
			for (class_2350 lv2 : class_2350.values()) {
				lv.method_10114(arg2).method_10118(lv2);
				if (arg.method_8320(lv).method_11614() == this) {
					if (++j >= i) {
						return false;
					}
				}
			}

			return true;
		}
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11097);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_9574(class_1922 arg, class_2338 arg2, class_2680 arg3) {
		return class_1799.field_8037;
	}
}
