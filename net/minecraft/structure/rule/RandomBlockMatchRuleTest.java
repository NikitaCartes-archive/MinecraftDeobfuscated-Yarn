/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.rule;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.Registry;

public class RandomBlockMatchRuleTest
extends RuleTest {
    public static final Codec<RandomBlockMatchRuleTest> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Registry.BLOCK.getCodec().fieldOf("block")).forGetter(randomBlockMatchRuleTest -> randomBlockMatchRuleTest.block), ((MapCodec)Codec.FLOAT.fieldOf("probability")).forGetter(randomBlockMatchRuleTest -> Float.valueOf(randomBlockMatchRuleTest.probability))).apply((Applicative<RandomBlockMatchRuleTest, ?>)instance, RandomBlockMatchRuleTest::new));
    private final Block block;
    private final float probability;

    public RandomBlockMatchRuleTest(Block block, float probability) {
        this.block = block;
        this.probability = probability;
    }

    @Override
    public boolean test(BlockState state, AbstractRandom random) {
        return state.isOf(this.block) && random.nextFloat() < this.probability;
    }

    @Override
    protected RuleTestType<?> getType() {
        return RuleTestType.RANDOM_BLOCK_MATCH;
    }
}

