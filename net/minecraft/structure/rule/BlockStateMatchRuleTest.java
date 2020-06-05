/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.RuleTestType;

public class BlockStateMatchRuleTest
extends RuleTest {
    public static final Codec<BlockStateMatchRuleTest> field_25001 = ((MapCodec)BlockState.CODEC.fieldOf("block_state")).xmap(BlockStateMatchRuleTest::new, blockStateMatchRuleTest -> blockStateMatchRuleTest.blockState).codec();
    private final BlockState blockState;

    public BlockStateMatchRuleTest(BlockState blockState) {
        this.blockState = blockState;
    }

    @Override
    public boolean test(BlockState state, Random random) {
        return state == this.blockState;
    }

    @Override
    protected RuleTestType<?> getType() {
        return RuleTestType.BLOCKSTATE_MATCH;
    }
}

