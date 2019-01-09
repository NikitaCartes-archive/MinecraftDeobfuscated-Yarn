package net.minecraft;

public class class_1841 extends class_1747 {
	public class_1841(class_2248 arg, class_1792.class_1793 arg2) {
		super(arg, arg2);
	}

	@Override
	public class_1269 method_7884(class_1838 arg) {
		return class_1269.field_5811;
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		class_1799 lv = arg2.method_5998(arg3);
		class_239 lv2 = this.method_7872(arg, arg2, true);
		if (lv2 == null) {
			return new class_1271<>(class_1269.field_5811, lv);
		} else {
			if (lv2.field_1330 == class_239.class_240.field_1332) {
				class_2338 lv3 = lv2.method_1015();
				if (!arg.method_8505(arg2, lv3) || !arg2.method_7343(lv3.method_10093(lv2.field_1327), lv2.field_1327, lv)) {
					return new class_1271<>(class_1269.field_5814, lv);
				}

				class_2338 lv4 = lv3.method_10084();
				class_2680 lv5 = arg.method_8320(lv3);
				class_3614 lv6 = lv5.method_11620();
				class_3610 lv7 = arg.method_8316(lv3);
				if ((lv7.method_15772() == class_3612.field_15910 || lv6 == class_3614.field_15958) && arg.method_8623(lv4)) {
					arg.method_8652(lv4, class_2246.field_10588.method_9564(), 11);
					if (arg2 instanceof class_3222) {
						class_174.field_1191.method_9087((class_3222)arg2, lv4, lv);
					}

					if (!arg2.field_7503.field_7477) {
						lv.method_7934(1);
					}

					arg2.method_7259(class_3468.field_15372.method_14956(this));
					arg.method_8396(arg2, lv3, class_3417.field_15173, class_3419.field_15245, 1.0F, 1.0F);
					return new class_1271<>(class_1269.field_5812, lv);
				}
			}

			return new class_1271<>(class_1269.field_5814, lv);
		}
	}
}
