/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.class_4995;
import net.minecraft.class_4996;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class class_4992
extends class_4995 {
    private final float field_23334;
    private final float field_23335;
    private final int field_23336;
    private final int field_23337;
    private final Direction.Axis field_23338;

    public class_4992(float f, float g, int i, int j, Direction.Axis axis) {
        if (i >= j) {
            throw new IllegalArgumentException("Invalid range: [" + i + "," + j + "]");
        }
        this.field_23334 = f;
        this.field_23335 = g;
        this.field_23336 = i;
        this.field_23337 = j;
        this.field_23338 = axis;
    }

    public <T> class_4992(Dynamic<T> dynamic) {
        this(dynamic.get("min_chance").asFloat(0.0f), dynamic.get("max_chance").asFloat(0.0f), dynamic.get("min_dist").asInt(0), dynamic.get("max_dist").asInt(0), Direction.Axis.fromName(dynamic.get("axis").asString("y")));
    }

    @Override
    public boolean method_26406(BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, Random random) {
        Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, this.field_23338);
        float f = Math.abs((blockPos2.getX() - blockPos3.getX()) * direction.getOffsetX());
        float g = Math.abs((blockPos2.getY() - blockPos3.getY()) * direction.getOffsetY());
        float h = Math.abs((blockPos2.getZ() - blockPos3.getZ()) * direction.getOffsetZ());
        int i = (int)(f + g + h);
        float j = random.nextFloat();
        return (double)j <= MathHelper.clampedLerp(this.field_23334, this.field_23335, MathHelper.getLerpProgress(i, this.field_23336, this.field_23337));
    }

    @Override
    protected class_4996 method_26404() {
        return class_4996.field_23346;
    }

    @Override
    protected <T> Dynamic<T> method_26405(DynamicOps<T> dynamicOps) {
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("min_chance"), dynamicOps.createFloat(this.field_23334), dynamicOps.createString("max_chance"), dynamicOps.createFloat(this.field_23335), dynamicOps.createString("min_dist"), dynamicOps.createFloat(this.field_23336), dynamicOps.createString("max_dist"), dynamicOps.createFloat(this.field_23337), dynamicOps.createString("axis"), dynamicOps.createString(this.field_23338.getName()))));
    }
}

