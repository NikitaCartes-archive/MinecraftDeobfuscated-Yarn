package net.minecraft;

public class class_1815 extends class_1789 {
	private final class_2680 field_8909;

	public class_1815(int i, float f, class_2248 arg, class_1792.class_1793 arg2) {
		super(i, f, false, arg2);
		this.field_8909 = arg.method_9564();
	}

	@Override
	public class_1269 method_7884(class_1838 arg) {
		class_1936 lv = arg.method_8045();
		class_2338 lv2 = arg.method_8037().method_10084();
		if (arg.method_8038() == class_2350.field_11036 && lv.method_8623(lv2) && this.field_8909.method_11591(lv, lv2)) {
			lv.method_8652(lv2, this.field_8909, 11);
			class_1657 lv3 = arg.method_8036();
			class_1799 lv4 = arg.method_8041();
			if (lv3 instanceof class_3222) {
				class_174.field_1191.method_9087((class_3222)lv3, lv2, lv4);
			}

			lv4.method_7934(1);
			return class_1269.field_5812;
		} else {
			return class_1269.field_5811;
		}
	}
}
