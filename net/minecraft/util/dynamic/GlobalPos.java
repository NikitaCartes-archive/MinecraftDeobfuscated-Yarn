/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.dynamic;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;

public final class GlobalPos {
    public static final Codec<GlobalPos> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)DimensionType.field_24751.fieldOf("dimension")).forGetter(GlobalPos::getDimension), ((MapCodec)BlockPos.field_25064.fieldOf("pos")).forGetter(GlobalPos::getPos)).apply((Applicative<GlobalPos, ?>)instance, GlobalPos::create));
    private final RegistryKey<DimensionType> dimension;
    private final BlockPos pos;

    private GlobalPos(RegistryKey<DimensionType> registryKey, BlockPos pos) {
        this.dimension = registryKey;
        this.pos = pos;
    }

    public static GlobalPos create(RegistryKey<DimensionType> registryKey, BlockPos pos) {
        return new GlobalPos(registryKey, pos);
    }

    public RegistryKey<DimensionType> getDimension() {
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

    public String toString() {
        return this.dimension.toString() + " " + this.pos;
    }
}

