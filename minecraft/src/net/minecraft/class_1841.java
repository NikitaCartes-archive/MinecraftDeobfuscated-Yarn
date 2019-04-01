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
		class_239 lv2 = method_7872(arg, arg2, class_3959.class_242.field_1345);
		if (lv2.method_17783() == class_239.class_240.field_1333) {
			return new class_1271<>(class_1269.field_5811, lv);
		} else {
			if (lv2.method_17783() == class_239.class_240.field_1332) {
				class_3965 lv3 = (class_3965)lv2;
				class_2338 lv4 = lv3.method_17777();
				class_2350 lv5 = lv3.method_17780();
				if (!arg.method_8505(arg2, lv4) || !arg2.method_7343(lv4.method_10093(lv5), lv5, lv)) {
					return new class_1271<>(class_1269.field_5814, lv);
				}

				class_2338 lv6 = lv4.method_10084();
				class_2680 lv7 = arg.method_8320(lv4);
				class_3614 lv8 = lv7.method_11620();
				class_3610 lv9 = arg.method_8316(lv4);
				if ((lv9.method_15772() == class_3612.field_15910 || lv8 == class_3614.field_15958) && arg.method_8623(lv6)) {
					arg.method_8652(lv6, class_2246.field_10588.method_9564(), 11);
					if (arg2 instanceof class_3222) {
						class_174.field_1191.method_9087((class_3222)arg2, lv6, lv);
					}

					if (!arg2.field_7503.field_7477) {
						lv.method_7934(1);
					}

					arg2.method_7259(class_3468.field_15372.method_14956(this));
					arg.method_8396(arg2, lv4, class_3417.field_15173, class_3419.field_15245, 1.0F, 1.0F);
					return new class_1271<>(class_1269.field_5812, lv);
				}
			}

			return new class_1271<>(class_1269.field_5814, lv);
		}
	}
}
