/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
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
import java.util.function.LongFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;

public class MultiNoiseBiomeSource
extends BiomeSource {
    private static final class_5487 field_26433 = new class_5487(-7, ImmutableList.of(Double.valueOf(1.0), Double.valueOf(1.0)));
    public static final MapCodec<MultiNoiseBiomeSource> field_24718 = RecordCodecBuilder.mapCodec(instance2 -> instance2.group(((MapCodec)Codec.LONG.fieldOf("seed")).forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.seed), ((MapCodec)RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Biome.MixedNoisePoint.CODEC.fieldOf("parameters")).forGetter(Pair::getFirst), ((MapCodec)Biome.REGISTRY_CODEC.fieldOf("biome")).forGetter(Pair::getSecond)).apply((Applicative<Pair, ?>)instance, Pair::of)).listOf().fieldOf("biomes")).forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.biomePoints), ((MapCodec)class_5487.field_26438.fieldOf("temperature_noise")).forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.field_26434), ((MapCodec)class_5487.field_26438.fieldOf("humidity_noise")).forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.field_26435), ((MapCodec)class_5487.field_26438.fieldOf("altitude_noise")).forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.field_26436), ((MapCodec)class_5487.field_26438.fieldOf("weirdness_noise")).forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.field_26437)).apply((Applicative<MultiNoiseBiomeSource, ?>)instance2, MultiNoiseBiomeSource::new));
    public static final Codec<MultiNoiseBiomeSource> CODEC = Codec.mapEither(Preset.CODEC, field_24718).xmap(either -> either.map(pair -> ((Preset)pair.getFirst()).getBiomeSource((Long)pair.getSecond()), Function.identity()), multiNoiseBiomeSource -> multiNoiseBiomeSource.field_24721.map(preset -> Either.left(Pair.of(preset, multiNoiseBiomeSource.seed))).orElseGet(() -> Either.right(multiNoiseBiomeSource))).codec();
    private final class_5487 field_26434;
    private final class_5487 field_26435;
    private final class_5487 field_26436;
    private final class_5487 field_26437;
    private final DoublePerlinNoiseSampler temperatureNoise;
    private final DoublePerlinNoiseSampler humidityNoise;
    private final DoublePerlinNoiseSampler altitudeNoise;
    private final DoublePerlinNoiseSampler weirdnessNoise;
    private final List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> biomePoints;
    private final boolean threeDimensionalSampling;
    private final long seed;
    private final Optional<Preset> field_24721;

    public MultiNoiseBiomeSource(long seed, List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> list, Optional<Preset> optional) {
        this(seed, list, field_26433, field_26433, field_26433, field_26433, optional);
    }

    public MultiNoiseBiomeSource(long l, List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> list, class_5487 arg, class_5487 arg2, class_5487 arg3, class_5487 arg4) {
        this(l, list, arg, arg2, arg3, arg4, Optional.empty());
    }

    public MultiNoiseBiomeSource(long l, List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> list, class_5487 arg, class_5487 arg2, class_5487 arg3, class_5487 arg4, Optional<Preset> optional) {
        super(list.stream().map(Pair::getSecond).map(Supplier::get).collect(Collectors.toList()));
        this.seed = l;
        this.field_24721 = optional;
        this.field_26434 = arg;
        this.field_26435 = arg2;
        this.field_26436 = arg3;
        this.field_26437 = arg4;
        this.temperatureNoise = DoublePerlinNoiseSampler.method_30846(new ChunkRandom(l), arg.method_30832(), arg.method_30834());
        this.humidityNoise = DoublePerlinNoiseSampler.method_30846(new ChunkRandom(l + 1L), arg2.method_30832(), arg2.method_30834());
        this.altitudeNoise = DoublePerlinNoiseSampler.method_30846(new ChunkRandom(l + 2L), arg3.method_30832(), arg3.method_30834());
        this.weirdnessNoise = DoublePerlinNoiseSampler.method_30846(new ChunkRandom(l + 3L), arg4.method_30832(), arg4.method_30834());
        this.biomePoints = list;
        this.threeDimensionalSampling = false;
    }

    private static MultiNoiseBiomeSource method_28467(long l) {
        return new MultiNoiseBiomeSource(l, ImmutableList.of(Pair.of(new Biome.MixedNoisePoint(0.0f, 0.0f, 0.0f, 0.0f, 0.0f), () -> Biomes.NETHER_WASTES), Pair.of(new Biome.MixedNoisePoint(0.0f, -0.5f, 0.0f, 0.0f, 0.0f), () -> Biomes.SOUL_SAND_VALLEY), Pair.of(new Biome.MixedNoisePoint(0.4f, 0.0f, 0.0f, 0.0f, 0.0f), () -> Biomes.CRIMSON_FOREST), Pair.of(new Biome.MixedNoisePoint(0.0f, 0.5f, 0.0f, 0.0f, 0.375f), () -> Biomes.WARPED_FOREST), Pair.of(new Biome.MixedNoisePoint(-0.5f, 0.0f, 0.0f, 0.0f, 0.175f), () -> Biomes.BASALT_DELTAS)), Optional.of(Preset.NETHER));
    }

    @Override
    protected Codec<? extends BiomeSource> method_28442() {
        return CODEC;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public BiomeSource withSeed(long seed) {
        return new MultiNoiseBiomeSource(seed, this.biomePoints, this.field_26434, this.field_26435, this.field_26436, this.field_26437, this.field_24721);
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        int i = this.threeDimensionalSampling ? biomeY : 0;
        Biome.MixedNoisePoint mixedNoisePoint = new Biome.MixedNoisePoint((float)this.temperatureNoise.sample(biomeX, i, biomeZ), (float)this.humidityNoise.sample(biomeX, i, biomeZ), (float)this.altitudeNoise.sample(biomeX, i, biomeZ), (float)this.weirdnessNoise.sample(biomeX, i, biomeZ), 0.0f);
        return this.biomePoints.stream().min(Comparator.comparing(pair -> Float.valueOf(((Biome.MixedNoisePoint)pair.getFirst()).calculateDistanceTo(mixedNoisePoint)))).map(Pair::getSecond).map(Supplier::get).orElse(Biomes.THE_VOID);
    }

    public boolean method_28462(long l) {
        return this.seed == l && Objects.equals(this.field_24721, Optional.of(Preset.NETHER));
    }

    public static class Preset {
        private static final Map<Identifier, Preset> field_24724 = Maps.newHashMap();
        public static final MapCodec<Pair<Preset, Long>> CODEC = Codec.mapPair(Identifier.CODEC.flatXmap(identifier -> Optional.ofNullable(field_24724.get(identifier)).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown preset: " + identifier)), preset -> DataResult.success(preset.id)).fieldOf("preset"), Codec.LONG.fieldOf("seed")).stable();
        public static final Preset NETHER = new Preset(new Identifier("nether"), seed -> MultiNoiseBiomeSource.method_28465(seed));
        private final Identifier id;
        private final LongFunction<MultiNoiseBiomeSource> biomeSourceFunction;

        public Preset(Identifier id, LongFunction<MultiNoiseBiomeSource> longFunction) {
            this.id = id;
            this.biomeSourceFunction = longFunction;
            field_24724.put(id, this);
        }

        public MultiNoiseBiomeSource getBiomeSource(long seed) {
            return this.biomeSourceFunction.apply(seed);
        }
    }

    static class class_5487 {
        private final int field_26439;
        private final DoubleList field_26440;
        public static final Codec<class_5487> field_26438 = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("firstOctave")).forGetter(class_5487::method_30832), ((MapCodec)Codec.DOUBLE.listOf().fieldOf("amplitudes")).forGetter(class_5487::method_30834)).apply((Applicative<class_5487, ?>)instance, class_5487::new));

        public class_5487(int i, List<Double> list) {
            this.field_26439 = i;
            this.field_26440 = new DoubleArrayList(list);
        }

        public int method_30832() {
            return this.field_26439;
        }

        public DoubleList method_30834() {
            return this.field_26440;
        }
    }
}

