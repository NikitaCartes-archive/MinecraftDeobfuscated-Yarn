package net.minecraft;

import com.google.common.collect.Multimap;

public class class_1829 extends class_1831 {
	public class_1829(class_1832 arg, class_1792.class_1793 arg2) {
		super(arg, arg2);
	}

	public float method_8020() {
		return class_5117.field_23645.method_26740(this.method_8022());
	}

	@Override
	public boolean method_7885(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4) {
		return !arg4.method_7337();
	}

	@Override
	public float method_7865(class_1799 arg, class_2680 arg2) {
		class_2248 lv = arg2.method_11614();
		if (lv == class_2246.field_10343) {
			return 15.0F;
		} else {
			class_3614 lv2 = arg2.method_11620();
			return lv2 != class_3614.field_15935
					&& lv2 != class_3614.field_15956
					&& lv2 != class_3614.field_15921
					&& !arg2.method_11602(class_3481.field_15503)
					&& lv2 != class_3614.field_15954
				? 1.0F
				: 1.5F;
		}
	}

	@Override
	public boolean method_7873(class_1799 arg, class_1309 arg2, class_1309 arg3) {
		arg.method_7956(1, arg3, argx -> argx.method_20235(class_1304.field_6173));
		return true;
	}

	@Override
	public boolean method_7879(class_1799 arg, class_1937 arg2, class_2680 arg3, class_2338 arg4, class_1309 arg5) {
		if (arg3.method_11579(arg2, arg4) != 0.0F) {
			arg.method_7956(2, arg5, argx -> argx.method_20235(class_1304.field_6173));
		}

		return true;
	}

	@Override
	public boolean method_7856(class_2680 arg) {
		return arg.method_11614() == class_2246.field_10343;
	}

	@Override
	public Multimap<String, class_1322> method_7844(class_1304 arg) {
		Multimap<String, class_1322> multimap = super.method_7844(arg);
		if (arg == class_1304.field_6173) {
			class_5117.field_23645.method_26741(this.method_8022(), multimap);
		}

		return multimap;
	}
}
