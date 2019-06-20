package net.minecraft;

import com.google.common.collect.ImmutableMap;

public class class_4295 extends class_4097<class_1646> {
	public class_4295() {
		super(ImmutableMap.of(class_4140.field_18439, class_4141.field_18457));
	}

	protected boolean method_20449(class_3218 arg, class_1646 arg2) {
		class_3850 lv = arg2.method_7231();
		return lv.method_16924() != class_3852.field_17051 && lv.method_16924() != class_3852.field_17062 && arg2.method_19269() == 0 && lv.method_16925() <= 1;
	}

	protected void method_20450(class_3218 arg, class_1646 arg2, long l) {
		arg2.method_7221(arg2.method_7231().method_16921(class_3852.field_17051));
		arg2.method_19179(arg);
	}
}
