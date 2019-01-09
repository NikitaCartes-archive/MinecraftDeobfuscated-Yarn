package net.minecraft;

public enum class_3639 implements class_3664 {
	field_16059;

	@Override
	public int method_15869(class_3630 arg, int i) {
		if (class_3645.method_15846(i)) {
			return i;
		} else {
			int j = arg.method_15834(6);
			if (j == 0) {
				return 4;
			} else {
				return j == 1 ? 3 : 1;
			}
		}
	}
}
