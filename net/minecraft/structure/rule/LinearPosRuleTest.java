/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.rule;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.structure.rule.PosRuleTest;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class LinearPosRuleTest
extends PosRuleTest {
    public static final Codec<LinearPosRuleTest> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.FLOAT.fieldOf("min_chance")).orElse(Float.valueOf(0.0f)).forGetter(linearPosRuleTest -> Float.valueOf(linearPosRuleTest.minChance)), ((MapCodec)Codec.FLOAT.fieldOf("max_chance")).orElse(Float.valueOf(0.0f)).forGetter(linearPosRuleTest -> Float.valueOf(linearPosRuleTest.maxChance)), ((MapCodec)Codec.INT.fieldOf("min_dist")).orElse(0).forGetter(linearPosRuleTest -> linearPosRuleTest.minDistance), ((MapCodec)Codec.INT.fieldOf("max_dist")).orElse(0).forGetter(linearPosRuleTest -> linearPosRuleTest.maxDistance)).apply((Applicative<LinearPosRuleTest, ?>)instance, LinearPosRuleTest::new));
    private final float minChance;
    private final float maxChance;
    private final int minDistance;
    private final int maxDistance;

    public LinearPosRuleTest(float minChance, float maxChance, int minDistance, int maxDistance) {
        if (minDistance >= maxDistance) {
            throw new IllegalArgumentException("Invalid range: [" + minDistance + "," + maxDistance + "]");
        }
        this.minChance = minChance;
        this.maxChance = maxChance;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    @Override
    public boolean test(BlockPos blockPos, BlockPos blockPos2, BlockPos pivot, Random random) {
        int i = blockPos2.getManhattanDistance(pivot);
        float f = random.nextFloat();
        return (double)f <= MathHelper.clampedLerp(this.minChance, this.maxChance, MathHelper.getLerpProgress(i, this.minDistance, this.maxDistance));
    }

    @Override
    protected PosRuleTestType<?> getType() {
        return PosRuleTestType.LINEAR_POS;
    }
}

