package net.minecraft;

public enum class_3641 implements class_3663 {
	field_16091;

	private static final int field_16092 = class_2378.field_11153.method_10249(class_1972.field_9424);
	private static final int field_16090 = class_2378.field_11153.method_10249(class_1972.field_9472);
	private static final int field_16089 = class_2378.field_11153.method_10249(class_1972.field_9460);
	private static final int field_16088 = class_2378.field_11153.method_10249(class_1972.field_9452);
	private static final int field_16087 = class_2378.field_11153.method_10249(class_1972.field_9417);
	private static final int field_16086 = class_2378.field_11153.method_10249(class_1972.field_9440);
	private static final int field_16085 = class_2378.field_11153.method_10249(class_1972.field_9474);
	private static final int field_16084 = class_2378.field_11153.method_10249(class_1972.field_9415);
	private static final int field_16101 = class_2378.field_11153.method_10249(class_1972.field_9433);
	private static final int field_16100 = class_2378.field_11153.method_10249(class_1972.field_9410);
	private static final int field_16099 = class_2378.field_11153.method_10249(class_1972.field_9451);
	private static final int field_16098 = class_2378.field_11153.method_10249(class_1972.field_9477);
	private static final int field_16097 = class_2378.field_11153.method_10249(class_1972.field_9464);
	private static final int field_16096 = class_2378.field_11153.method_10249(class_1972.field_9471);
	private static final int field_16095 = class_2378.field_11153.method_10249(class_1972.field_9420);
	private static final int field_16094 = class_2378.field_11153.method_10249(class_1972.field_9454);

	@Override
	public int method_15868(class_3630 arg, int i, int j, int k, int l, int m) {
		int[] is = new int[1];
		if (!this.method_15841(is, i, j, k, l, m, field_16090, field_16097)
			&& !this.method_15840(is, i, j, k, l, m, field_16100, field_16084)
			&& !this.method_15840(is, i, j, k, l, m, field_16101, field_16084)
			&& !this.method_15840(is, i, j, k, l, m, field_16098, field_16095)) {
			if (m != field_16092 || i != field_16088 && j != field_16088 && l != field_16088 && k != field_16088) {
				if (m == field_16096) {
					if (i == field_16092
						|| j == field_16092
						|| l == field_16092
						|| k == field_16092
						|| i == field_16094
						|| j == field_16094
						|| l == field_16094
						|| k == field_16094
						|| i == field_16088
						|| j == field_16088
						|| l == field_16088
						|| k == field_16088) {
						return field_16099;
					}

					if (i == field_16087
						|| k == field_16087
						|| j == field_16087
						|| l == field_16087
						|| i == field_16086
						|| k == field_16086
						|| j == field_16086
						|| l == field_16086) {
						return field_16085;
					}
				}

				return m;
			} else {
				return field_16089;
			}
		} else {
			return is[0];
		}
	}

	private boolean method_15841(int[] is, int i, int j, int k, int l, int m, int n, int o) {
		if (!class_3645.method_15844(m, n)) {
			return false;
		} else {
			if (this.method_15839(i, n) && this.method_15839(j, n) && this.method_15839(l, n) && this.method_15839(k, n)) {
				is[0] = m;
			} else {
				is[0] = o;
			}

			return true;
		}
	}

	private boolean method_15840(int[] is, int i, int j, int k, int l, int m, int n, int o) {
		if (m != n) {
			return false;
		} else {
			if (class_3645.method_15844(i, n) && class_3645.method_15844(j, n) && class_3645.method_15844(l, n) && class_3645.method_15844(k, n)) {
				is[0] = m;
			} else {
				is[0] = o;
			}

			return true;
		}
	}

	private boolean method_15839(int i, int j) {
		if (class_3645.method_15844(i, j)) {
			return true;
		} else {
			class_1959 lv = class_2378.field_11153.method_10200(i);
			class_1959 lv2 = class_2378.field_11153.method_10200(j);
			if (lv != null && lv2 != null) {
				class_1959.class_1962 lv3 = lv.method_8704();
				class_1959.class_1962 lv4 = lv2.method_8704();
				return lv3 == lv4 || lv3 == class_1959.class_1962.field_9375 || lv4 == class_1959.class_1962.field_9375;
			} else {
				return false;
			}
		}
	}
}
