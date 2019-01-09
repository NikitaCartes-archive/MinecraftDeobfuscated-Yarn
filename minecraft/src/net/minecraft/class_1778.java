package net.minecraft;

public class class_1778 extends class_1792 {
	public class_1778(class_1792.class_1793 arg) {
		super(arg);
	}

	@Override
	public class_1269 method_7884(class_1838 arg) {
		class_1937 lv = arg.method_8045();
		if (lv.field_9236) {
			return class_1269.field_5812;
		} else {
			class_2338 lv2 = arg.method_8037().method_10093(arg.method_8038());
			if (lv.method_8320(lv2).method_11588()) {
				lv.method_8396(null, lv2, class_3417.field_15013, class_3419.field_15245, 1.0F, (field_8005.nextFloat() - field_8005.nextFloat()) * 0.2F + 1.0F);
				lv.method_8501(lv2, ((class_2358)class_2246.field_10036).method_10198(lv, lv2));
			}

			arg.method_8041().method_7934(1);
			return class_1269.field_5812;
		}
	}
}
