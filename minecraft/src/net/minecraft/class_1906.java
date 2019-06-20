package net.minecraft;

import java.util.Random;
import java.util.Map.Entry;

public class class_1906 extends class_1887 {
	public class_1906(class_1887.class_1888 arg, class_1304... args) {
		super(arg, class_1886.field_9071, args);
	}

	@Override
	public int method_8182(int i) {
		return 10 + 20 * (i - 1);
	}

	@Override
	public int method_20742(int i) {
		return super.method_8182(i) + 50;
	}

	@Override
	public int method_8183() {
		return 3;
	}

	@Override
	public boolean method_8192(class_1799 arg) {
		return arg.method_7909() instanceof class_1738 ? true : super.method_8192(arg);
	}

	@Override
	public void method_8178(class_1309 arg, class_1297 arg2, int i) {
		Random random = arg.method_6051();
		Entry<class_1304, class_1799> entry = class_1890.method_8204(class_1893.field_9097, arg);
		if (method_8243(i, random)) {
			if (arg2 != null) {
				arg2.method_5643(class_1282.method_5513(arg), (float)method_8242(i, random));
			}

			if (entry != null) {
				((class_1799)entry.getValue()).method_7956(3, arg, argx -> argx.method_20235((class_1304)entry.getKey()));
			}
		} else if (entry != null) {
			((class_1799)entry.getValue()).method_7956(1, arg, argx -> argx.method_20235((class_1304)entry.getKey()));
		}
	}

	public static boolean method_8243(int i, Random random) {
		return i <= 0 ? false : random.nextFloat() < 0.15F * (float)i;
	}

	public static int method_8242(int i, Random random) {
		return i > 10 ? i - 10 : 1 + random.nextInt(4);
	}
}
