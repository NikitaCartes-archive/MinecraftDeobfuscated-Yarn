/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryLookupCodec;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.WorldGenRandom;

public class MultiNoiseBiomeSource
extends BiomeSource {
    private static final NoiseParameters DEFAULT_NOISE_PARAMETERS = new NoiseParameters(-7, ImmutableList.of(Double.valueOf(1.0), Double.valueOf(1.0)));
    /**
     * Used to parse a custom biome source, when a preset hasn't been provided.
     */
    public static final MapCodec<MultiNoiseBiomeSource> CUSTOM_CODEC = RecordCodecBuilder.mapCodec(instance2 -> instance2.group(((MapCodec)Codec.LONG.fieldOf("seed")).forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.seed), ((MapCodec)RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Biome.MixedNoisePoint.CODEC.fieldOf("parameters")).forGetter(Pair::getFirst), ((MapCodec)Biome.REGISTRY_CODEC.fieldOf("biome")).forGetter(Pair::getSecond)).apply((Applicative<Pair, ?>)instance, Pair::of)).listOf().fieldOf("biomes")).forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.biomePoints), ((MapCodec)NoiseParameters.CODEC.fieldOf("temperature_noise")).forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.temperatureNoiseParameters), ((MapCodec)NoiseParameters.CODEC.fieldOf("humidity_noise")).forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.humidityNoiseParameters), ((MapCodec)NoiseParameters.CODEC.fieldOf("altitude_noise")).forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.altitudeNoiseParameters), ((MapCodec)NoiseParameters.CODEC.fieldOf("weirdness_noise")).forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.weirdnessNoiseParameters)).apply((Applicative<MultiNoiseBiomeSource, ?>)instance2, MultiNoiseBiomeSource::new));
    public static final Codec<MultiNoiseBiomeSource> CODEC = Codec.mapEither(Instance.CODEC, CUSTOM_CODEC).xmap(either -> either.map(Instance::getBiomeSource, Function.identity()), multiNoiseBiomeSource -> multiNoiseBiomeSource.getInstance().map(Either::left).orElseGet(() -> Either.right(multiNoiseBiomeSource))).codec();
    private final NoiseParameters temperatureNoiseParameters;
    private final NoiseParameters humidityNoiseParameters;
    private final NoiseParameters altitudeNoiseParameters;
    private final NoiseParameters weirdnessNoiseParameters;
    private final DoublePerlinNoiseSampler temperatureNoise;
    private final DoublePerlinNoiseSampler humidityNoise;
    private final DoublePerlinNoiseSampler altitudeNoise;
    private final DoublePerlinNoiseSampler weirdnessNoise;
    private final List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> biomePoints;
    private final boolean threeDimensionalSampling;
    private final long seed;
    private final Optional<Pair<Registry<Biome>, Preset>> instance;

    public MultiNoiseBiomeSource(long seed, List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> biomePoints) {
        this(seed, biomePoints, Optional.empty());
    }

    MultiNoiseBiomeSource(long seed, List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> biomePoints, Optional<Pair<Registry<Biome>, Preset>> instance) {
        this(seed, biomePoints, DEFAULT_NOISE_PARAMETERS, DEFAULT_NOISE_PARAMETERS, DEFAULT_NOISE_PARAMETERS, DEFAULT_NOISE_PARAMETERS, instance);
    }

    private MultiNoiseBiomeSource(long seed, List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> biomePoints, NoiseParameters temperatureNoiseParameters, NoiseParameters humidityNoiseParameters, NoiseParameters altitudeNoiseParameters, NoiseParameters weirdnessNoiseParameters) {
        this(seed, biomePoints, temperatureNoiseParameters, humidityNoiseParameters, altitudeNoiseParameters, weirdnessNoiseParameters, Optional.empty());
    }

    private MultiNoiseBiomeSource(long seed, List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> biomePoints, NoiseParameters temperatureNoiseParameters, NoiseParameters humidityNoiseParameters, NoiseParameters altitudeNoiseParameters, NoiseParameters weirdnessNoiseParameters, Optional<Pair<Registry<Biome>, Preset>> instance) {
        super(biomePoints.stream().map(Pair::getSecond));
        this.seed = seed;
        this.instance = instance;
        this.temperatureNoiseParameters = temperatureNoiseParameters;
        this.humidityNoiseParameters = humidityNoiseParameters;
        this.altitudeNoiseParameters = altitudeNoiseParameters;
        this.weirdnessNoiseParameters = weirdnessNoiseParameters;
        this.temperatureNoise = DoublePerlinNoiseSampler.create((WorldGenRandom)new ChunkRandom(seed), temperatureNoiseParameters.getFirstOctave(), temperatureNoiseParameters.getAmplitudes());
        this.humidityNoise = DoublePerlinNoiseSampler.create((WorldGenRandom)new ChunkRandom(seed + 1L), humidityNoiseParameters.getFirstOctave(), humidityNoiseParameters.getAmplitudes());
        this.altitudeNoise = DoublePerlinNoiseSampler.create((WorldGenRandom)new ChunkRandom(seed + 2L), altitudeNoiseParameters.getFirstOctave(), altitudeNoiseParameters.getAmplitudes());
        this.weirdnessNoise = DoublePerlinNoiseSampler.create((WorldGenRandom)new ChunkRandom(seed + 3L), weirdnessNoiseParameters.getFirstOctave(), weirdnessNoiseParameters.getAmplitudes());
        this.biomePoints = biomePoints;
        this.threeDimensionalSampling = false;
    }

    public static MultiNoiseBiomeSource method_35242(Registry<Biome> registry, long l) {
        ImmutableList<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> immutableList = MultiNoiseBiomeSource.method_35241(registry);
        NoiseParameters noiseParameters = new NoiseParameters(-9, 1.0, 0.0, 3.0, 3.0, 3.0, 3.0);
        NoiseParameters noiseParameters2 = new NoiseParameters(-7, 1.0, 2.0, 4.0, 4.0);
        NoiseParameters noiseParameters3 = new NoiseParameters(-9, 1.0, 0.0, 0.0, 1.0, 1.0, 0.0);
        NoiseParameters noiseParameters4 = new NoiseParameters(-8, 1.2, 0.6, 0.0, 0.0, 1.0, 0.0);
        return new MultiNoiseBiomeSource(l, immutableList, noiseParameters, noiseParameters2, noiseParameters3, noiseParameters4, Optional.empty());
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    public BiomeSource withSeed(long seed) {
        return new MultiNoiseBiomeSource(seed, this.biomePoints, this.temperatureNoiseParameters, this.humidityNoiseParameters, this.altitudeNoiseParameters, this.weirdnessNoiseParameters, this.instance);
    }

    private Optional<Instance> getInstance() {
        return this.instance.map(pair -> new Instance((Preset)pair.getSecond(), (Registry)pair.getFirst(), this.seed));
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        int i = this.threeDimensionalSampling ? biomeY : 0;
        Biome.MixedNoisePoint mixedNoisePoint = new Biome.MixedNoisePoint((float)this.temperatureNoise.sample(biomeX, i, biomeZ), (float)this.humidityNoise.sample(biomeX, i, biomeZ), (float)this.altitudeNoise.sample(biomeX, i, biomeZ), (float)this.weirdnessNoise.sample(biomeX, i, biomeZ), 0.0f);
        return this.biomePoints.stream().min(Comparator.comparing(pair -> Float.valueOf(((Biome.MixedNoisePoint)pair.getFirst()).calculateDistanceTo(mixedNoisePoint)))).map(Pair::getSecond).map(Supplier::get).orElse(BuiltinBiomes.THE_VOID);
    }

    public static ImmutableList<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> method_35241(Registry<Biome> registry) {
        return ImmutableList.of(Pair.of(new Biome.MixedNoisePoint(0.0f, 0.0f, 0.0f, 0.0f, 0.0f), () -> registry.getOrThrow(BiomeKeys.PLAINS)));
    }

    public boolean matchesInstance(long seed) {
        return this.seed == seed && this.instance.isPresent() && Objects.equals(this.instance.get().getSecond(), Preset.NETHER);
    }

    static class NoiseParameters {
        private final int firstOctave;
        private final DoubleList amplitudes;
        public static final Codec<NoiseParameters> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("firstOctave")).forGetter(NoiseParameters::getFirstOctave), ((MapCodec)Codec.DOUBLE.listOf().fieldOf("amplitudes")).forGetter(NoiseParameters::getAmplitudes)).apply((Applicative<NoiseParameters, ?>)instance, NoiseParameters::new));

        public NoiseParameters(int firstOctave, List<Double> amplitudes) {
            this.firstOctave = firstOctave;
            this.amplitudes = new DoubleArrayList(amplitudes);
        }

        public NoiseParameters(int firstOctave, double ... amplitudes) {
            this.firstOctave = firstOctave;
            this.amplitudes = new DoubleArrayList(amplitudes);
        }

        public int getFirstOctave() {
            return this.firstOctave;
        }

        public DoubleList getAmplitudes() {
            return this.amplitudes;
        }
    }

    public static class Preset {
        static final Map<Identifier, Preset> BY_IDENTIFIER = Maps.newHashMap();
        public static final Preset NETHER = new Preset(new Identifier("nether"), (preset, biomeRegistry, seed) -> new MultiNoiseBiomeSource((long)seed, (List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>>)ImmutableList.of(Pair.of(new Biome.MixedNoisePoint(0.0f, 0.0f, 0.0f, 0.0f, 0.0f), () -> biomeRegistry.getOrThrow(BiomeKeys.NETHER_WASTES)), Pair.of(new Biome.MixedNoisePoint(0.0f, -0.5f, 0.0f, 0.0f, 0.0f), () -> biomeRegistry.getOrThrow(BiomeKeys.SOUL_SAND_VALLEY)), Pair.of(new Biome.MixedNoisePoint(0.4f, 0.0f, 0.0f, 0.0f, 0.0f), () -> biomeRegistry.getOrThrow(BiomeKeys.CRIMSON_FOREST)), Pair.of(new Biome.MixedNoisePoint(0.0f, 0.5f, 0.0f, 0.0f, 0.375f), () -> biomeRegistry.getOrThrow(BiomeKeys.WARPED_FOREST)), Pair.of(new Biome.MixedNoisePoint(-0.5f, 0.0f, 0.0f, 0.0f, 0.175f), () -> biomeRegistry.getOrThrow(BiomeKeys.BASALT_DELTAS))), Optional.of(Pair.of(biomeRegistry, preset))));
        final Identifier id;
        private final Function3<Preset, Registry<Biome>, Long, MultiNoiseBiomeSource> biomeSourceFunction;

        public Preset(Identifier id, Function3<Preset, Registry<Biome>, Long, MultiNoiseBiomeSource> biomeSourceFunction) {
            this.id = id;
            this.biomeSourceFunction = biomeSourceFunction;
            BY_IDENTIFIER.put(id, this);
        }

        public MultiNoiseBiomeSource getBiomeSource(Registry<Biome> biomeRegistry, long seed) {
            return this.biomeSourceFunction.apply(this, biomeRegistry, seed);
        }
    }

    static final class Instance {
        public static final MapCodec<Instance> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Identifier.CODEC.flatXmap(id -> Optional.ofNullable(Preset.BY_IDENTIFIER.get(id)).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown preset: " + id)), preset -> DataResult.success(preset.id)).fieldOf("preset")).stable().forGetter(Instance::getPreset), RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter(Instance::getBiomeRegistry), ((MapCodec)Codec.LONG.fieldOf("seed")).stable().forGetter(Instance::getSeed)).apply((Applicative<Instance, ?>)instance, instance.stable(Instance::new)));
        private final Preset preset;
        private final Registry<Biome> biomeRegistry;
        private final long seed;

        Instance(Preset preset, Registry<Biome> biomeRegistry, long seed) {
            this.preset = preset;
            this.biomeRegistry = biomeRegistry;
            this.seed = seed;
        }

        public Preset getPreset() {
            return this.preset;
        }

        public Registry<Biome> getBiomeRegistry() {
            return this.biomeRegistry;
        }

        public long getSeed() {
            return this.seed;
        }

        public MultiNoiseBiomeSource getBiomeSource() {
            return this.preset.getBiomeSource(this.biomeRegistry, this.seed);
        }
    }
}

