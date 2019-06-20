package net.minecraft;

public class class_1777 extends class_1792 {
	public class_1777(class_1792.class_1793 arg) {
		super(arg);
	}

	@Override
	public class_1269 method_7884(class_1838 arg) {
		class_1937 lv = arg.method_8045();
		class_2338 lv2 = arg.method_8037();
		class_2680 lv3 = lv.method_8320(lv2);
		if (lv3.method_11614() != class_2246.field_10398 || (Boolean)lv3.method_11654(class_2333.field_10958)) {
			return class_1269.field_5811;
		} else if (lv.field_9236) {
			return class_1269.field_5812;
		} else {
			class_2680 lv4 = lv3.method_11657(class_2333.field_10958, Boolean.valueOf(true));
			class_2248.method_9582(lv3, lv4, lv, lv2);
			lv.method_8652(lv2, lv4, 2);
			lv.method_8455(lv2, class_2246.field_10398);
			arg.method_8041().method_7934(1);
			lv.method_20290(1503, lv2, 0);
			class_2700.class_2702 lv5 = class_2333.method_10054().method_11708(lv, lv2);
			if (lv5 != null) {
				class_2338 lv6 = lv5.method_11715().method_10069(-3, 0, -3);

				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						lv.method_8652(lv6.method_10069(i, 0, j), class_2246.field_10027.method_9564(), 2);
					}
				}

				lv.method_8474(1038, lv6.method_10069(1, 0, 1), 0);
			}

			return class_1269.field_5812;
		}
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		class_1799 lv = arg2.method_5998(arg3);
		class_239 lv2 = method_7872(arg, arg2, class_3959.class_242.field_1348);
		if (lv2.method_17783() == class_239.class_240.field_1332 && arg.method_8320(((class_3965)lv2).method_17777()).method_11614() == class_2246.field_10398) {
			return new class_1271<>(class_1269.field_5811, lv);
		} else {
			arg2.method_6019(arg3);
			if (!arg.field_9236) {
				class_2338 lv3 = arg.method_8398().method_12129().method_12103(arg, "Stronghold", new class_2338(arg2), 100, false);
				if (lv3 != null) {
					class_1672 lv4 = new class_1672(arg, arg2.field_5987, arg2.field_6010 + (double)(arg2.method_17682() / 2.0F), arg2.field_6035);
					lv4.method_16933(lv);
					lv4.method_7478(lv3);
					arg.method_8649(lv4);
					if (arg2 instanceof class_3222) {
						class_174.field_1186.method_9157((class_3222)arg2, lv3);
					}

					arg.method_8465(
						null,
						arg2.field_5987,
						arg2.field_6010,
						arg2.field_6035,
						class_3417.field_15155,
						class_3419.field_15254,
						0.5F,
						0.4F / (field_8005.nextFloat() * 0.4F + 0.8F)
					);
					arg.method_8444(null, 1003, new class_2338(arg2), 0);
					if (!arg2.field_7503.field_7477) {
						lv.method_7934(1);
					}

					arg2.method_7259(class_3468.field_15372.method_14956(this));
					return new class_1271<>(class_1269.field_5812, lv);
				}
			}

			return new class_1271<>(class_1269.field_5812, lv);
		}
	}
}
