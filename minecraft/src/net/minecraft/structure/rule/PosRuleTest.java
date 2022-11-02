package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;

public abstract class PosRuleTest {
	public static final Codec<PosRuleTest> BASE_CODEC = Registry.POS_RULE_TEST.getCodec().dispatch("predicate_type", PosRuleTest::getType, PosRuleTestType::codec);

	public abstract boolean test(BlockPos originalPos, BlockPos currentPos, BlockPos pivot, Random random);

	protected abstract PosRuleTestType<?> getType();
}
