/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;

public class CheckerboardBiomeSource
extends BiomeSource {
    public static final Codec<CheckerboardBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Biome.field_26750.fieldOf("biomes")).forGetter(checkerboardBiomeSource -> checkerboardBiomeSource.biomeArray), ((MapCodec)Codec.intRange(0, 62).fieldOf("scale")).orElse(2).forGetter(checkerboardBiomeSource -> checkerboardBiomeSource.field_24716)).apply((Applicative<CheckerboardBiomeSource, ?>)instance, CheckerboardBiomeSource::new));
    private final List<Supplier<Biome>> biomeArray;
    private final int gridSize;
    private final int field_24716;

    public CheckerboardBiomeSource(List<Supplier<Biome>> list, int size) {
        super(list.stream());
        this.biomeArray = list;
        this.gridSize = size + 2;
        this.field_24716 = size;
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public BiomeSource withSeed(long seed) {
        return this;
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.biomeArray.get(Math.floorMod((biomeX >> this.gridSize) + (biomeZ >> this.gridSize), this.biomeArray.size())).get();
    }
}

