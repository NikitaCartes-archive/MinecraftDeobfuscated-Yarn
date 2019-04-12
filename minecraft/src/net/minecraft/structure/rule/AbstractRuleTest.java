package net.minecraft.structure.rule;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;

public abstract class AbstractRuleTest {
	public abstract boolean test(BlockState blockState, Random random);

	public <T> Dynamic<T> method_16767(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.mergeInto(
				this.serialize(dynamicOps).getValue(),
				dynamicOps.createString("predicate_type"),
				dynamicOps.createString(Registry.RULE_TEST.getId(this.getRuleTest()).toString())
			)
		);
	}

	protected abstract RuleTest getRuleTest();

	protected abstract <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps);
}
