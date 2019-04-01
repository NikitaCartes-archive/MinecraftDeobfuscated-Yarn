package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Set;

public class class_4119 extends class_4097<class_1309> {
	private final class_1299<?> field_18376;
	private final float field_18377;

	public class_4119(class_1299<?> arg, float f) {
		this.field_18376 = arg;
		this.field_18377 = f * f;
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18446, class_4141.field_18457), Pair.of(class_4140.field_18442, class_4141.field_18456));
	}

	@Override
	protected boolean method_18919(class_3218 arg, class_1309 arg2) {
		return ((List)arg2.method_18868().method_19543(class_4140.field_18442).get()).stream().anyMatch(argx -> this.field_18376.equals(argx.method_5864()));
	}

	@Override
	protected void method_18920(class_3218 arg, class_1309 arg2, long l) {
		class_4095<?> lv = arg2.method_18868();
		lv.method_19543(class_4140.field_18442)
			.ifPresent(
				list -> list.stream()
						.filter(argxx -> this.field_18376.equals(argxx.method_5864()))
						.filter(arg2xx -> arg2xx.method_5858(arg2) <= (double)this.field_18377)
						.findFirst()
						.ifPresent(arg2xx -> lv.method_18878(class_4140.field_18446, new class_4102(arg2xx)))
			);
	}
}
