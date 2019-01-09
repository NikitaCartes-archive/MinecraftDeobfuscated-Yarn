package net.minecraft;

public enum class_3652 implements class_3659, class_3740 {
	field_16161;

	private static final int field_16165 = class_2378.field_11153.method_10249(class_1972.field_9463);
	private static final int field_16164 = class_2378.field_11153.method_10249(class_1972.field_9452);
	private static final int field_16163 = class_2378.field_11153.method_10249(class_1972.field_9462);
	private static final int field_16162 = class_2378.field_11153.method_10249(class_1972.field_9407);
	private static final int field_16160 = class_2378.field_11153.method_10249(class_1972.field_9438);

	@Override
	public int method_15861(class_3630 arg, class_3625 arg2, class_3625 arg3, int i, int j) {
		int k = arg2.method_15825(this.method_16342(i), this.method_16343(j));
		int l = arg3.method_15825(this.method_16342(i), this.method_16343(j));
		if (class_3645.method_15845(k)) {
			return k;
		} else if (l == field_16160) {
			if (k == field_16164) {
				return field_16165;
			} else {
				return k != field_16163 && k != field_16162 ? l & 0xFF : field_16162;
			}
		} else {
			return k;
		}
	}
}
