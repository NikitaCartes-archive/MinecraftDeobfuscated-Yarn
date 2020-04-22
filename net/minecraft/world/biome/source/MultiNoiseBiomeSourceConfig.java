/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSourceConfig;

public class MultiNoiseBiomeSourceConfig
implements BiomeSourceConfig {
    private final long seed;
    private ImmutableList<Integer> temperatureOctaves = IntStream.rangeClosed(-7, -6).boxed().collect(ImmutableList.toImmutableList());
    private ImmutableList<Integer> humidityOctaves = IntStream.rangeClosed(-7, -6).boxed().collect(ImmutableList.toImmutableList());
    private ImmutableList<Integer> hillinessOctaves = IntStream.rangeClosed(-7, -6).boxed().collect(ImmutableList.toImmutableList());
    private ImmutableList<Integer> styleOctaves = IntStream.rangeClosed(-7, -6).boxed().collect(ImmutableList.toImmutableList());
    private boolean field_24117;
    private List<Pair<Biome.MixedNoisePoint, Biome>> field_24118 = ImmutableList.of();

    public MultiNoiseBiomeSourceConfig(long seed) {
        this.seed = seed;
    }

    public MultiNoiseBiomeSourceConfig withBiomes(List<Biome> list) {
        return this.method_27350(list.stream().flatMap(biome -> biome.method_27342().map(mixedNoisePoint -> Pair.of(mixedNoisePoint, biome))).collect(ImmutableList.toImmutableList()));
    }

    public MultiNoiseBiomeSourceConfig method_27350(List<Pair<Biome.MixedNoisePoint, Biome>> list) {
        this.field_24118 = list;
        return this;
    }

    public List<Pair<Biome.MixedNoisePoint, Biome>> method_27347() {
        return this.field_24118;
    }

    public long getSeed() {
        return this.seed;
    }

    public ImmutableList<Integer> getTemperatureOctaves() {
        return this.temperatureOctaves;
    }

    public ImmutableList<Integer> getHumidityOctaves() {
        return this.humidityOctaves;
    }

    public ImmutableList<Integer> getHillinessOctaves() {
        return this.hillinessOctaves;
    }

    public ImmutableList<Integer> getStyleOctaves() {
        return this.styleOctaves;
    }

    public boolean method_27351() {
        return this.field_24117;
    }
}

