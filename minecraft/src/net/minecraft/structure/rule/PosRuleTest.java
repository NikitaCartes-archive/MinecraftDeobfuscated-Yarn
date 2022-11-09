package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public abstract class PosRuleTest {
	public static final Codec<PosRuleTest> BASE_CODEC = Registries.POS_RULE_TEST
		.getCodec()
		.dispatch("predicate_type", PosRuleTest::getType, PosRuleTestType::codec);

	public abstract boolean test(BlockPos originalPos, BlockPos currentPos, BlockPos pivot, Random random);

	protected abstract PosRuleTestType<?> getType();
}
