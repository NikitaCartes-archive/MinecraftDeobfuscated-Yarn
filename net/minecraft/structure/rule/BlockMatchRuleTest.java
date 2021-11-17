/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.util.registry.Registry;

public class BlockMatchRuleTest
extends RuleTest {
    public static final Codec<BlockMatchRuleTest> CODEC = ((MapCodec)Registry.BLOCK.getCodec().fieldOf("block")).xmap(BlockMatchRuleTest::new, blockMatchRuleTest -> blockMatchRuleTest.block).codec();
    private final Block block;

    public BlockMatchRuleTest(Block block) {
        this.block = block;
    }

    @Override
    public boolean test(BlockState state, Random random) {
        return state.isOf(this.block);
    }

    @Override
    protected RuleTestType<?> getType() {
        return RuleTestType.BLOCK_MATCH;
    }
}

