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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5504;
import net.minecraft.class_5505;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;

public class MultiNoiseBiomeSource
extends BiomeSource {
    private static final class_5487 field_26433 = new class_5487(-7, ImmutableList.of(Double.valueOf(1.0), Double.valueOf(1.0)));
    public static final MapCodec<MultiNoiseBiomeSource> field_24718 = RecordCodecBuilder.mapCodec(instance2 -> instance2.group(((MapCodec)Codec.LONG.fieldOf("seed")).forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.seed), ((MapCodec)RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Biome.MixedNoisePoint.CODEC.fieldOf("parameters")).forGetter(Pair::getFirst), ((MapCodec)Biome.REGISTRY_CODEC.fieldOf("biome")).forGetter(Pair::getSecond)).apply((Applicative<Pair, ?>)instance, Pair::of)).listOf().fieldOf("biomes")).forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.biomePoints), ((MapCodec)class_5487.field_26438.fieldOf("temperature_noise")).forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.field_26434), ((MapCodec)class_5487.field_26438.fieldOf("humidity_noise")).forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.field_26435), ((MapCodec)class_5487.field_26438.fieldOf("altitude_noise")).forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.field_26436), ((MapCodec)class_5487.field_26438.fieldOf("weirdness_noise")).forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.field_26437)).apply((Applicative<MultiNoiseBiomeSource, ?>)instance2, MultiNoiseBiomeSource::new));
    public static final Codec<MultiNoiseBiomeSource> CODEC = Codec.mapEither(class_5502.field_26694, field_24718).xmap(either -> either.map(class_5502::method_31101, Function.identity()), multiNoiseBiomeSource -> multiNoiseBiomeSource.method_31085().map(Either::left).orElseGet(() -> Either.right(multiNoiseBiomeSource))).codec();
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
    private final Optional<Pair<Registry<Biome>, Preset>> field_24721;

    private MultiNoiseBiomeSource(long seed, List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> list, Optional<Pair<Registry<Biome>, Preset>> optional) {
        this(seed, list, field_26433, field_26433, field_26433, field_26433, optional);
    }

    private MultiNoiseBiomeSource(long l, List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> list, class_5487 arg, class_5487 arg2, class_5487 arg3, class_5487 arg4) {
        this(l, list, arg, arg2, arg3, arg4, Optional.empty());
    }

    private MultiNoiseBiomeSource(long l, List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> list, class_5487 arg, class_5487 arg2, class_5487 arg3, class_5487 arg4, Optional<Pair<Registry<Biome>, Preset>> optional) {
        super(list.stream().map(Pair::getSecond));
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

    @Override
    protected Codec<? extends BiomeSource> method_28442() {
        return CODEC;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public BiomeSource withSeed(long seed) {
        return new MultiNoiseBiomeSource(seed, this.biomePoints, this.field_26434, this.field_26435, this.field_26436, this.field_26437, this.field_24721);
    }

    private Optional<class_5502> method_31085() {
        return this.field_24721.map(pair -> new class_5502((Preset)pair.getSecond(), (Registry)pair.getFirst(), this.seed));
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        int i = this.threeDimensionalSampling ? biomeY : 0;
        Biome.MixedNoisePoint mixedNoisePoint = new Biome.MixedNoisePoint((float)this.temperatureNoise.sample(biomeX, i, biomeZ), (float)this.humidityNoise.sample(biomeX, i, biomeZ), (float)this.altitudeNoise.sample(biomeX, i, biomeZ), (float)this.weirdnessNoise.sample(biomeX, i, biomeZ), 0.0f);
        return this.biomePoints.stream().min(Comparator.comparing(pair -> Float.valueOf(((Biome.MixedNoisePoint)pair.getFirst()).calculateDistanceTo(mixedNoisePoint)))).map(Pair::getSecond).map(Supplier::get).orElse(class_5504.field_26735);
    }

    public boolean method_28462(long l) {
        return this.seed == l && this.field_24721.isPresent() && Objects.equals(this.field_24721.get().getSecond(), Preset.NETHER);
    }

    public static class Preset {
        private static final Map<Identifier, Preset> field_24724 = Maps.newHashMap();
        public static final Preset NETHER = new Preset(new Identifier("nether"), (preset, registry, long_) -> new MultiNoiseBiomeSource((long)long_, ImmutableList.of(Pair.of(new Biome.MixedNoisePoint(0.0f, 0.0f, 0.0f, 0.0f, 0.0f), () -> registry.method_31140(Biomes.NETHER_WASTES)), Pair.of(new Biome.MixedNoisePoint(0.0f, -0.5f, 0.0f, 0.0f, 0.0f), () -> registry.method_31140(Biomes.SOUL_SAND_VALLEY)), Pair.of(new Biome.MixedNoisePoint(0.4f, 0.0f, 0.0f, 0.0f, 0.0f), () -> registry.method_31140(Biomes.CRIMSON_FOREST)), Pair.of(new Biome.MixedNoisePoint(0.0f, 0.5f, 0.0f, 0.0f, 0.375f), () -> registry.method_31140(Biomes.WARPED_FOREST)), Pair.of(new Biome.MixedNoisePoint(-0.5f, 0.0f, 0.0f, 0.0f, 0.175f), () -> registry.method_31140(Biomes.BASALT_DELTAS))), Optional.of(Pair.of(registry, preset))));
        private final Identifier id;
        private final Function3<Preset, Registry<Biome>, Long, MultiNoiseBiomeSource> biomeSourceFunction;

        public Preset(Identifier id, Function3<Preset, Registry<Biome>, Long, MultiNoiseBiomeSource> function3) {
            this.id = id;
            this.biomeSourceFunction = function3;
            field_24724.put(id, this);
        }

        public MultiNoiseBiomeSource getBiomeSource(Registry<Biome> registry, long l) {
            return this.biomeSourceFunction.apply(this, registry, l);
        }
    }

    static final class class_5502 {
        public static final MapCodec<class_5502> field_26694 = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Identifier.CODEC.flatXmap(identifier -> Optional.ofNullable(Preset.field_24724.get(identifier)).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown preset: " + identifier)), preset -> DataResult.success(((Preset)preset).id)).fieldOf("preset")).stable().forGetter(class_5502::method_31094), class_5505.method_31148(Registry.BIOME_KEY).forGetter(class_5502::method_31098), ((MapCodec)Codec.LONG.fieldOf("seed")).stable().forGetter(class_5502::method_31100)).apply((Applicative<class_5502, ?>)instance, instance.stable(class_5502::new)));
        private final Preset field_26695;
        private final Registry<Biome> field_26696;
        private final long field_26697;

        private class_5502(Preset preset, Registry<Biome> registry, long l) {
            this.field_26695 = preset;
            this.field_26696 = registry;
            this.field_26697 = l;
        }

        public Preset method_31094() {
            return this.field_26695;
        }

        public Registry<Biome> method_31098() {
            return this.field_26696;
        }

        public long method_31100() {
            return this.field_26697;
        }

        public MultiNoiseBiomeSource method_31101() {
            return this.field_26695.getBiomeSource(this.field_26696, this.field_26697);
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

