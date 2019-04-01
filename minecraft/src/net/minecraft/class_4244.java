package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;

public class class_4244 extends class_4248 {
	public class_4244(float f) {
		super(f);
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18445, class_4141.field_18457));
	}

	@Override
	protected boolean method_18919(class_3218 arg, class_1309 arg2) {
		class_3765 lv = arg.method_19502(new class_2338(arg2));
		return lv != null && lv.method_20023() && super.method_18919(arg, arg2);
	}
}
