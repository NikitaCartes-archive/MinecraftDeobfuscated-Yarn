package net.minecraft.structure.rule;

import net.minecraft.util.dynamic.DynamicDeserializer;
import net.minecraft.util.registry.Registry;

public interface RuleTestType extends DynamicDeserializer<RuleTest> {
	RuleTestType ALWAYS_TRUE = register("always_true", dynamic -> AlwaysTrueRuleTest.INSTANCE);
	RuleTestType BLOCK_MATCH = register("block_match", BlockMatchRuleTest::new);
	RuleTestType BLOCKSTATE_MATCH = register("blockstate_match", BlockStateMatchRuleTest::new);
	RuleTestType TAG_MATCH = register("tag_match", TagMatchRuleTest::new);
	RuleTestType RANDOM_BLOCK_MATCH = register("random_block_match", RandomBlockMatchRuleTest::new);
	RuleTestType RANDOM_BLOCKSTATE_MATCH = register("random_blockstate_match", RandomBlockStateMatchRuleTest::new);

	static RuleTestType register(String id, RuleTestType test) {
		return Registry.register(Registry.RULE_TEST, id, test);
	}
}
