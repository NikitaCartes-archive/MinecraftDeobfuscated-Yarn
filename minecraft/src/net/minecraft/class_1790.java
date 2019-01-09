package net.minecraft;

import javax.annotation.Nullable;

public class class_1790 extends class_1792 {
	private final Class<? extends class_1530> field_7999;

	public class_1790(Class<? extends class_1530> class_, class_1792.class_1793 arg) {
		super(arg);
		this.field_7999 = class_;
	}

	@Override
	public class_1269 method_7884(class_1838 arg) {
		class_2338 lv = arg.method_8037();
		class_2350 lv2 = arg.method_8038();
		class_2338 lv3 = lv.method_10093(lv2);
		class_1657 lv4 = arg.method_8036();
		if (lv4 != null && !this.method_7834(lv4, lv2, arg.method_8041(), lv3)) {
			return class_1269.field_5814;
		} else {
			class_1937 lv5 = arg.method_8045();
			class_1530 lv6 = this.method_7835(lv5, lv3, lv2);
			if (lv6 != null && lv6.method_6888()) {
				if (!lv5.field_9236) {
					lv6.method_6894();
					lv5.method_8649(lv6);
				}

				arg.method_8041().method_7934(1);
			}

			return class_1269.field_5812;
		}
	}

	protected boolean method_7834(class_1657 arg, class_2350 arg2, class_1799 arg3, class_2338 arg4) {
		return !arg2.method_10166().method_10178() && arg.method_7343(arg4, arg2, arg3);
	}

	@Nullable
	private class_1530 method_7835(class_1937 arg, class_2338 arg2, class_2350 arg3) {
		if (this.field_7999 == class_1534.class) {
			return new class_1534(arg, arg2, arg3);
		} else {
			return this.field_7999 == class_1533.class ? new class_1533(arg, arg2, arg3) : null;
		}
	}
}
