package net.minecraft;

public enum class_3654 implements class_3663 {
	field_16171;

	@Override
	public int method_15868(class_3630 arg, int i, int j, int k, int l, int m) {
		boolean bl = j == l;
		boolean bl2 = i == k;
		if (bl == bl2) {
			if (bl) {
				return arg.method_15834(2) == 0 ? l : i;
			} else {
				return m;
			}
		} else {
			return bl ? l : i;
		}
	}
}
