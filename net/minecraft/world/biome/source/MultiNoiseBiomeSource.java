/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;

public class MultiNoiseBiomeSource
extends BiomeSource {
    private final DoublePerlinNoiseSampler temperatureNoise;
    private final DoublePerlinNoiseSampler humidityNoise;
    private final DoublePerlinNoiseSampler altitudeNoise;
    private final DoublePerlinNoiseSampler weirdnessNoise;
    private final List<Pair<Biome.MixedNoisePoint, Biome>> biomePoints;
    private final boolean threeDimensionalSampling;

    public static MultiNoiseBiomeSource fromBiomes(long seed, List<Biome> biomes) {
        return new MultiNoiseBiomeSource(seed, biomes.stream().flatMap(biome -> biome.streamNoises().map(point -> Pair.of(point, biome))).collect(ImmutableList.toImmutableList()));
    }

    public MultiNoiseBiomeSource(long seed, List<Pair<Biome.MixedNoisePoint, Biome>> biomePoints) {
        super(biomePoints.stream().map(Pair::getSecond).collect(Collectors.toSet()));
        IntStream intStream = IntStream.rangeClosed(-7, -6);
        IntStream intStream2 = IntStream.rangeClosed(-7, -6);
        IntStream intStream3 = IntStream.rangeClosed(-7, -6);
        IntStream intStream4 = IntStream.rangeClosed(-7, -6);
        this.temperatureNoise = new DoublePerlinNoiseSampler(new ChunkRandom(seed), intStream);
        this.humidityNoise = new DoublePerlinNoiseSampler(new ChunkRandom(seed + 1L), intStream2);
        this.altitudeNoise = new DoublePerlinNoiseSampler(new ChunkRandom(seed + 2L), intStream3);
        this.weirdnessNoise = new DoublePerlinNoiseSampler(new ChunkRandom(seed + 3L), intStream4);
        this.biomePoints = biomePoints;
        this.threeDimensionalSampling = false;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public BiomeSource create(long seed) {
        return new MultiNoiseBiomeSource(seed, this.biomePoints);
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        int i = this.threeDimensionalSampling ? biomeY : 0;
        Biome.MixedNoisePoint mixedNoisePoint = new Biome.MixedNoisePoint((float)this.temperatureNoise.sample(biomeX, i, biomeZ), (float)this.humidityNoise.sample(biomeX, i, biomeZ), (float)this.altitudeNoise.sample(biomeX, i, biomeZ), (float)this.weirdnessNoise.sample(biomeX, i, biomeZ), 0.0f);
        return this.biomePoints.stream().min(Comparator.comparing(pair -> Float.valueOf(((Biome.MixedNoisePoint)pair.getFirst()).calculateDistanceTo(mixedNoisePoint)))).map(Pair::getSecond).orElse(Biomes.THE_VOID);
    }
}

