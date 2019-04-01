package net.minecraft;

import java.util.List;

public class class_1262 {
	public static class_1799 method_5430(List<class_1799> list, int i, int j) {
		return i >= 0 && i < list.size() && !((class_1799)list.get(i)).method_7960() && j > 0 ? ((class_1799)list.get(i)).method_7971(j) : class_1799.field_8037;
	}

	public static class_1799 method_5428(List<class_1799> list, int i) {
		return i >= 0 && i < list.size() ? (class_1799)list.set(i, class_1799.field_8037) : class_1799.field_8037;
	}

	public static class_2487 method_5426(class_2487 arg, class_2371<class_1799> arg2) {
		return method_5427(arg, arg2, true);
	}

	public static class_2487 method_5427(class_2487 arg, class_2371<class_1799> arg2, boolean bl) {
		class_2499 lv = new class_2499();

		for (int i = 0; i < arg2.size(); i++) {
			class_1799 lv2 = arg2.get(i);
			if (!lv2.method_7960()) {
				class_2487 lv3 = new class_2487();
				lv3.method_10567("Slot", (byte)i);
				lv2.method_7953(lv3);
				lv.add(lv3);
			}
		}

		if (!lv.isEmpty() || bl) {
			arg.method_10566("Items", lv);
		}

		return arg;
	}

	public static void method_5429(class_2487 arg, class_2371<class_1799> arg2) {
		class_2499 lv = arg.method_10554("Items", 10);

		for (int i = 0; i < lv.size(); i++) {
			class_2487 lv2 = lv.method_10602(i);
			int j = lv2.method_10571("Slot") & 255;
			if (j >= 0 && j < arg2.size()) {
				arg2.set(j, class_1799.method_7915(lv2));
			}
		}
	}
}
