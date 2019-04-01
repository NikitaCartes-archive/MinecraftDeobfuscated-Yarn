package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.Set;

public class class_4117 extends class_4097<class_1314> {
	private final float field_18375;

	public class_4117(float f) {
		this.field_18375 = f;
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18445, class_4141.field_18457));
	}

	protected void method_18996(class_3218 arg, class_1314 arg2, long l) {
		Optional<class_243> optional = Optional.ofNullable(class_1414.method_6378(arg2, 10, 7));
		arg2.method_18868().method_18879(class_4140.field_18445, optional.map(argx -> new class_4142(argx, this.field_18375, 0)));
	}
}
