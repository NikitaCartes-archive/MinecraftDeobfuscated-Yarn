package net.minecraft.structure.rule;

import net.minecraft.util.DynamicDeserializer;
import net.minecraft.util.registry.Registry;

public interface RuleTest extends DynamicDeserializer<AbstractRuleTest> {
	RuleTest ALWAYS_TRUE = register("always_true", dynamic -> AlwaysTrueRuleTest.INSTANCE);
	RuleTest BLOCK_MATCH = register("block_match", BlockMatchRuleTest::new);
	RuleTest BLOCKSTATE_MATCH = register("blockstate_match", BlockStateMatchRuleTest::new);
	RuleTest TAG_MATCH = register("tag_match", TagMatchRuleTest::new);
	RuleTest RANDOM_BLOCK_MATCH = register("random_block_match", RandomBlockMatchRuleTest::new);
	RuleTest RANDOM_BLOCKSTATE_MATCH = register("random_blockstate_match", RandomBlockStateMatchRuleTest::new);

	static RuleTest register(String string, RuleTest ruleTest) {
		return Registry.register(Registry.RULE_TEST, string, ruleTest);
	}
}
