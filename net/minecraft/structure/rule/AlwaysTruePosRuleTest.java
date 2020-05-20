/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.structure.rule.PosRuleTest;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.util.math.BlockPos;

public class AlwaysTruePosRuleTest
extends PosRuleTest {
    public static final Codec<AlwaysTruePosRuleTest> field_25006 = Codec.unit(() -> INSTANCE);
    public static final AlwaysTruePosRuleTest INSTANCE = new AlwaysTruePosRuleTest();

    private AlwaysTruePosRuleTest() {
    }

    @Override
    public boolean test(BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, Random random) {
        return true;
    }

    @Override
    protected PosRuleTestType<?> getType() {
        return PosRuleTestType.ALWAYS_TRUE;
    }
}

