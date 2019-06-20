package net.minecraft;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class class_1810 extends class_1766 {
	private static final Set<class_2248> field_8899 = ImmutableSet.of(
		class_2246.field_10546,
		class_2246.field_10418,
		class_2246.field_10445,
		class_2246.field_10025,
		class_2246.field_10201,
		class_2246.field_10442,
		class_2246.field_10425,
		class_2246.field_10205,
		class_2246.field_10571,
		class_2246.field_10295,
		class_2246.field_10085,
		class_2246.field_10212,
		class_2246.field_10441,
		class_2246.field_10090,
		class_2246.field_9989,
		class_2246.field_10515,
		class_2246.field_10225,
		class_2246.field_10384,
		class_2246.field_10167,
		class_2246.field_10080,
		class_2246.field_9979,
		class_2246.field_10292,
		class_2246.field_10361,
		class_2246.field_10117,
		class_2246.field_10518,
		class_2246.field_10344,
		class_2246.field_10340,
		class_2246.field_10474,
		class_2246.field_10289,
		class_2246.field_10508,
		class_2246.field_10346,
		class_2246.field_10115,
		class_2246.field_10093,
		class_2246.field_10454,
		class_2246.field_10136,
		class_2246.field_10007,
		class_2246.field_10298,
		class_2246.field_10351,
		class_2246.field_10191,
		class_2246.field_10131,
		class_2246.field_10390,
		class_2246.field_10237,
		class_2246.field_10624,
		class_2246.field_10175,
		class_2246.field_9978,
		class_2246.field_10483,
		class_2246.field_10467,
		class_2246.field_10360,
		class_2246.field_10494,
		class_2246.field_10158,
		class_2246.field_10329,
		class_2246.field_10283,
		class_2246.field_10024,
		class_2246.field_10412,
		class_2246.field_10405,
		class_2246.field_10064,
		class_2246.field_10262,
		class_2246.field_10601,
		class_2246.field_10189,
		class_2246.field_10016,
		class_2246.field_10478,
		class_2246.field_10322,
		class_2246.field_10507,
		class_2246.field_10603,
		class_2246.field_10371,
		class_2246.field_10605,
		class_2246.field_10373,
		class_2246.field_10532,
		class_2246.field_10140,
		class_2246.field_10055,
		class_2246.field_10203,
		class_2246.field_10320,
		class_2246.field_10275,
		class_2246.field_10063,
		class_2246.field_10407,
		class_2246.field_10051,
		class_2246.field_10268,
		class_2246.field_10068,
		class_2246.field_10199,
		class_2246.field_10600
	);

	protected class_1810(class_1832 arg, class_1792.class_1793 arg2) {
		super(arg, field_8899, arg2);
	}

	@Override
	public boolean method_7856(class_2680 arg) {
		class_2248 lv = arg.method_11614();
		int i = this.method_8022().method_8024();
		if (lv == class_2246.field_10540) {
			return i == 3;
		} else if (lv == class_2246.field_10201
			|| lv == class_2246.field_10442
			|| lv == class_2246.field_10013
			|| lv == class_2246.field_10234
			|| lv == class_2246.field_10205
			|| lv == class_2246.field_10571
			|| lv == class_2246.field_10080) {
			return i >= 2;
		} else if (lv != class_2246.field_10085 && lv != class_2246.field_10212 && lv != class_2246.field_10441 && lv != class_2246.field_10090) {
			class_3614 lv2 = arg.method_11620();
			return lv2 == class_3614.field_15914 || lv2 == class_3614.field_15953 || lv2 == class_3614.field_15949;
		} else {
			return i >= 1;
		}
	}

	@Override
	public float method_7865(class_1799 arg, class_2680 arg2) {
		class_3614 lv = arg2.method_11620();
		return lv != class_3614.field_15953 && lv != class_3614.field_15949 && lv != class_3614.field_15914 ? super.method_7865(arg, arg2) : this.field_7940;
	}

	@Override
	protected class_5117 method_26739() {
		return class_5117.field_23647;
	}
}
