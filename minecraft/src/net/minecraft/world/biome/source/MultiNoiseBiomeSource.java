package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.ChunkRandom;

public class MultiNoiseBiomeSource extends BiomeSource {
	public static final MapCodec<MultiNoiseBiomeSource> field_24718 = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.LONG.fieldOf("seed").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.seed),
					RecordCodecBuilder.create(
							instancex -> instancex.group(
										Biome.MixedNoisePoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), Registry.BIOME.fieldOf("biome").forGetter(Pair::getSecond)
									)
									.apply(instancex, Pair::of)
						)
						.listOf()
						.fieldOf("biomes")
						.forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.biomePoints)
				)
				.apply(instance, MultiNoiseBiomeSource::new)
	);
	public static final Codec<MultiNoiseBiomeSource> CODEC = Codec.mapEither(MultiNoiseBiomeSource.Preset.CODEC, field_24718)
		.<MultiNoiseBiomeSource>xmap(
			either -> either.map(pair -> ((MultiNoiseBiomeSource.Preset)pair.getFirst()).getBiomeSource((Long)pair.getSecond()), Function.identity()),
			multiNoiseBiomeSource -> (Either)multiNoiseBiomeSource.field_24721
					.map(preset -> Either.left(Pair.of(preset, multiNoiseBiomeSource.seed)))
					.orElseGet(() -> Either.right(multiNoiseBiomeSource))
		)
		.codec();
	private final DoublePerlinNoiseSampler temperatureNoise;
	private final DoublePerlinNoiseSampler humidityNoise;
	private final DoublePerlinNoiseSampler altitudeNoise;
	private final DoublePerlinNoiseSampler weirdnessNoise;
	private final List<Pair<Biome.MixedNoisePoint, Biome>> biomePoints;
	private final boolean threeDimensionalSampling;
	private final long seed;
	private final Optional<MultiNoiseBiomeSource.Preset> field_24721;

	private MultiNoiseBiomeSource(long seed, List<Pair<Biome.MixedNoisePoint, Biome>> biomePoints) {
		this(seed, biomePoints, Optional.empty());
	}

	public MultiNoiseBiomeSource(long seed, List<Pair<Biome.MixedNoisePoint, Biome>> list, Optional<MultiNoiseBiomeSource.Preset> optional) {
		super((List<Biome>)list.stream().map(Pair::getSecond).collect(Collectors.toList()));
		this.seed = seed;
		this.field_24721 = optional;
		IntStream intStream = IntStream.rangeClosed(-7, -6);
		IntStream intStream2 = IntStream.rangeClosed(-7, -6);
		IntStream intStream3 = IntStream.rangeClosed(-7, -6);
		IntStream intStream4 = IntStream.rangeClosed(-7, -6);
		this.temperatureNoise = new DoublePerlinNoiseSampler(new ChunkRandom(seed), intStream);
		this.humidityNoise = new DoublePerlinNoiseSampler(new ChunkRandom(seed + 1L), intStream2);
		this.altitudeNoise = new DoublePerlinNoiseSampler(new ChunkRandom(seed + 2L), intStream3);
		this.weirdnessNoise = new DoublePerlinNoiseSampler(new ChunkRandom(seed + 3L), intStream4);
		this.biomePoints = list;
		this.threeDimensionalSampling = false;
	}

	private static MultiNoiseBiomeSource method_28467(long l) {
		ImmutableList<Biome> immutableList = ImmutableList.of(
			Biomes.NETHER_WASTES, Biomes.SOUL_SAND_VALLEY, Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST, Biomes.BASALT_DELTAS
		);
		return new MultiNoiseBiomeSource(
			l,
			(List<Pair<Biome.MixedNoisePoint, Biome>>)immutableList.stream()
				.flatMap(biome -> biome.streamNoises().map(point -> Pair.of(point, biome)))
				.collect(ImmutableList.toImmutableList()),
			Optional.of(MultiNoiseBiomeSource.Preset.NETHER)
		);
	}

	@Override
	protected Codec<? extends BiomeSource> method_28442() {
		return CODEC;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public BiomeSource withSeed(long seed) {
		return new MultiNoiseBiomeSource(seed, this.biomePoints, this.field_24721);
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		int i = this.threeDimensionalSampling ? biomeY : 0;
		Biome.MixedNoisePoint mixedNoisePoint = new Biome.MixedNoisePoint(
			(float)this.temperatureNoise.sample((double)biomeX, (double)i, (double)biomeZ),
			(float)this.humidityNoise.sample((double)biomeX, (double)i, (double)biomeZ),
			(float)this.altitudeNoise.sample((double)biomeX, (double)i, (double)biomeZ),
			(float)this.weirdnessNoise.sample((double)biomeX, (double)i, (double)biomeZ),
			0.0F
		);
		return (Biome)this.biomePoints
			.stream()
			.min(Comparator.comparing(pair -> ((Biome.MixedNoisePoint)pair.getFirst()).calculateDistanceTo(mixedNoisePoint)))
			.map(Pair::getSecond)
			.orElse(Biomes.THE_VOID);
	}

	public boolean method_28462(long l) {
		return this.seed == l && Objects.equals(this.field_24721, Optional.of(MultiNoiseBiomeSource.Preset.NETHER));
	}

	public static class Preset {
		private static final Map<Identifier, MultiNoiseBiomeSource.Preset> field_24724 = Maps.<Identifier, MultiNoiseBiomeSource.Preset>newHashMap();
		public static final MapCodec<Pair<MultiNoiseBiomeSource.Preset, Long>> CODEC = Codec.mapPair(
				Identifier.CODEC
					.<MultiNoiseBiomeSource.Preset>flatXmap(
						identifier -> (DataResult)Optional.ofNullable(field_24724.get(identifier))
								.map(DataResult::success)
								.orElseGet(() -> DataResult.error("Unknown preset: " + identifier)),
						preset -> DataResult.success(preset.id)
					)
					.fieldOf("preset"),
				Codec.LONG.fieldOf("seed")
			)
			.stable();
		public static final MultiNoiseBiomeSource.Preset NETHER = new MultiNoiseBiomeSource.Preset(
			new Identifier("nether"), seed -> MultiNoiseBiomeSource.method_28467(seed)
		);
		private final Identifier id;
		private final LongFunction<MultiNoiseBiomeSource> biomeSourceFunction;

		public Preset(Identifier id, LongFunction<MultiNoiseBiomeSource> longFunction) {
			this.id = id;
			this.biomeSourceFunction = longFunction;
			field_24724.put(id, this);
		}

		public MultiNoiseBiomeSource getBiomeSource(long seed) {
			return (MultiNoiseBiomeSource)this.biomeSourceFunction.apply(seed);
		}
	}
}
