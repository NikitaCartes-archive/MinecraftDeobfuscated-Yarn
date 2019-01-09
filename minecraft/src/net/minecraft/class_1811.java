package net.minecraft;

public class class_1811 extends class_1792 {
	public class_1811(class_1792.class_1793 arg) {
		super(arg);
	}

	protected class_1799 method_8008(class_1657 arg) {
		if (this.method_8007(arg.method_5998(class_1268.field_5810))) {
			return arg.method_5998(class_1268.field_5810);
		} else if (this.method_8007(arg.method_5998(class_1268.field_5808))) {
			return arg.method_5998(class_1268.field_5808);
		} else {
			for (int i = 0; i < arg.field_7514.method_5439(); i++) {
				class_1799 lv = arg.field_7514.method_5438(i);
				if (this.method_8007(lv)) {
					return lv;
				}
			}

			return class_1799.field_8037;
		}
	}

	protected boolean method_8007(class_1799 arg) {
		return arg.method_7909() instanceof class_1744;
	}

	@Override
	public int method_7837() {
		return 1;
	}
}
