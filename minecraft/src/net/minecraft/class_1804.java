package net.minecraft;

public class class_1804 extends class_1792 {
	public class_1804(class_1792.class_1793 arg) {
		super(arg);
	}

	@Override
	public class_1269 method_7884(class_1838 arg) {
		class_1937 lv = arg.method_8045();
		class_2338 lv2 = arg.method_8037();
		class_2248 lv3 = lv.method_8320(lv2).method_11614();
		if (lv3.method_9525(class_3481.field_16584)) {
			class_1657 lv4 = arg.method_8036();
			if (!lv.field_9236 && lv4 != null) {
				method_7994(lv4, lv, lv2);
			}

			return class_1269.field_5812;
		} else {
			return class_1269.field_5811;
		}
	}

	public static boolean method_7994(class_1657 arg, class_1937 arg2, class_2338 arg3) {
		class_1532 lv = class_1532.method_6932(arg2, arg3);
		boolean bl = false;
		double d = 7.0;
		int i = arg3.method_10263();
		int j = arg3.method_10264();
		int k = arg3.method_10260();

		for (class_1308 lv2 : arg2.method_8403(
			class_1308.class, new class_238((double)i - 7.0, (double)j - 7.0, (double)k - 7.0, (double)i + 7.0, (double)j + 7.0, (double)k + 7.0)
		)) {
			if (lv2.method_5934() && lv2.method_5933() == arg) {
				if (lv == null) {
					lv = class_1532.method_6931(arg2, arg3);
				}

				lv2.method_5954(lv, true);
				bl = true;
			}
		}

		return bl;
	}
}
