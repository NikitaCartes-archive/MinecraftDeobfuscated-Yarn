package net.minecraft;

import javax.annotation.Nullable;

public class class_4291 extends class_1379 {
	public class_4291(class_1314 arg, double d) {
		super(arg, d, 10);
	}

	@Override
	public boolean method_6264() {
		class_3218 lv = (class_3218)this.field_6566.field_6002;
		class_2338 lv2 = new class_2338(this.field_6566);
		return lv.method_19500(lv2) ? false : super.method_6264();
	}

	@Nullable
	@Override
	protected class_243 method_6302() {
		class_3218 lv = (class_3218)this.field_6566.field_6002;
		class_2338 lv2 = new class_2338(this.field_6566);
		class_4076 lv3 = class_4076.method_18682(lv2);
		class_4076 lv4 = class_4215.method_20419(lv, lv3, 2);
		if (lv4 != lv3) {
			class_2338 lv5 = lv4.method_19768();
			return class_1414.method_6373(this.field_6566, 10, 7, new class_243((double)lv5.method_10263(), (double)lv5.method_10264(), (double)lv5.method_10260()));
		} else {
			return null;
		}
	}
}
