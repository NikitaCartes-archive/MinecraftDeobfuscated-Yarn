package net.minecraft;

public class class_1773 extends class_1762 {
	public class_1773(class_1792.class_1793 arg) {
		super(arg);
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		class_1799 lv = class_1806.method_8005(arg, class_3532.method_15357(arg2.field_5987), class_3532.method_15357(arg2.field_6035), (byte)0, true, false);
		class_1799 lv2 = arg2.method_5998(arg3);
		if (!arg2.field_7503.field_7477) {
			lv2.method_7934(1);
		}

		if (lv2.method_7960()) {
			return new class_1271<>(class_1269.field_5812, lv);
		} else {
			if (!arg2.field_7514.method_7394(lv.method_7972())) {
				arg2.method_7328(lv, false);
			}

			arg2.method_7259(class_3468.field_15372.method_14956(this));
			return new class_1271<>(class_1269.field_5812, lv2);
		}
	}
}
