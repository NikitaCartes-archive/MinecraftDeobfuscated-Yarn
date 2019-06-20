package net.minecraft;

import com.google.common.collect.ImmutableMap;

public class class_4110 extends class_4097<class_1308> {
	public class_4110(int i, int j) {
		super(ImmutableMap.of(class_4140.field_18446, class_4141.field_18456), i, j);
	}

	protected boolean method_18967(class_3218 arg, class_1308 arg2, long l) {
		return arg2.method_18868().method_18904(class_4140.field_18446).filter(arg2x -> arg2x.method_18990(arg2)).isPresent();
	}

	protected void method_18968(class_3218 arg, class_1308 arg2, long l) {
		arg2.method_18868().method_18875(class_4140.field_18446);
	}

	protected void method_18969(class_3218 arg, class_1308 arg2, long l) {
		arg2.method_18868().method_18904(class_4140.field_18446).ifPresent(arg2x -> arg2.method_5988().method_19615(arg2x.method_18991()));
	}
}
