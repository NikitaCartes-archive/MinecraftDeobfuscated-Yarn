package net.minecraft;

public class class_1820 extends class_1792 {
	public class_1820(class_1792.class_1793 arg) {
		super(arg);
	}

	@Override
	public boolean method_7879(class_1799 arg, class_1937 arg2, class_2680 arg3, class_2338 arg4, class_1309 arg5) {
		if (!arg2.field_9236) {
			arg.method_7956(1, arg5, argx -> argx.method_20235(class_1304.field_6173));
		}

		class_2248 lv = arg3.method_11614();
		return !arg3.method_11602(class_3481.field_15503)
				&& lv != class_2246.field_10343
				&& lv != class_2246.field_10479
				&& lv != class_2246.field_10112
				&& lv != class_2246.field_10428
				&& lv != class_2246.field_10597
				&& lv != class_2246.field_10589
				&& !lv.method_9525(class_3481.field_15481)
			? super.method_7879(arg, arg2, arg3, arg4, arg5)
			: true;
	}

	@Override
	public boolean method_7856(class_2680 arg) {
		class_2248 lv = arg.method_11614();
		return lv == class_2246.field_10343 || lv == class_2246.field_10091 || lv == class_2246.field_10589;
	}

	@Override
	public float method_7865(class_1799 arg, class_2680 arg2) {
		class_2248 lv = arg2.method_11614();
		if (lv == class_2246.field_10343 || arg2.method_11602(class_3481.field_15503)) {
			return 15.0F;
		} else {
			return lv.method_9525(class_3481.field_15481) ? 5.0F : super.method_7865(arg, arg2);
		}
	}
}
