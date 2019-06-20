package net.minecraft;

import com.google.common.collect.ImmutableMap;

public class class_4100 extends class_4097<class_1646> {
	public class_4100() {
		super(ImmutableMap.of());
	}

	protected void method_20645(class_3218 arg, class_1646 arg2, long l) {
		boolean bl = class_4113.method_19575(arg2) || class_4113.method_19574(arg2) || method_19557(arg2);
		if (!bl) {
			arg2.method_18868().method_18875(class_4140.field_18451);
			arg2.method_18868().method_18875(class_4140.field_18452);
			arg2.method_18868().method_18871(arg.method_8532(), arg.method_8510());
		}
	}

	private static boolean method_19557(class_1646 arg) {
		return arg.method_18868().method_18904(class_4140.field_18452).filter(arg2 -> arg2.method_5858(arg) <= 36.0).isPresent();
	}
}
