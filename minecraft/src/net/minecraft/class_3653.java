package net.minecraft;

public enum class_3653 implements class_3663 {
	field_16168;

	public static final int field_16167 = class_2378.field_11153.method_10249(class_1972.field_9438);

	@Override
	public int method_15868(class_3630 arg, int i, int j, int k, int l, int m) {
		int n = method_15850(m);
		return n == method_15850(l) && n == method_15850(i) && n == method_15850(j) && n == method_15850(k) ? -1 : field_16167;
	}

	private static int method_15850(int i) {
		return i >= 2 ? 2 + (i & 1) : i;
	}
}
