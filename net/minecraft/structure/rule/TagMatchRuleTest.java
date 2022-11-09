/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.util.math.random.Random;

public class TagMatchRuleTest
extends RuleTest {
    public static final Codec<TagMatchRuleTest> CODEC = ((MapCodec)TagKey.unprefixedCodec(RegistryKeys.BLOCK).fieldOf("tag")).xmap(TagMatchRuleTest::new, ruleTest -> ruleTest.tag).codec();
    private final TagKey<Block> tag;

    public TagMatchRuleTest(TagKey<Block> tag) {
        this.tag = tag;
    }

    @Override
    public boolean test(BlockState state, Random random) {
        return state.isIn(this.tag);
    }

    @Override
    protected RuleTestType<?> getType() {
        return RuleTestType.TAG_MATCH;
    }
}

