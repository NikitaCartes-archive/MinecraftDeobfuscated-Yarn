package net.minecraft;

public class class_1816 extends class_1792 {
	public class_1816(class_1792.class_1793 arg) {
		super(arg);
	}

	@Override
	public boolean method_7847(class_1799 arg, class_1657 arg2, class_1309 arg3, class_1268 arg4) {
		if (arg3 instanceof class_1452) {
			class_1452 lv = (class_1452)arg3;
			if (lv.method_5805() && !lv.method_6575() && !lv.method_6109()) {
				lv.method_6576(true);
				lv.field_6002.method_8465(arg2, lv.field_5987, lv.field_6010, lv.field_6035, class_3417.field_14824, class_3419.field_15254, 0.5F, 1.0F);
				arg.method_7934(1);
			}

			return true;
		} else {
			return false;
		}
	}
}
