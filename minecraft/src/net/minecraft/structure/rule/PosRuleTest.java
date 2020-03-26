package net.minecraft.structure.rule;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public abstract class PosRuleTest {
	public abstract boolean test(BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, Random random);

	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.mergeInto(
				this.serializeContents(ops).getValue(), ops.createString("predicate_type"), ops.createString(Registry.POS_RULE_TEST.getId(this.getType()).toString())
			)
		);
	}

	protected abstract PosRuleTestType getType();

	protected abstract <T> Dynamic<T> serializeContents(DynamicOps<T> ops);
}
