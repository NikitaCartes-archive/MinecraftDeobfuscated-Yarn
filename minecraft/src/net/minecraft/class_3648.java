package net.minecraft;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum class_3648 implements class_3659, class_3739 {
	field_16134;

	private static final Logger field_16128 = LogManager.getLogger();
	private static final int field_16126 = class_2378.field_11153.method_10249(class_1972.field_9412);
	private static final int field_16142 = class_2378.field_11153.method_10249(class_1972.field_9421);
	private static final int field_16125 = class_2378.field_11153.method_10249(class_1972.field_9424);
	private static final int field_16140 = class_2378.field_11153.method_10249(class_1972.field_9466);
	private static final int field_16124 = class_2378.field_11153.method_10249(class_1972.field_9472);
	private static final int field_16139 = class_2378.field_11153.method_10249(class_1972.field_9460);
	private static final int field_16123 = class_2378.field_11153.method_10249(class_1972.field_9409);
	private static final int field_16138 = class_2378.field_11153.method_10249(class_1972.field_9459);
	private static final int field_16151 = class_2378.field_11153.method_10249(class_1972.field_9452);
	private static final int field_16137 = class_2378.field_11153.method_10249(class_1972.field_9444);
	private static final int field_16150 = class_2378.field_11153.method_10249(class_1972.field_9417);
	private static final int field_16136 = class_2378.field_11153.method_10249(class_1972.field_9432);
	private static final int field_16149 = class_2378.field_11153.method_10249(class_1972.field_9440);
	private static final int field_16135 = class_2378.field_11153.method_10249(class_1972.field_9468);
	private static final int field_16148 = class_2378.field_11153.method_10249(class_1972.field_9415);
	private static final int field_16133 = class_2378.field_11153.method_10249(class_1972.field_9410);
	private static final int field_16147 = class_2378.field_11153.method_10249(class_1972.field_9451);
	private static final int field_16132 = class_2378.field_11153.method_10249(class_1972.field_9477);
	private static final int field_16146 = class_2378.field_11153.method_10249(class_1972.field_9429);
	private static final int field_16131 = class_2378.field_11153.method_10249(class_1972.field_9475);
	private static final int field_16145 = class_2378.field_11153.method_10249(class_1972.field_9449);
	private static final int field_16130 = class_2378.field_11153.method_10249(class_1972.field_9430);
	private static final int field_16144 = class_2378.field_11153.method_10249(class_1972.field_9420);
	private static final int field_16129 = class_2378.field_11153.method_10249(class_1972.field_9454);
	private static final int field_16143 = class_2378.field_11153.method_10249(class_1972.field_9425);
	private static final int field_16127 = class_2378.field_11153.method_10249(class_1972.field_9428);

	@Override
	public int method_15861(class_3630 arg, class_3625 arg2, class_3625 arg3, int i, int j) {
		int k = arg2.method_15825(this.method_16342(i + 1), this.method_16343(j + 1));
		int l = arg3.method_15825(this.method_16342(i + 1), this.method_16343(j + 1));
		if (k > 255) {
			field_16128.debug("old! {}", k);
		}

		int m = (l - 2) % 29;
		if (!class_3645.method_15846(k) && l >= 2 && m == 1) {
			class_1959 lv = class_2378.field_11153.method_10200(k);
			if (lv == null || !lv.method_8723()) {
				class_1959 lv2 = class_1959.method_8716(lv);
				return lv2 == null ? k : class_2378.field_11153.method_10249(lv2);
			}
		}

		if (arg.method_15834(3) == 0 || m == 0) {
			int n = k;
			if (k == field_16125) {
				n = field_16140;
			} else if (k == field_16123) {
				n = field_16138;
			} else if (k == field_16126) {
				n = field_16142;
			} else if (k == field_16131) {
				n = field_16147;
			} else if (k == field_16144) {
				n = field_16127;
			} else if (k == field_16132) {
				n = field_16146;
			} else if (k == field_16129) {
				n = field_16143;
			} else if (k == field_16147) {
				n = arg.method_15834(3) == 0 ? field_16138 : field_16123;
			} else if (k == field_16151) {
				n = field_16137;
			} else if (k == field_16150) {
				n = field_16136;
			} else if (k == field_16149) {
				n = field_16135;
			} else if (k == class_3645.field_16113) {
				n = class_3645.field_16108;
			} else if (k == class_3645.field_16114) {
				n = class_3645.field_16109;
			} else if (k == class_3645.field_16112) {
				n = class_3645.field_16107;
			} else if (k == class_3645.field_16111) {
				n = class_3645.field_16116;
			} else if (k == field_16124) {
				n = field_16139;
			} else if (k == field_16145) {
				n = field_16130;
			} else if (class_3645.method_15844(k, field_16133)) {
				n = field_16148;
			} else if ((k == class_3645.field_16108 || k == class_3645.field_16109 || k == class_3645.field_16107 || k == class_3645.field_16116)
				&& arg.method_15834(3) == 0) {
				n = arg.method_15834(2) == 0 ? field_16147 : field_16123;
			}

			if (m == 0 && n != k) {
				class_1959 lv2 = class_1959.method_8716(class_2378.field_11153.method_10200(n));
				n = lv2 == null ? k : class_2378.field_11153.method_10249(lv2);
			}

			if (n != k) {
				int o = 0;
				if (class_3645.method_15844(arg2.method_15825(this.method_16342(i + 1), this.method_16343(j + 0)), k)) {
					o++;
				}

				if (class_3645.method_15844(arg2.method_15825(this.method_16342(i + 2), this.method_16343(j + 1)), k)) {
					o++;
				}

				if (class_3645.method_15844(arg2.method_15825(this.method_16342(i + 0), this.method_16343(j + 1)), k)) {
					o++;
				}

				if (class_3645.method_15844(arg2.method_15825(this.method_16342(i + 1), this.method_16343(j + 2)), k)) {
					o++;
				}

				if (o >= 3) {
					return n;
				}
			}
		}

		return k;
	}
}
