package net.minecraft;

public interface class_3737 extends class_2263, class_2402 {
	@Override
	default boolean method_10310(class_1922 arg, class_2338 arg2, class_2680 arg3, class_3611 arg4) {
		return !(Boolean)arg3.method_11654(class_2741.field_12508) && arg4 == class_3612.field_15910;
	}

	@Override
	default boolean method_10311(class_1936 arg, class_2338 arg2, class_2680 arg3, class_3610 arg4) {
		if (!(Boolean)arg3.method_11654(class_2741.field_12508) && arg4.method_15772() == class_3612.field_15910) {
			if (!arg.method_8608()) {
				arg.method_8652(arg2, arg3.method_11657(class_2741.field_12508, Boolean.valueOf(true)), 3);
				arg.method_8405().method_8676(arg2, arg4.method_15772(), arg4.method_15772().method_15789(arg));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	default class_3611 method_9700(class_1936 arg, class_2338 arg2, class_2680 arg3) {
		if ((Boolean)arg3.method_11654(class_2741.field_12508)) {
			arg.method_8652(arg2, arg3.method_11657(class_2741.field_12508, Boolean.valueOf(false)), 3);
			return class_3612.field_15910;
		} else {
			return class_3612.field_15906;
		}
	}
}
