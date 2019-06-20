package net.minecraft;

public class class_2325 extends class_2315 {
	private static final class_2357 field_10949 = new class_2347();

	public class_2325(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	protected class_2357 method_10011(class_1799 arg) {
		return field_10949;
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_2608();
	}

	@Override
	protected void method_10012(class_1937 arg, class_2338 arg2) {
		class_2345 lv = new class_2345(arg, arg2);
		class_2601 lv2 = lv.method_10121();
		int i = lv2.method_11076();
		if (i < 0) {
			arg.method_20290(1001, arg2, 0);
		} else {
			class_1799 lv3 = lv2.method_5438(i);
			if (!lv3.method_7960()) {
				class_2350 lv4 = arg.method_8320(arg2).method_11654(field_10918);
				class_1263 lv5 = class_2614.method_11250(arg, arg2.method_10093(lv4));
				class_1799 lv6;
				if (lv5 == null) {
					lv6 = field_10949.dispense(lv, lv3);
				} else {
					lv6 = class_2614.method_11260(lv2, lv5, lv3.method_7972().method_7971(1), lv4.method_10153());
					if (lv6.method_7960()) {
						lv6 = lv3.method_7972();
						lv6.method_7934(1);
					} else {
						lv6 = lv3.method_7972();
					}
				}

				lv2.method_5447(i, lv6);
			}
		}
	}
}
