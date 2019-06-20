package net.minecraft;

import javax.annotation.Nullable;

public class class_1378 extends class_1379 {
	public class_1378(class_1314 arg, double d, int i) {
		super(arg, d, i);
	}

	@Nullable
	@Override
	protected class_243 method_6302() {
		class_243 lv = class_1414.method_6375(this.field_6566, 10, 7);
		int i = 0;

		while (
			lv != null
				&& !this.field_6566.field_6002.method_8320(new class_2338(lv)).method_11609(this.field_6566.field_6002, new class_2338(lv), class_10.field_48)
				&& i++ < 10
		) {
			lv = class_1414.method_6375(this.field_6566, 10, 7);
		}

		return lv;
	}
}
