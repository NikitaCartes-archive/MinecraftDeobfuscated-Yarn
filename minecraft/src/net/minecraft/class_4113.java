package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;

public class class_4113 extends class_4097<class_1309> {
	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of();
	}

	@Override
	protected void method_18920(class_3218 arg, class_1309 arg2, long l) {
		if (method_19575(arg2) || method_19574(arg2)) {
			class_4095<?> lv = arg2.method_18868();
			if (!lv.method_18906(class_4168.field_18599)) {
				lv.method_18875(class_4140.field_18449);
				lv.method_18875(class_4140.field_18445);
				lv.method_18875(class_4140.field_18446);
				lv.method_18875(class_4140.field_18448);
				lv.method_18875(class_4140.field_18447);
			}

			lv.method_18880(class_4168.field_18599);
		}
	}

	public static boolean method_19574(class_1309 arg) {
		return arg.method_18868().method_18896(class_4140.field_18453);
	}

	public static boolean method_19575(class_1309 arg) {
		return arg.method_18868().method_18896(class_4140.field_18451);
	}
}
