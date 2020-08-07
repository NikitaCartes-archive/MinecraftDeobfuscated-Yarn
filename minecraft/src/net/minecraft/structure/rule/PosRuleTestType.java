package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public interface PosRuleTestType<P extends PosRuleTest> {
	PosRuleTestType<AlwaysTruePosRuleTest> field_23344 = register("always_true", AlwaysTruePosRuleTest.CODEC);
	PosRuleTestType<LinearPosRuleTest> field_23345 = register("linear_pos", LinearPosRuleTest.CODEC);
	PosRuleTestType<AxisAlignedLinearPosRuleTest> field_23346 = register("axis_aligned_linear_pos", AxisAlignedLinearPosRuleTest.CODEC);

	Codec<P> codec();

	static <P extends PosRuleTest> PosRuleTestType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registry.POS_RULE_TEST, id, () -> codec);
	}
}
