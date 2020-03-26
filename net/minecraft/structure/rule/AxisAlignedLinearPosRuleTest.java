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
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class AxisAlignedLinearPosRuleTest
extends PosRuleTest {
    private final float minChance;
    private final float maxChance;
    private final int minDistance;
    private final int maxDistance;
    private final Direction.Axis axis;

    public AxisAlignedLinearPosRuleTest(float minChance, float maxChance, int minDistance, int maxDistance, Direction.Axis axis) {
        if (minDistance >= maxDistance) {
            throw new IllegalArgumentException("Invalid range: [" + minDistance + "," + maxDistance + "]");
        }
        this.minChance = minChance;
        this.maxChance = maxChance;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.axis = axis;
    }

    public <T> AxisAlignedLinearPosRuleTest(Dynamic<T> data) {
        this(data.get("min_chance").asFloat(0.0f), data.get("max_chance").asFloat(0.0f), data.get("min_dist").asInt(0), data.get("max_dist").asInt(0), Direction.Axis.fromName(data.get("axis").asString("y")));
    }

    @Override
    public boolean test(BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, Random random) {
        Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, this.axis);
        float f = Math.abs((blockPos2.getX() - blockPos3.getX()) * direction.getOffsetX());
        float g = Math.abs((blockPos2.getY() - blockPos3.getY()) * direction.getOffsetY());
        float h = Math.abs((blockPos2.getZ() - blockPos3.getZ()) * direction.getOffsetZ());
        int i = (int)(f + g + h);
        float j = random.nextFloat();
        return (double)j <= MathHelper.clampedLerp(this.minChance, this.maxChance, MathHelper.getLerpProgress(i, this.minDistance, this.maxDistance));
    }

    @Override
    protected PosRuleTestType getType() {
        return PosRuleTestType.AXIS_ALIGNED_LINEAR_POS;
    }

    @Override
    protected <T> Dynamic<T> serializeContents(DynamicOps<T> ops) {
        return new Dynamic<T>(ops, ops.createMap(ImmutableMap.of(ops.createString("min_chance"), ops.createFloat(this.minChance), ops.createString("max_chance"), ops.createFloat(this.maxChance), ops.createString("min_dist"), ops.createFloat(this.minDistance), ops.createString("max_dist"), ops.createFloat(this.maxDistance), ops.createString("axis"), ops.createString(this.axis.getName()))));
    }
}

