package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface PosRuleTestType<P extends PosRuleTest> {
	PosRuleTestType<AlwaysTruePosRuleTest> ALWAYS_TRUE = register("always_true", AlwaysTruePosRuleTest.CODEC);
	PosRuleTestType<LinearPosRuleTest> LINEAR_POS = register("linear_pos", LinearPosRuleTest.CODEC);
	PosRuleTestType<AxisAlignedLinearPosRuleTest> AXIS_ALIGNED_LINEAR_POS = register("axis_aligned_linear_pos", AxisAlignedLinearPosRuleTest.CODEC);

	Codec<P> codec();

	static <P extends PosRuleTest> PosRuleTestType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registries.POS_RULE_TEST, id, () -> codec);
	}
}
