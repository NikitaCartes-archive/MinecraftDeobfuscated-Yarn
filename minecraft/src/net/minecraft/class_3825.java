package net.minecraft;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;

abstract class class_3825 {
	public abstract boolean method_16768(class_2680 arg, Random random);

	public <T> Dynamic<T> method_16767(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.mergeInto(
				this.method_16769(dynamicOps).getValue(),
				dynamicOps.createString("predicate_type"),
				dynamicOps.createString(class_2378.field_16792.method_10221(this.method_16766()).toString())
			)
		);
	}

	protected abstract class_3827 method_16766();

	protected abstract <T> Dynamic<T> method_16769(DynamicOps<T> dynamicOps);
}
