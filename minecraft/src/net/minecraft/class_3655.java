package net.minecraft;

public enum class_3655 implements class_3663 {
	field_16184;

	private static final int field_16180 = class_2378.field_11153.method_10249(class_1972.field_9434);
	private static final int field_16178 = class_2378.field_11153.method_10249(class_1972.field_9478);
	private static final int field_16177 = class_2378.field_11153.method_10249(class_1972.field_9424);
	private static final int field_16176 = class_2378.field_11153.method_10249(class_1972.field_9472);
	private static final int field_16175 = class_2378.field_11153.method_10249(class_1972.field_9460);
	private static final int field_16174 = class_2378.field_11153.method_10249(class_1972.field_9409);
	private static final int field_16173 = class_2378.field_11153.method_10249(class_1972.field_9417);
	private static final int field_16172 = class_2378.field_11153.method_10249(class_1972.field_9474);
	private static final int field_16195 = class_2378.field_11153.method_10249(class_1972.field_9432);
	private static final int field_16194 = class_2378.field_11153.method_10249(class_1972.field_9415);
	private static final int field_16193 = class_2378.field_11153.method_10249(class_1972.field_9410);
	private static final int field_16192 = class_2378.field_11153.method_10249(class_1972.field_9433);
	private static final int field_16191 = class_2378.field_11153.method_10249(class_1972.field_9443);
	private static final int field_16190 = class_2378.field_11153.method_10249(class_1972.field_9413);
	private static final int field_16189 = class_2378.field_11153.method_10249(class_1972.field_9406);
	private static final int field_16188 = class_2378.field_11153.method_10249(class_1972.field_9462);
	private static final int field_16187 = class_2378.field_11153.method_10249(class_1972.field_9407);
	private static final int field_16186 = class_2378.field_11153.method_10249(class_1972.field_9438);
	private static final int field_16185 = class_2378.field_11153.method_10249(class_1972.field_9464);
	private static final int field_16183 = class_2378.field_11153.method_10249(class_1972.field_9419);
	private static final int field_16182 = class_2378.field_11153.method_10249(class_1972.field_9471);
	private static final int field_16181 = class_2378.field_11153.method_10249(class_1972.field_9420);

	@Override
	public int method_15868(class_3630 arg, int i, int j, int k, int l, int m) {
		class_1959 lv = class_2378.field_11153.method_10200(m);
		if (m == field_16188) {
			if (class_3645.method_15846(i) || class_3645.method_15846(j) || class_3645.method_15846(k) || class_3645.method_15846(l)) {
				return field_16187;
			}
		} else if (lv != null && lv.method_8688() == class_1959.class_1961.field_9358) {
			if (!method_15851(i) || !method_15851(j) || !method_15851(k) || !method_15851(l)) {
				return field_16172;
			}

			if (class_3645.method_15845(i) || class_3645.method_15845(j) || class_3645.method_15845(k) || class_3645.method_15845(l)) {
				return field_16180;
			}
		} else if (m != field_16176 && m != field_16175 && m != field_16185) {
			if (lv != null && lv.method_8694() == class_1959.class_1963.field_9383) {
				if (!class_3645.method_15845(m) && (class_3645.method_15845(i) || class_3645.method_15845(j) || class_3645.method_15845(k) || class_3645.method_15845(l))) {
					return field_16178;
				}
			} else if (m != field_16194 && m != field_16193) {
				if (!class_3645.method_15845(m)
					&& m != field_16186
					&& m != field_16182
					&& (class_3645.method_15845(i) || class_3645.method_15845(j) || class_3645.method_15845(k) || class_3645.method_15845(l))) {
					return field_16180;
				}
			} else if (!class_3645.method_15845(i)
				&& !class_3645.method_15845(j)
				&& !class_3645.method_15845(k)
				&& !class_3645.method_15845(l)
				&& (!this.method_15852(i) || !this.method_15852(j) || !this.method_15852(k) || !this.method_15852(l))) {
				return field_16177;
			}
		} else if (!class_3645.method_15845(m)
			&& (class_3645.method_15845(i) || class_3645.method_15845(j) || class_3645.method_15845(k) || class_3645.method_15845(l))) {
			return field_16183;
		}

		return m;
	}

	private static boolean method_15851(int i) {
		return class_2378.field_11153.method_10200(i) != null && class_2378.field_11153.method_10200(i).method_8688() == class_1959.class_1961.field_9358
			? true
			: i == field_16172 || i == field_16173 || i == field_16195 || i == field_16174 || i == field_16181 || class_3645.method_15845(i);
	}

	private boolean method_15852(int i) {
		return i == field_16194 || i == field_16193 || i == field_16192 || i == field_16191 || i == field_16190 || i == field_16189;
	}
}
