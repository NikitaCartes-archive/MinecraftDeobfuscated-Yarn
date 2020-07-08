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
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;

public class TagMatchRuleTest
extends RuleTest {
    public static final Codec<TagMatchRuleTest> field_25014 = ((MapCodec)Tag.codec(() -> ServerTagManagerHolder.getTagManager().getBlocks()).fieldOf("tag")).xmap(TagMatchRuleTest::new, tagMatchRuleTest -> tagMatchRuleTest.tag).codec();
    private final Tag<Block> tag;

    public TagMatchRuleTest(Tag<Block> tag) {
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

