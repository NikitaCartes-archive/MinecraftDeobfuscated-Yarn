package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public interface RuleTestType<P extends RuleTest> {
	RuleTestType<AlwaysTrueRuleTest> ALWAYS_TRUE = register("always_true", AlwaysTrueRuleTest.field_24994);
	RuleTestType<BlockMatchRuleTest> BLOCK_MATCH = register("block_match", BlockMatchRuleTest.field_24999);
	RuleTestType<BlockStateMatchRuleTest> BLOCKSTATE_MATCH = register("blockstate_match", BlockStateMatchRuleTest.field_25001);
	RuleTestType<TagMatchRuleTest> TAG_MATCH = register("tag_match", TagMatchRuleTest.field_25014);
	RuleTestType<RandomBlockMatchRuleTest> RANDOM_BLOCK_MATCH = register("random_block_match", RandomBlockMatchRuleTest.CODEC);
	RuleTestType<RandomBlockStateMatchRuleTest> RANDOM_BLOCKSTATE_MATCH = register("random_blockstate_match", RandomBlockStateMatchRuleTest.CODEC);

	Codec<P> codec();

	static <P extends RuleTest> RuleTestType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registry.RULE_TEST, id, () -> codec);
	}
}
