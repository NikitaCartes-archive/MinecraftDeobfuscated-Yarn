package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;

public class class_4249 extends class_4097<class_1309> {
	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_19009, class_4141.field_18456));
	}

	@Override
	protected void method_18920(class_3218 arg, class_1309 arg2, long l) {
		class_4095<?> lv = arg2.method_18868();
		class_3765 lv2 = arg.method_19502(new class_2338(arg2));
		if (lv2 == null) {
			lv.method_18880(class_4168.field_19043);
		}
	}
}
