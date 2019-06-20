package net.minecraft;

public class class_1786 extends class_1792 {
	public class_1786(class_1792.class_1793 arg) {
		super(arg);
	}

	@Override
	public class_1269 method_7884(class_1838 arg) {
		class_1657 lv = arg.method_8036();
		class_1936 lv2 = arg.method_8045();
		class_2338 lv3 = arg.method_8037();
		class_2338 lv4 = lv3.method_10093(arg.method_8038());
		if (method_7825(lv2.method_8320(lv4), lv2, lv4)) {
			lv2.method_8396(lv, lv4, class_3417.field_15145, class_3419.field_15245, 1.0F, field_8005.nextFloat() * 0.4F + 0.8F);
			class_2680 lv5 = ((class_2358)class_2246.field_10036).method_10198(lv2, lv4);
			lv2.method_8652(lv4, lv5, 11);
			class_1799 lv6 = arg.method_8041();
			if (lv instanceof class_3222) {
				class_174.field_1191.method_9087((class_3222)lv, lv4, lv6);
				lv6.method_7956(1, lv, arg2 -> arg2.method_20236(arg.method_20287()));
			}

			return class_1269.field_5812;
		} else {
			class_2680 lv5 = lv2.method_8320(lv3);
			if (method_17439(lv5)) {
				lv2.method_8396(lv, lv3, class_3417.field_15145, class_3419.field_15245, 1.0F, field_8005.nextFloat() * 0.4F + 0.8F);
				lv2.method_8652(lv3, lv5.method_11657(class_2741.field_12548, Boolean.valueOf(true)), 11);
				if (lv != null) {
					arg.method_8041().method_7956(1, lv, arg2 -> arg2.method_20236(arg.method_20287()));
				}

				return class_1269.field_5812;
			} else {
				return class_1269.field_5814;
			}
		}
	}

	public static boolean method_17439(class_2680 arg) {
		return arg.method_11614() == class_2246.field_17350
			&& !(Boolean)arg.method_11654(class_2741.field_12508)
			&& !(Boolean)arg.method_11654(class_2741.field_12548);
	}

	public static boolean method_7825(class_2680 arg, class_1936 arg2, class_2338 arg3) {
		class_2680 lv = ((class_2358)class_2246.field_10036).method_10198(arg2, arg3);
		boolean bl = false;

		for (class_2350 lv2 : class_2350.class_2353.field_11062) {
			if (arg2.method_8320(arg3.method_10093(lv2)).method_11614() == class_2246.field_10540
				&& ((class_2423)class_2246.field_10316).method_10351(arg2, arg3) != null) {
				bl = true;
			}
		}

		return arg.method_11588() && (lv.method_11591(arg2, arg3) || bl);
	}
}
