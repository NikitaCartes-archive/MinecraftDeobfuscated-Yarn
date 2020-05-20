/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.rule;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.RuleTestType;

public class RandomBlockStateMatchRuleTest
extends RuleTest {
    public static final Codec<RandomBlockStateMatchRuleTest> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockState.field_24734.fieldOf("block_state")).forGetter(randomBlockStateMatchRuleTest -> randomBlockStateMatchRuleTest.blockState), ((MapCodec)Codec.FLOAT.fieldOf("probability")).forGetter(randomBlockStateMatchRuleTest -> Float.valueOf(randomBlockStateMatchRuleTest.probability))).apply((Applicative<RandomBlockStateMatchRuleTest, ?>)instance, RandomBlockStateMatchRuleTest::new));
    private final BlockState blockState;
    private final float probability;

    public RandomBlockStateMatchRuleTest(BlockState blockState, float f) {
        this.blockState = blockState;
        this.probability = f;
    }

    @Override
    public boolean test(BlockState state, Random random) {
        return state == this.blockState && random.nextFloat() < this.probability;
    }

    @Override
    protected RuleTestType<?> getType() {
        return RuleTestType.RANDOM_BLOCKSTATE_MATCH;
    }
}

