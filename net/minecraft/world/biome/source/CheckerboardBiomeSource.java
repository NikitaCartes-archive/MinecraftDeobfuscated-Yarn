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
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

public class CheckerboardBiomeSource
extends BiomeSource {
    public static final Codec<CheckerboardBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Biome.field_26750.fieldOf("biomes")).forGetter(checkerboardBiomeSource -> checkerboardBiomeSource.biomeArray), ((MapCodec)Codec.intRange(0, 62).fieldOf("scale")).orElse(2).forGetter(checkerboardBiomeSource -> checkerboardBiomeSource.scale)).apply((Applicative<CheckerboardBiomeSource, ?>)instance, CheckerboardBiomeSource::new));
    private final List<Supplier<Biome>> biomeArray;
    private final int gridSize;
    private final int scale;

    public CheckerboardBiomeSource(List<Supplier<Biome>> biomeArray, int size) {
        super(biomeArray.stream());
        this.biomeArray = biomeArray;
        this.gridSize = size + 2;
        this.scale = size;
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    public BiomeSource withSeed(long seed) {
        return this;
    }

    @Override
    public Biome method_38109(int i, int j, int k, MultiNoiseUtil.MultiNoiseSampler multiNoiseSampler) {
        return this.biomeArray.get(Math.floorMod((i >> this.gridSize) + (k >> this.gridSize), this.biomeArray.size())).get();
    }
}

