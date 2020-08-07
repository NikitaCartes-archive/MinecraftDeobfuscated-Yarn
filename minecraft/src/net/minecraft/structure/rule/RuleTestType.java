package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public interface RuleTestType<P extends RuleTest> {
	RuleTestType<AlwaysTrueRuleTest> field_16982 = register("always_true", AlwaysTrueRuleTest.CODEC);
	RuleTestType<BlockMatchRuleTest> field_16981 = register("block_match", BlockMatchRuleTest.CODEC);
	RuleTestType<BlockStateMatchRuleTest> field_16985 = register("blockstate_match", BlockStateMatchRuleTest.CODEC);
	RuleTestType<TagMatchRuleTest> field_16983 = register("tag_match", TagMatchRuleTest.CODEC);
	RuleTestType<RandomBlockMatchRuleTest> field_16980 = register("random_block_match", RandomBlockMatchRuleTest.CODEC);
	RuleTestType<RandomBlockStateMatchRuleTest> field_16984 = register("random_blockstate_match", RandomBlockStateMatchRuleTest.CODEC);

	Codec<P> codec();

	static <P extends RuleTest> RuleTestType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registry.RULE_TEST, id, () -> codec);
	}
}
