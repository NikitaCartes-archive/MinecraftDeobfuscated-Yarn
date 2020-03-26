/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.dynamic;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Objects;
import net.minecraft.util.dynamic.DynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public final class GlobalPos
implements DynamicSerializable {
    private final DimensionType dimension;
    private final BlockPos pos;

    private GlobalPos(DimensionType dimension, BlockPos pos) {
        this.dimension = dimension;
        this.pos = pos;
    }

    public static GlobalPos create(DimensionType dimension, BlockPos pos) {
        return new GlobalPos(dimension, pos);
    }

    public static GlobalPos deserialize(Dynamic<?> dynamic) {
        return (GlobalPos)dynamic.get("dimension").map(DimensionType::deserialize).flatMap(dimensionType -> dynamic.get("pos").map(BlockPos::deserialize).map(blockPos -> new GlobalPos((DimensionType)dimensionType, (BlockPos)blockPos))).orElseThrow(() -> new IllegalArgumentException("Could not parse GlobalPos"));
    }

    public DimensionType getDimension() {
        return this.dimension;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        GlobalPos globalPos = (GlobalPos)o;
        return Objects.equals(this.dimension, globalPos.dimension) && Objects.equals(this.pos, globalPos.pos);
    }

    public int hashCode() {
        return Objects.hash(this.dimension, this.pos);
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        return ops.createMap(ImmutableMap.of(ops.createString("dimension"), this.dimension.serialize(ops), ops.createString("pos"), this.pos.serialize(ops)));
    }

    public String toString() {
        return this.dimension.toString() + " " + this.pos;
    }
}

