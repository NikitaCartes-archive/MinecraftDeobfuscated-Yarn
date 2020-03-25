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
import net.minecraft.util.math.MathHelper;

public class class_4993
extends class_4995 {
    private final float field_23339;
    private final float field_23340;
    private final int field_23341;
    private final int field_23342;

    public class_4993(float f, float g, int i, int j) {
        if (i >= j) {
            throw new IllegalArgumentException("Invalid range: [" + i + "," + j + "]");
        }
        this.field_23339 = f;
        this.field_23340 = g;
        this.field_23341 = i;
        this.field_23342 = j;
    }

    public <T> class_4993(Dynamic<T> dynamic) {
        this(dynamic.get("min_chance").asFloat(0.0f), dynamic.get("max_chance").asFloat(0.0f), dynamic.get("min_dist").asInt(0), dynamic.get("max_dist").asInt(0));
    }

    @Override
    public boolean method_26406(BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, Random random) {
        int i = blockPos2.getManhattanDistance(blockPos3);
        float f = random.nextFloat();
        return (double)f <= MathHelper.clampedLerp(this.field_23339, this.field_23340, MathHelper.getLerpProgress(i, this.field_23341, this.field_23342));
    }

    @Override
    protected class_4996 method_26404() {
        return class_4996.field_23345;
    }

    @Override
    protected <T> Dynamic<T> method_26405(DynamicOps<T> dynamicOps) {
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("min_chance"), dynamicOps.createFloat(this.field_23339), dynamicOps.createString("max_chance"), dynamicOps.createFloat(this.field_23340), dynamicOps.createString("min_dist"), dynamicOps.createFloat(this.field_23341), dynamicOps.createString("max_dist"), dynamicOps.createFloat(this.field_23342))));
    }
}

