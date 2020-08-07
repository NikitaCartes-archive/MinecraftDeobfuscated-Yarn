package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.BuiltInBiomes;
import net.minecraft.world.gen.ChunkRandom;

public class MultiNoiseBiomeSource extends BiomeSource {
	private static final MultiNoiseBiomeSource.class_5487 field_26433 = new MultiNoiseBiomeSource.class_5487(-7, ImmutableList.of(1.0, 1.0));
	public static final MapCodec<MultiNoiseBiomeSource> field_24718 = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.LONG.fieldOf("seed").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.seed),
					RecordCodecBuilder.create(
							instancex -> instancex.group(
										Biome.MixedNoisePoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), Biome.REGISTRY_CODEC.fieldOf("biome").forGetter(Pair::getSecond)
									)
									.apply(instancex, Pair::of)
						)
						.listOf()
						.fieldOf("biomes")
						.forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.biomePoints),
					MultiNoiseBiomeSource.class_5487.field_26438.fieldOf("temperature_noise").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.field_26434),
					MultiNoiseBiomeSource.class_5487.field_26438.fieldOf("humidity_noise").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.field_26435),
					MultiNoiseBiomeSource.class_5487.field_26438.fieldOf("altitude_noise").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.field_26436),
					MultiNoiseBiomeSource.class_5487.field_26438.fieldOf("weirdness_noise").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.field_26437)
				)
				.apply(instance, MultiNoiseBiomeSource::new)
	);
	public static final Codec<MultiNoiseBiomeSource> CODEC = Codec.mapEither(MultiNoiseBiomeSource.class_5502.field_26694, field_24718)
		.<MultiNoiseBiomeSource>xmap(
			either -> either.map(MultiNoiseBiomeSource.class_5502::method_31101, Function.identity()),
			multiNoiseBiomeSource -> (Either)multiNoiseBiomeSource.method_31085().map(Either::left).orElseGet(() -> Either.right(multiNoiseBiomeSource))
		)
		.codec();
	private final MultiNoiseBiomeSource.class_5487 field_26434;
	private final MultiNoiseBiomeSource.class_5487 field_26435;
	private final MultiNoiseBiomeSource.class_5487 field_26436;
	private final MultiNoiseBiomeSource.class_5487 field_26437;
	private final DoublePerlinNoiseSampler temperatureNoise;
	private final DoublePerlinNoiseSampler humidityNoise;
	private final DoublePerlinNoiseSampler altitudeNoise;
	private final DoublePerlinNoiseSampler weirdnessNoise;
	private final List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> biomePoints;
	private final boolean threeDimensionalSampling;
	private final long seed;
	private final Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> field_24721;

	private MultiNoiseBiomeSource(
		long seed, List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> list, Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> optional
	) {
		this(seed, list, field_26433, field_26433, field_26433, field_26433, optional);
	}

	private MultiNoiseBiomeSource(
		long l,
		List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> list,
		MultiNoiseBiomeSource.class_5487 arg,
		MultiNoiseBiomeSource.class_5487 arg2,
		MultiNoiseBiomeSource.class_5487 arg3,
		MultiNoiseBiomeSource.class_5487 arg4
	) {
		this(l, list, arg, arg2, arg3, arg4, Optional.empty());
	}

	private MultiNoiseBiomeSource(
		long seed,
		List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> list,
		MultiNoiseBiomeSource.class_5487 arg,
		MultiNoiseBiomeSource.class_5487 arg2,
		MultiNoiseBiomeSource.class_5487 arg3,
		MultiNoiseBiomeSource.class_5487 arg4,
		Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> optional
	) {
		super(list.stream().map(Pair::getSecond));
		this.seed = seed;
		this.field_24721 = optional;
		this.field_26434 = arg;
		this.field_26435 = arg2;
		this.field_26436 = arg3;
		this.field_26437 = arg4;
		this.temperatureNoise = DoublePerlinNoiseSampler.method_30846(new ChunkRandom(seed), arg.method_30832(), arg.method_30834());
		this.humidityNoise = DoublePerlinNoiseSampler.method_30846(new ChunkRandom(seed + 1L), arg2.method_30832(), arg2.method_30834());
		this.altitudeNoise = DoublePerlinNoiseSampler.method_30846(new ChunkRandom(seed + 2L), arg3.method_30832(), arg3.method_30834());
		this.weirdnessNoise = DoublePerlinNoiseSampler.method_30846(new ChunkRandom(seed + 3L), arg4.method_30832(), arg4.method_30834());
		this.biomePoints = list;
		this.threeDimensionalSampling = false;
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec() {
		return CODEC;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public BiomeSource withSeed(long seed) {
		return new MultiNoiseBiomeSource(seed, this.biomePoints, this.field_26434, this.field_26435, this.field_26436, this.field_26437, this.field_24721);
	}

	private Optional<MultiNoiseBiomeSource.class_5502> method_31085() {
		return this.field_24721
			.map(pair -> new MultiNoiseBiomeSource.class_5502((MultiNoiseBiomeSource.Preset)pair.getSecond(), (Registry)pair.getFirst(), this.seed));
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
			.map(Supplier::get)
			.orElse(Biomes.THE_VOID);
	}

	public boolean method_28462(long l) {
		return this.seed == l && this.field_24721.isPresent() && Objects.equals(((Pair)this.field_24721.get()).getSecond(), MultiNoiseBiomeSource.Preset.NETHER);
	}

	public static class Preset {
		private static final Map<Identifier, MultiNoiseBiomeSource.Preset> field_24724 = Maps.<Identifier, MultiNoiseBiomeSource.Preset>newHashMap();
		public static final MultiNoiseBiomeSource.Preset NETHER = new MultiNoiseBiomeSource.Preset(
			new Identifier("nether"),
			(preset, registry, long_) -> new MultiNoiseBiomeSource(
					long_,
					ImmutableList.of(
						Pair.of(new Biome.MixedNoisePoint(0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> registry.method_31140(BuiltInBiomes.NETHER_WASTES)),
						Pair.of(new Biome.MixedNoisePoint(0.0F, -0.5F, 0.0F, 0.0F, 0.0F), () -> registry.method_31140(BuiltInBiomes.SOUL_SAND_VALLEY)),
						Pair.of(new Biome.MixedNoisePoint(0.4F, 0.0F, 0.0F, 0.0F, 0.0F), () -> registry.method_31140(BuiltInBiomes.CRIMSON_FOREST)),
						Pair.of(new Biome.MixedNoisePoint(0.0F, 0.5F, 0.0F, 0.0F, 0.375F), () -> registry.method_31140(BuiltInBiomes.WARPED_FOREST)),
						Pair.of(new Biome.MixedNoisePoint(-0.5F, 0.0F, 0.0F, 0.0F, 0.175F), () -> registry.method_31140(BuiltInBiomes.BASALT_DELTAS))
					),
					Optional.of(Pair.of(registry, preset))
				)
		);
		private final Identifier id;
		private final Function3<MultiNoiseBiomeSource.Preset, Registry<Biome>, Long, MultiNoiseBiomeSource> biomeSourceFunction;

		public Preset(Identifier id, Function3<MultiNoiseBiomeSource.Preset, Registry<Biome>, Long, MultiNoiseBiomeSource> function3) {
			this.id = id;
			this.biomeSourceFunction = function3;
			field_24724.put(id, this);
		}

		public MultiNoiseBiomeSource getBiomeSource(Registry<Biome> registry, long l) {
			return this.biomeSourceFunction.apply(this, registry, l);
		}
	}

	static class class_5487 {
		private final int field_26439;
		private final DoubleList field_26440;
		public static final Codec<MultiNoiseBiomeSource.class_5487> field_26438 = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.INT.fieldOf("firstOctave").forGetter(MultiNoiseBiomeSource.class_5487::method_30832),
						Codec.DOUBLE.listOf().fieldOf("amplitudes").forGetter(MultiNoiseBiomeSource.class_5487::method_30834)
					)
					.apply(instance, MultiNoiseBiomeSource.class_5487::new)
		);

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

	static final class class_5502 {
		public static final MapCodec<MultiNoiseBiomeSource.class_5502> field_26694 = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Identifier.CODEC
							.flatXmap(
								identifier -> (DataResult)Optional.ofNullable(MultiNoiseBiomeSource.Preset.field_24724.get(identifier))
										.map(DataResult::success)
										.orElseGet(() -> DataResult.error("Unknown preset: " + identifier)),
								preset -> DataResult.success(preset.id)
							)
							.fieldOf("preset")
							.stable()
							.forGetter(MultiNoiseBiomeSource.class_5502::method_31094),
						RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter(MultiNoiseBiomeSource.class_5502::method_31098),
						Codec.LONG.fieldOf("seed").stable().forGetter(MultiNoiseBiomeSource.class_5502::method_31100)
					)
					.apply(instance, instance.stable(MultiNoiseBiomeSource.class_5502::new))
		);
		private final MultiNoiseBiomeSource.Preset field_26695;
		private final Registry<Biome> field_26696;
		private final long field_26697;

		private class_5502(MultiNoiseBiomeSource.Preset preset, Registry<Biome> registry, long l) {
			this.field_26695 = preset;
			this.field_26696 = registry;
			this.field_26697 = l;
		}

		public MultiNoiseBiomeSource.Preset method_31094() {
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
}
