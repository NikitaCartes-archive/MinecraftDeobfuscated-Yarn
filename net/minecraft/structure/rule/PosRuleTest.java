/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;

public abstract class PosRuleTest {
    public static final Codec<PosRuleTest> BASE_CODEC = Registry.POS_RULE_TEST.getCodec().dispatch("predicate_type", PosRuleTest::getType, PosRuleTestType::codec);

    public abstract boolean test(BlockPos var1, BlockPos var2, BlockPos var3, Random var4);

    protected abstract PosRuleTestType<?> getType();
}

