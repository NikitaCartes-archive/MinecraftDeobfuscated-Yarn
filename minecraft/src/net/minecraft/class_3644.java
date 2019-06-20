package net.minecraft;

public enum class_3644 implements class_3658 {
	field_16105;

	@Override
	public int method_15855(class_3630 arg, int i, int j) {
		class_3756 lv = arg.method_15835();
		double d = lv.method_16447((double)i / 8.0, (double)j / 8.0, 0.0, 0.0, 0.0);
		if (d > 0.4) {
			return class_3645.field_16115;
		} else if (d > 0.2) {
			return class_3645.field_16114;
		} else if (d < -0.4) {
			return class_3645.field_16111;
		} else {
			return d < -0.2 ? class_3645.field_16112 : class_3645.field_16113;
		}
	}
}
