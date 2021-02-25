package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;

/**
 * Rule tests are used in structure generation to check if a block state matches some condition.
 */
public abstract class RuleTest {
	public static final Codec<RuleTest> TYPE_CODEC = Registry.RULE_TEST.dispatch("predicate_type", RuleTest::getType, RuleTestType::codec);

	public abstract boolean test(BlockState state, Random random);

	protected abstract RuleTestType<?> getType();
}
