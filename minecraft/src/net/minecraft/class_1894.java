package net.minecraft;

public class class_1894 extends class_1887 {
	public class_1894(class_1887.class_1888 arg, class_1304... args) {
		super(arg, class_1886.field_9079, args);
	}

	@Override
	public int method_8182(int i) {
		return i * 10;
	}

	@Override
	public boolean method_8193() {
		return true;
	}

	@Override
	public int method_8183() {
		return 2;
	}

	public static void method_8236(class_1309 arg, class_1937 arg2, class_2338 arg3, int i) {
		if (arg.field_5952) {
			class_2680 lv = class_2246.field_10110.method_9564();
			float f = (float)Math.min(16, 2 + i);
			class_2338.class_2339 lv2 = new class_2338.class_2339(0, 0, 0);

			for (class_2338.class_2339 lv3 : class_2338.method_10082(arg3.method_10080((double)(-f), -1.0, (double)(-f)), arg3.method_10080((double)f, -1.0, (double)f))) {
				if (lv3.method_10268(arg.field_5987, arg.field_6010, arg.field_6035) <= (double)(f * f)) {
					lv2.method_10103(lv3.method_10263(), lv3.method_10264() + 1, lv3.method_10260());
					class_2680 lv4 = arg2.method_8320(lv2);
					if (lv4.method_11588()) {
						class_2680 lv5 = arg2.method_8320(lv3);
						if (lv5.method_11620() == class_3614.field_15920
							&& (Integer)lv5.method_11654(class_2404.field_11278) == 0
							&& lv.method_11591(arg2, lv3)
							&& arg2.method_8628(lv, lv3)) {
							arg2.method_8501(lv3, lv);
							arg2.method_8397().method_8676(lv3.method_10062(), class_2246.field_10110, class_3532.method_15395(arg.method_6051(), 60, 120));
						}
					}
				}
			}
		}
	}

	@Override
	public boolean method_8180(class_1887 arg) {
		return super.method_8180(arg) && arg != class_1893.field_9128;
	}
}
