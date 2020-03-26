package net.minecraft.structure.rule;

import net.minecraft.util.dynamic.DynamicDeserializer;
import net.minecraft.util.registry.Registry;

public interface PosRuleTestType extends DynamicDeserializer<PosRuleTest> {
	PosRuleTestType ALWAYS_TRUE = register("always_true", dynamic -> AlwaysTruePosRuleTest.INSTANCE);
	PosRuleTestType LINEAR_POS = register("linear_pos", LinearPosRuleTest::new);
	PosRuleTestType AXIS_ALIGNED_LINEAR_POS = register("axis_aligned_linear_pos", AxisAlignedLinearPosRuleTest::new);

	static PosRuleTestType register(String id, PosRuleTestType object) {
		return Registry.register(Registry.POS_RULE_TEST, id, object);
	}
}
