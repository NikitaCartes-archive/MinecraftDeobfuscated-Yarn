package net.minecraft;

import java.util.Random;

public class class_2523 extends class_2248 {
	public static final class_2758 field_11610 = class_2741.field_12498;
	protected static final class_265 field_11611 = class_2248.method_9541(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

	protected class_2523(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11610, Integer.valueOf(0)));
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return field_11611;
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (arg.method_11591(arg2, arg3) && arg2.method_8623(arg3.method_10084())) {
			int i = 1;

			while (arg2.method_8320(arg3.method_10087(i)).method_11614() == this) {
				i++;
			}

			if (i < 3) {
				int j = (Integer)arg.method_11654(field_11610);
				if (j == 15) {
					arg2.method_8501(arg3.method_10084(), this.method_9564());
					arg2.method_8652(arg3, arg.method_11657(field_11610, Integer.valueOf(0)), 4);
				} else {
					arg2.method_8652(arg3, arg.method_11657(field_11610, Integer.valueOf(j + 1)), 4);
				}
			}
		}
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return !arg.method_11591(arg4, arg5) ? class_2246.field_10124.method_9564() : super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2248 lv = arg2.method_8320(arg3.method_10074()).method_11614();
		if (lv == this) {
			return true;
		} else {
			if (lv == class_2246.field_10219
				|| lv == class_2246.field_10566
				|| lv == class_2246.field_10253
				|| lv == class_2246.field_10520
				|| lv == class_2246.field_10102
				|| lv == class_2246.field_10534) {
				class_2338 lv2 = arg3.method_10074();

				for (class_2350 lv3 : class_2350.class_2353.field_11062) {
					class_2680 lv4 = arg2.method_8320(lv2.method_10093(lv3));
					class_3610 lv5 = arg2.method_8316(lv2.method_10093(lv3));
					if (lv5.method_15767(class_3486.field_15517) || lv4.method_11614() == class_2246.field_10110) {
						return true;
					}
				}
			}

			return false;
		}
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11610);
	}
}
