/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.rule;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.structure.rule.PosRuleTest;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class LinearPosRuleTest
extends PosRuleTest {
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

    public <T> LinearPosRuleTest(Dynamic<T> data) {
        this(data.get("min_chance").asFloat(0.0f), data.get("max_chance").asFloat(0.0f), data.get("min_dist").asInt(0), data.get("max_dist").asInt(0));
    }

    @Override
    public boolean test(BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, Random random) {
        int i = blockPos2.getManhattanDistance(blockPos3);
        float f = random.nextFloat();
        return (double)f <= MathHelper.clampedLerp(this.minChance, this.maxChance, MathHelper.getLerpProgress(i, this.minDistance, this.maxDistance));
    }

    @Override
    protected PosRuleTestType getType() {
        return PosRuleTestType.LINEAR_POS;
    }

    @Override
    protected <T> Dynamic<T> serializeContents(DynamicOps<T> ops) {
        return new Dynamic<T>(ops, ops.createMap(ImmutableMap.of(ops.createString("min_chance"), ops.createFloat(this.minChance), ops.createString("max_chance"), ops.createFloat(this.maxChance), ops.createString("min_dist"), ops.createFloat(this.minDistance), ops.createString("max_dist"), ops.createFloat(this.maxDistance))));
    }
}

