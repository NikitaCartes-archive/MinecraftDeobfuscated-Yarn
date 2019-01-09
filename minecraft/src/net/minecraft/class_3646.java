package net.minecraft;

public enum class_3646 implements class_3664 {
	field_16120;

	private static final int field_16119 = class_2378.field_11153.method_10249(class_1972.field_9417);
	private static final int field_16118 = class_2378.field_11153.method_10249(class_1972.field_9440);

	@Override
	public int method_15869(class_3630 arg, int i) {
		return arg.method_15834(10) == 0 && i == field_16119 ? field_16118 : i;
	}
}
