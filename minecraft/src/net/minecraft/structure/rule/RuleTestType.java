package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface RuleTestType<P extends RuleTest> {
	RuleTestType<AlwaysTrueRuleTest> ALWAYS_TRUE = register("always_true", AlwaysTrueRuleTest.CODEC);
	RuleTestType<BlockMatchRuleTest> BLOCK_MATCH = register("block_match", BlockMatchRuleTest.CODEC);
	RuleTestType<BlockStateMatchRuleTest> BLOCKSTATE_MATCH = register("blockstate_match", BlockStateMatchRuleTest.CODEC);
	RuleTestType<TagMatchRuleTest> TAG_MATCH = register("tag_match", TagMatchRuleTest.CODEC);
	RuleTestType<RandomBlockMatchRuleTest> RANDOM_BLOCK_MATCH = register("random_block_match", RandomBlockMatchRuleTest.CODEC);
	RuleTestType<RandomBlockStateMatchRuleTest> RANDOM_BLOCKSTATE_MATCH = register("random_blockstate_match", RandomBlockStateMatchRuleTest.CODEC);

	Codec<P> codec();

	static <P extends RuleTest> RuleTestType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registries.RULE_TEST, id, () -> codec);
	}
}
