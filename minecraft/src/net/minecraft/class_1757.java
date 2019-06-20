package net.minecraft;

public class class_1757 extends class_1792 {
	public class_1757(class_1792.class_1793 arg) {
		super(arg);
	}

	@Override
	public class_1799 method_7861(class_1799 arg, class_1937 arg2, class_1309 arg3) {
		class_1799 lv = super.method_7861(arg, arg2, arg3);
		if (!arg2.field_9236) {
			double d = arg3.field_5987;
			double e = arg3.field_6010;
			double f = arg3.field_6035;

			for (int i = 0; i < 16; i++) {
				double g = arg3.field_5987 + (arg3.method_6051().nextDouble() - 0.5) * 16.0;
				double h = class_3532.method_15350(arg3.field_6010 + (double)(arg3.method_6051().nextInt(16) - 8), 0.0, (double)(arg2.method_8456() - 1));
				double j = arg3.field_6035 + (arg3.method_6051().nextDouble() - 0.5) * 16.0;
				if (arg3.method_5765()) {
					arg3.method_5848();
				}

				if (arg3.method_6082(g, h, j, true)) {
					arg2.method_8465(null, d, e, f, class_3417.field_14890, class_3419.field_15248, 1.0F, 1.0F);
					arg3.method_5783(class_3417.field_14890, 1.0F, 1.0F);
					break;
				}
			}

			if (arg3 instanceof class_1657) {
				((class_1657)arg3).method_7357().method_7906(this, 20);
			}
		}

		return lv;
	}
}
