package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;

public class class_4120 extends class_4097<class_1309> {
	private final float field_18378;
	private final int field_19002;

	public class_4120(float f, int i) {
		this.field_18378 = f;
		this.field_19002 = i;
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18445, class_4141.field_18457), Pair.of(class_4140.field_18446, class_4141.field_18456));
	}

	@Override
	protected void method_18920(class_3218 arg, class_1309 arg2, long l) {
		class_4095<?> lv = arg2.method_18868();
		class_4115 lv2 = (class_4115)lv.method_19543(class_4140.field_18446).get();
		lv.method_18878(class_4140.field_18445, new class_4142(lv2, this.field_18378, this.field_19002));
	}
}
