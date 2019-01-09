package net.minecraft;

import javax.annotation.Nullable;

public class class_1840 extends class_1792 {
	public class_1840(class_1792.class_1793 arg) {
		super(arg);
	}

	@Override
	public class_1269 method_7884(class_1838 arg) {
		class_1937 lv = arg.method_8045();
		class_2338 lv2 = arg.method_8037();
		class_2680 lv3 = lv.method_8320(lv2);
		if (lv3.method_11614() == class_2246.field_16330) {
			return class_3715.method_17472(lv, lv2, lv3, arg.method_8041()) ? class_1269.field_5812 : class_1269.field_5811;
		} else {
			return class_1269.field_5811;
		}
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		class_1799 lv = arg2.method_5998(arg3);
		arg2.method_7315(lv, arg3);
		arg2.method_7259(class_3468.field_15372.method_14956(this));
		return new class_1271<>(class_1269.field_5812, lv);
	}

	public static boolean method_8047(@Nullable class_2487 arg) {
		if (arg == null) {
			return false;
		} else if (!arg.method_10573("pages", 9)) {
			return false;
		} else {
			class_2499 lv = arg.method_10554("pages", 8);

			for (int i = 0; i < lv.size(); i++) {
				String string = lv.method_10608(i);
				if (string.length() > 32767) {
					return false;
				}
			}

			return true;
		}
	}
}
