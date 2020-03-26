/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.rule;

import net.minecraft.structure.rule.AlwaysTruePosRuleTest;
import net.minecraft.structure.rule.AxisAlignedLinearPosRuleTest;
import net.minecraft.structure.rule.LinearPosRuleTest;
import net.minecraft.structure.rule.PosRuleTest;
import net.minecraft.util.dynamic.DynamicDeserializer;
import net.minecraft.util.registry.Registry;

public interface PosRuleTestType
extends DynamicDeserializer<PosRuleTest> {
    public static final PosRuleTestType ALWAYS_TRUE = PosRuleTestType.register("always_true", dynamic -> AlwaysTruePosRuleTest.INSTANCE);
    public static final PosRuleTestType LINEAR_POS = PosRuleTestType.register("linear_pos", LinearPosRuleTest::new);
    public static final PosRuleTestType AXIS_ALIGNED_LINEAR_POS = PosRuleTestType.register("axis_aligned_linear_pos", AxisAlignedLinearPosRuleTest::new);

    public static PosRuleTestType register(String id, PosRuleTestType object) {
        return Registry.register(Registry.POS_RULE_TEST, id, object);
    }
}

