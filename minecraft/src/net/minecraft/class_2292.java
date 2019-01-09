package net.minecraft;

public class class_2292 extends class_2346 {
	private final class_2680 field_10810;

	public class_2292(class_2248 arg, class_2248.class_2251 arg2) {
		super(arg2);
		this.field_10810 = arg.method_9564();
	}

	@Override
	public void method_10127(class_1937 arg, class_2338 arg2, class_2680 arg3, class_2680 arg4) {
		if (method_9799(arg4)) {
			arg.method_8652(arg2, this.field_10810, 3);
		}
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_1922 lv = arg.method_8045();
		class_2338 lv2 = arg.method_8037();
		return !method_9799(lv.method_8320(lv2)) && !method_9798(lv, lv2) ? super.method_9605(arg) : this.field_10810;
	}

	private static boolean method_9798(class_1922 arg, class_2338 arg2) {
		boolean bl = false;
		class_2338.class_2339 lv = new class_2338.class_2339(arg2);

		for (class_2350 lv2 : class_2350.values()) {
			class_2680 lv3 = arg.method_8320(lv);
			if (lv2 != class_2350.field_11033 || method_9799(lv3)) {
				lv.method_10101(arg2).method_10098(lv2);
				lv3 = arg.method_8320(lv);
				if (method_9799(lv3) && !class_2248.method_9501(lv3.method_11628(arg, arg2), lv2.method_10153())) {
					bl = true;
					break;
				}
			}
		}

		return bl;
	}

	private static boolean method_9799(class_2680 arg) {
		return arg.method_11618().method_15767(class_3486.field_15517);
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return method_9798(arg4, arg5) ? this.field_10810 : super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}
}
