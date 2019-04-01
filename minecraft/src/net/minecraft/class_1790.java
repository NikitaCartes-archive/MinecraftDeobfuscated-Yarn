package net.minecraft;

public class class_1790 extends class_1792 {
	private final class_1299<? extends class_1530> field_7999;

	public class_1790(class_1299<? extends class_1530> arg, class_1792.class_1793 arg2) {
		super(arg2);
		this.field_7999 = arg;
	}

	@Override
	public class_1269 method_7884(class_1838 arg) {
		class_2338 lv = arg.method_8037();
		class_2350 lv2 = arg.method_8038();
		class_2338 lv3 = lv.method_10093(lv2);
		class_1657 lv4 = arg.method_8036();
		class_1799 lv5 = arg.method_8041();
		if (lv4 != null && !this.method_7834(lv4, lv2, lv5, lv3)) {
			return class_1269.field_5814;
		} else {
			class_1937 lv6 = arg.method_8045();
			class_1530 lv7;
			if (this.field_7999 == class_1299.field_6120) {
				lv7 = new class_1534(lv6, lv3, lv2);
			} else {
				if (this.field_7999 != class_1299.field_6043) {
					return class_1269.field_5812;
				}

				lv7 = new class_1533(lv6, lv3, lv2);
			}

			class_2487 lv8 = lv5.method_7969();
			if (lv8 != null) {
				class_1299.method_5881(lv6, lv4, lv7, lv8);
			}

			if (lv7.method_6888()) {
				if (!lv6.field_9236) {
					lv7.method_6894();
					lv6.method_8649(lv7);
				}

				lv5.method_7934(1);
			}

			return class_1269.field_5812;
		}
	}

	protected boolean method_7834(class_1657 arg, class_2350 arg2, class_1799 arg3, class_2338 arg4) {
		return !arg2.method_10166().method_10178() && arg.method_7343(arg4, arg2, arg3);
	}
}
