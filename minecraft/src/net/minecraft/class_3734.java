package net.minecraft;

public class class_3734 extends class_1747 {
	public class_3734(class_2248 arg, class_1792.class_1793 arg2) {
		super(arg, arg2);
	}

	@Override
	public class_1750 method_16356(class_1750 arg) {
		class_2338 lv = arg.method_8037();
		class_1937 lv2 = arg.method_8045();
		class_2680 lv3 = lv2.method_8320(lv);
		if (lv3.method_11614() == this.method_7711() && arg.method_8038().method_10166() == class_2350.class_2351.field_11052) {
			class_2338.class_2339 lv4 = new class_2338.class_2339(lv).method_10098(class_2350.field_11036);

			while (lv3.method_11614() == this.method_7711() && !class_1937.method_8518(lv4)) {
				lv3 = lv2.method_8320(lv4);
				if (lv3.method_11588()) {
					return class_1750.method_16355(arg, lv4, class_2350.field_11036);
				}

				lv4.method_10098(class_2350.field_11036);
			}
		}

		return arg;
	}
}
