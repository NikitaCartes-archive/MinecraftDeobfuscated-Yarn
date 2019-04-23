/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Objects;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public final class GlobalPos
implements DynamicSerializable {
    private final DimensionType dimension;
    private final BlockPos pos;

    private GlobalPos(DimensionType dimensionType, BlockPos blockPos) {
        this.dimension = dimensionType;
        this.pos = blockPos;
    }

    public static GlobalPos create(DimensionType dimensionType, BlockPos blockPos) {
        return new GlobalPos(dimensionType, blockPos);
    }

    public static GlobalPos deserialize(Dynamic<?> dynamic) {
        return (GlobalPos)dynamic.get("dimension").map(DimensionType::method_19298).flatMap(dimensionType -> dynamic.get("pos").map(BlockPos::deserialize).map(blockPos -> new GlobalPos((DimensionType)dimensionType, (BlockPos)blockPos))).orElseThrow(() -> new IllegalArgumentException("Could not parse GlobalPos"));
    }

    public DimensionType getDimension() {
        return this.dimension;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        GlobalPos globalPos = (GlobalPos)object;
        return Objects.equals(this.dimension, globalPos.dimension) && Objects.equals(this.pos, globalPos.pos);
    }

    public int hashCode() {
        return Objects.hash(this.dimension, this.pos);
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        return dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("dimension"), this.dimension.serialize(dynamicOps), dynamicOps.createString("pos"), this.pos.serialize(dynamicOps)));
    }

    public String toString() {
        return this.dimension.toString() + " " + this.pos;
    }
}

