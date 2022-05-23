package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.Identifier;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.VanillaBiomeParameters;
import net.minecraft.world.gen.densityfunction.DensityFunctions;

public class MultiNoiseBiomeSource extends BiomeSource {
	/**
	 * Used to parse a custom biome source, when a preset hasn't been provided.
	 */
	public static final MapCodec<MultiNoiseBiomeSource> CUSTOM_CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codecs.nonEmptyList(
							RecordCodecBuilder.create(
									instance2 -> instance2.group(
												MultiNoiseUtil.NoiseHypercube.CODEC.fieldOf("parameters").forGetter(Pair::getFirst),
												Biome.REGISTRY_CODEC.fieldOf("biome").forGetter(Pair::getSecond)
											)
											.apply(instance2, Pair::of)
								)
								.listOf()
						)
						.xmap(MultiNoiseUtil.Entries::new, MultiNoiseUtil.Entries::getEntries)
						.fieldOf("biomes")
						.forGetter(biomeSource -> biomeSource.biomeEntries)
				)
				.apply(instance, MultiNoiseBiomeSource::new)
	);
	public static final Codec<MultiNoiseBiomeSource> CODEC = Codec.mapEither(MultiNoiseBiomeSource.Instance.CODEC, CUSTOM_CODEC)
		.<MultiNoiseBiomeSource>xmap(
			either -> either.map(MultiNoiseBiomeSource.Instance::getBiomeSource, Function.identity()),
			biomeSource -> (Either)biomeSource.getInstance().map(Either::left).orElseGet(() -> Either.right(biomeSource))
		)
		.codec();
	private final MultiNoiseUtil.Entries<RegistryEntry<Biome>> biomeEntries;
	private final Optional<MultiNoiseBiomeSource.Instance> instance;

	private MultiNoiseBiomeSource(MultiNoiseUtil.Entries<RegistryEntry<Biome>> entries) {
		this(entries, Optional.empty());
	}

	MultiNoiseBiomeSource(MultiNoiseUtil.Entries<RegistryEntry<Biome>> biomeEntries, Optional<MultiNoiseBiomeSource.Instance> instance) {
		super(biomeEntries.getEntries().stream().map(Pair::getSecond));
		this.instance = instance;
		this.biomeEntries = biomeEntries;
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec() {
		return CODEC;
	}

	private Optional<MultiNoiseBiomeSource.Instance> getInstance() {
		return this.instance;
	}

	public boolean matchesInstance(MultiNoiseBiomeSource.Preset instance) {
		return this.instance.isPresent() && Objects.equals(((MultiNoiseBiomeSource.Instance)this.instance.get()).preset(), instance);
	}

	@Override
	public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
		return this.getBiomeAtPoint(noise.sample(x, y, z));
	}

	@Debug
	public RegistryEntry<Biome> getBiomeAtPoint(MultiNoiseUtil.NoiseValuePoint point) {
		return this.biomeEntries.method_39529(point);
	}

	@Override
	public void addDebugInfo(List<String> info, BlockPos pos, MultiNoiseUtil.MultiNoiseSampler noiseSampler) {
		int i = BiomeCoords.fromBlock(pos.getX());
		int j = BiomeCoords.fromBlock(pos.getY());
		int k = BiomeCoords.fromBlock(pos.getZ());
		MultiNoiseUtil.NoiseValuePoint noiseValuePoint = noiseSampler.sample(i, j, k);
		float f = MultiNoiseUtil.method_38666(noiseValuePoint.continentalnessNoise());
		float g = MultiNoiseUtil.method_38666(noiseValuePoint.erosionNoise());
		float h = MultiNoiseUtil.method_38666(noiseValuePoint.temperatureNoise());
		float l = MultiNoiseUtil.method_38666(noiseValuePoint.humidityNoise());
		float m = MultiNoiseUtil.method_38666(noiseValuePoint.weirdnessNoise());
		double d = (double)DensityFunctions.method_41546(m);
		VanillaBiomeParameters vanillaBiomeParameters = new VanillaBiomeParameters();
		info.add(
			"Biome builder PV: "
				+ VanillaBiomeParameters.getWeirdnessDescription(d)
				+ " C: "
				+ vanillaBiomeParameters.getContinentalnessDescription((double)f)
				+ " E: "
				+ vanillaBiomeParameters.getErosionDescription((double)g)
				+ " T: "
				+ vanillaBiomeParameters.getTemperatureDescription((double)h)
				+ " H: "
				+ vanillaBiomeParameters.getHumidityDescription((double)l)
		);
	}

	static record Instance(MultiNoiseBiomeSource.Preset preset, Registry<Biome> biomeRegistry) {
		public static final MapCodec<MultiNoiseBiomeSource.Instance> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Identifier.CODEC
							.flatXmap(
								id -> (DataResult)Optional.ofNullable((MultiNoiseBiomeSource.Preset)MultiNoiseBiomeSource.Preset.BY_IDENTIFIER.get(id))
										.map(DataResult::success)
										.orElseGet(() -> DataResult.error("Unknown preset: " + id)),
								preset -> DataResult.success(preset.id)
							)
							.fieldOf("preset")
							.stable()
							.forGetter(MultiNoiseBiomeSource.Instance::preset),
						RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter(MultiNoiseBiomeSource.Instance::biomeRegistry)
					)
					.apply(instance, instance.stable(MultiNoiseBiomeSource.Instance::new))
		);

		public MultiNoiseBiomeSource getBiomeSource() {
			return this.preset.getBiomeSource(this, true);
		}
	}

	public static class Preset {
		static final Map<Identifier, MultiNoiseBiomeSource.Preset> BY_IDENTIFIER = Maps.<Identifier, MultiNoiseBiomeSource.Preset>newHashMap();
		public static final MultiNoiseBiomeSource.Preset NETHER = new MultiNoiseBiomeSource.Preset(
			new Identifier("nether"),
			biomeRegistry -> new MultiNoiseUtil.Entries(
					ImmutableList.of(
						Pair.of(MultiNoiseUtil.createNoiseHypercube(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), biomeRegistry.getOrCreateEntry(BiomeKeys.NETHER_WASTES)),
						Pair.of(MultiNoiseUtil.createNoiseHypercube(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), biomeRegistry.getOrCreateEntry(BiomeKeys.SOUL_SAND_VALLEY)),
						Pair.of(MultiNoiseUtil.createNoiseHypercube(0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), biomeRegistry.getOrCreateEntry(BiomeKeys.CRIMSON_FOREST)),
						Pair.of(MultiNoiseUtil.createNoiseHypercube(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.375F), biomeRegistry.getOrCreateEntry(BiomeKeys.WARPED_FOREST)),
						Pair.of(MultiNoiseUtil.createNoiseHypercube(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.175F), biomeRegistry.getOrCreateEntry(BiomeKeys.BASALT_DELTAS))
					)
				)
		);
		public static final MultiNoiseBiomeSource.Preset OVERWORLD = new MultiNoiseBiomeSource.Preset(new Identifier("overworld"), biomeRegistry -> {
			Builder<Pair<MultiNoiseUtil.NoiseHypercube, RegistryEntry<Biome>>> builder = ImmutableList.builder();
			new VanillaBiomeParameters().writeVanillaBiomeParameters(pair -> builder.add(pair.mapSecond(biomeRegistry::getOrCreateEntry)));
			return new MultiNoiseUtil.Entries(builder.build());
		});
		final Identifier id;
		private final Function<Registry<Biome>, MultiNoiseUtil.Entries<RegistryEntry<Biome>>> biomeSourceFunction;

		public Preset(Identifier id, Function<Registry<Biome>, MultiNoiseUtil.Entries<RegistryEntry<Biome>>> biomeSourceFunction) {
			this.id = id;
			this.biomeSourceFunction = biomeSourceFunction;
			BY_IDENTIFIER.put(id, this);
		}

		@Debug
		public static Stream<Pair<Identifier, MultiNoiseBiomeSource.Preset>> streamPresets() {
			return BY_IDENTIFIER.entrySet().stream().map(entry -> Pair.of((Identifier)entry.getKey(), (MultiNoiseBiomeSource.Preset)entry.getValue()));
		}

		MultiNoiseBiomeSource getBiomeSource(MultiNoiseBiomeSource.Instance instance, boolean useInstance) {
			MultiNoiseUtil.Entries<RegistryEntry<Biome>> entries = (MultiNoiseUtil.Entries<RegistryEntry<Biome>>)this.biomeSourceFunction
				.apply(instance.biomeRegistry());
			return new MultiNoiseBiomeSource(entries, useInstance ? Optional.of(instance) : Optional.empty());
		}

		public MultiNoiseBiomeSource getBiomeSource(Registry<Biome> biomeRegistry, boolean useInstance) {
			return this.getBiomeSource(new MultiNoiseBiomeSource.Instance(this, biomeRegistry), useInstance);
		}

		public MultiNoiseBiomeSource getBiomeSource(Registry<Biome> biomeRegistry) {
			return this.getBiomeSource(biomeRegistry, true);
		}

		public Stream<RegistryKey<Biome>> stream() {
			return this.getBiomeSource(BuiltinRegistries.BIOME).getBiomes().stream().flatMap(entry -> entry.getKey().stream());
		}
	}
}
