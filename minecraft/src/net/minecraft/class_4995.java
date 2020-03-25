package net.minecraft;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public abstract class class_4995 {
	public abstract boolean method_26406(BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, Random random);

	public <T> Dynamic<T> method_26407(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.mergeInto(
				this.method_26405(dynamicOps).getValue(),
				dynamicOps.createString("predicate_type"),
				dynamicOps.createString(Registry.POS_RULE_TEST.getId(this.method_26404()).toString())
			)
		);
	}

	protected abstract class_4996 method_26404();

	protected abstract <T> Dynamic<T> method_26405(DynamicOps<T> dynamicOps);
}
