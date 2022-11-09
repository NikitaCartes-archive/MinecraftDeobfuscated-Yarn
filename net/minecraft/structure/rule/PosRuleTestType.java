/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.structure.rule.AlwaysTruePosRuleTest;
import net.minecraft.structure.rule.AxisAlignedLinearPosRuleTest;
import net.minecraft.structure.rule.LinearPosRuleTest;
import net.minecraft.structure.rule.PosRuleTest;

public interface PosRuleTestType<P extends PosRuleTest> {
    public static final PosRuleTestType<AlwaysTruePosRuleTest> ALWAYS_TRUE = PosRuleTestType.register("always_true", AlwaysTruePosRuleTest.CODEC);
    public static final PosRuleTestType<LinearPosRuleTest> LINEAR_POS = PosRuleTestType.register("linear_pos", LinearPosRuleTest.CODEC);
    public static final PosRuleTestType<AxisAlignedLinearPosRuleTest> AXIS_ALIGNED_LINEAR_POS = PosRuleTestType.register("axis_aligned_linear_pos", AxisAlignedLinearPosRuleTest.CODEC);

    public Codec<P> codec();

    public static <P extends PosRuleTest> PosRuleTestType<P> register(String id, Codec<P> codec) {
        return Registry.register(Registries.POS_RULE_TEST, id, () -> codec);
    }
}

