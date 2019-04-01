package net.minecraft;

public class class_1805 extends class_1792 {
	public class_1805(class_1792.class_1793 arg) {
		super(arg);
	}

	@Override
	public class_1799 method_7861(class_1799 arg, class_1937 arg2, class_1309 arg3) {
		if (arg3 instanceof class_3222) {
			class_3222 lv = (class_3222)arg3;
			class_174.field_1198.method_8821(lv, arg);
			lv.method_7259(class_3468.field_15372.method_14956(this));
		}

		if (arg3 instanceof class_1657 && !((class_1657)arg3).field_7503.field_7477) {
			arg.method_7934(1);
		}

		if (!arg2.field_9236) {
			arg3.method_6012();
		}

		return arg.method_7960() ? new class_1799(class_1802.field_8550) : arg;
	}

	@Override
	public int method_7881(class_1799 arg) {
		return 32;
	}

	@Override
	public class_1839 method_7853(class_1799 arg) {
		return class_1839.field_8946;
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		arg2.method_6019(arg3);
		return new class_1271<>(class_1269.field_5812, arg2.method_5998(arg3));
	}
}
