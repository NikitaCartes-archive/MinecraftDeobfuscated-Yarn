package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;

public class class_4125 extends class_4097<class_1308> {
	private final float field_18386;
	private final float field_18387;

	public class_4125(float f, float g) {
		this.field_18386 = f;
		this.field_18387 = g;
	}

	protected boolean method_19010(class_3218 arg, class_1308 arg2) {
		return arg2.method_5799() && arg2.method_5861() > (double)this.field_18386 || arg2.method_5771();
	}

	protected boolean method_19011(class_3218 arg, class_1308 arg2, long l) {
		return this.method_19010(arg, arg2);
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of();
	}

	protected void method_19012(class_3218 arg, class_1308 arg2, long l) {
		if (arg2.method_6051().nextFloat() < this.field_18387) {
			arg2.method_5993().method_6233();
		}
	}
}
