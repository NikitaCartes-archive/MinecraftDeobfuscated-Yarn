/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import net.minecraft.structure.rule.AlwaysTruePosRuleTest;
import net.minecraft.structure.rule.AxisAlignedLinearPosRuleTest;
import net.minecraft.structure.rule.LinearPosRuleTest;
import net.minecraft.structure.rule.PosRuleTest;
import net.minecraft.util.registry.Registry;

public interface PosRuleTestType<P extends PosRuleTest> {
    public static final PosRuleTestType<AlwaysTruePosRuleTest> ALWAYS_TRUE = PosRuleTestType.register("always_true", AlwaysTruePosRuleTest.field_25006);
    public static final PosRuleTestType<LinearPosRuleTest> LINEAR_POS = PosRuleTestType.register("linear_pos", LinearPosRuleTest.CODEC);
    public static final PosRuleTestType<AxisAlignedLinearPosRuleTest> AXIS_ALIGNED_LINEAR_POS = PosRuleTestType.register("axis_aligned_linear_pos", AxisAlignedLinearPosRuleTest.CODEC);

    public Codec<P> codec();

    public static <P extends PosRuleTest> PosRuleTestType<P> register(String id, Codec<P> codec) {
        return Registry.register(Registry.POS_RULE_TEST, id, () -> codec);
    }
}

