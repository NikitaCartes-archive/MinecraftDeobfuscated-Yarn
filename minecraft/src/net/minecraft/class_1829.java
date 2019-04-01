package net.minecraft;

import com.google.common.collect.Multimap;

public class class_1829 extends class_1831 {
	private final float field_8920;
	private final float field_8919;

	public class_1829(class_1832 arg, int i, float f, class_1792.class_1793 arg2) {
		super(arg, arg2);
		this.field_8919 = f;
		this.field_8920 = (float)i + arg.method_8028();
	}

	public float method_8020() {
		return this.field_8920;
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
		arg.method_7956(1, arg3);
		return true;
	}

	@Override
	public boolean method_7879(class_1799 arg, class_1937 arg2, class_2680 arg3, class_2338 arg4, class_1309 arg5) {
		if (arg3.method_11579(arg2, arg4) != 0.0F) {
			arg.method_7956(2, arg5);
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
			multimap.put(class_1612.field_7363.method_6167(), new class_1322(field_8006, "Weapon modifier", (double)this.field_8920, class_1322.class_1323.field_6328));
			multimap.put(class_1612.field_7356.method_6167(), new class_1322(field_8001, "Weapon modifier", (double)this.field_8919, class_1322.class_1323.field_6328));
		}

		return multimap;
	}
}
