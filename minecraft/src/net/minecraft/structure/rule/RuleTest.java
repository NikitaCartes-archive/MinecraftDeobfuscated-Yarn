package net.minecraft.structure.rule;

import net.minecraft.util.DynamicDeserializer;
import net.minecraft.util.registry.Registry;

public interface RuleTest extends DynamicDeserializer<AbstractRuleTest> {
	RuleTest field_16982 = register("always_true", dynamic -> AlwaysTrueRuleTest.INSTANCE);
	RuleTest field_16981 = register("block_match", BlockMatchRuleTest::new);
	RuleTest field_16985 = register("blockstate_match", BlockStateMatchRuleTest::new);
	RuleTest field_16983 = register("tag_match", TagMatchRuleTest::new);
	RuleTest field_16980 = register("random_block_match", RandomBlockMatchRuleTest::new);
	RuleTest field_16984 = register("random_blockstate_match", RandomBlockStateMatchRuleTest::new);

	static RuleTest register(String string, RuleTest ruleTest) {
		return Registry.register(Registry.RULE_TEST, string, ruleTest);
	}
}
