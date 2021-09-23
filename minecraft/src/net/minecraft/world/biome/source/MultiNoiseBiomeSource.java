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
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.Identifier;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.dynamic.RegistryLookupCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.TerrainNoisePoint;
import net.minecraft.world.biome.source.util.VanillaBiomeParameters;
import net.minecraft.world.biome.source.util.VanillaTerrainParameters;
import net.minecraft.world.gen.NoiseColumnSampler;

public class MultiNoiseBiomeSource extends BiomeSource {
	/**
	 * Used to parse a custom biome source, when a preset hasn't been provided.
	 */
	public static final MapCodec<MultiNoiseBiomeSource> CUSTOM_CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					RecordCodecBuilder.create(
							instancex -> instancex.group(
										MultiNoiseUtil.NoiseHypercube.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), Biome.REGISTRY_CODEC.fieldOf("biome").forGetter(Pair::getSecond)
									)
									.apply(instancex, Pair::of)
						)
						.listOf()
						.xmap(MultiNoiseUtil.Entries::new, MultiNoiseUtil.Entries::getEntries)
						.fieldOf("biomes")
						.forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.biomeEntries)
				)
				.apply(instance, MultiNoiseBiomeSource::new)
	);
	public static final Codec<MultiNoiseBiomeSource> CODEC = Codec.mapEither(MultiNoiseBiomeSource.Instance.CODEC, CUSTOM_CODEC)
		.<MultiNoiseBiomeSource>xmap(
			either -> either.map(MultiNoiseBiomeSource.Instance::getBiomeSource, Function.identity()),
			multiNoiseBiomeSource -> (Either)multiNoiseBiomeSource.getInstance().map(Either::left).orElseGet(() -> Either.right(multiNoiseBiomeSource))
		)
		.codec();
	private final MultiNoiseUtil.Entries<Biome> biomeEntries;
	private final Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> instance;

	private MultiNoiseBiomeSource(MultiNoiseUtil.Entries<Biome> entries) {
		this(entries, Optional.empty());
	}

	MultiNoiseBiomeSource(MultiNoiseUtil.Entries<Biome> biomeEntries, Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> instance) {
		super(biomeEntries.getEntries().stream().map(Pair::getSecond));
		this.instance = instance;
		this.biomeEntries = biomeEntries;
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec() {
		return CODEC;
	}

	@Override
	public BiomeSource withSeed(long seed) {
		return this;
	}

	private Optional<MultiNoiseBiomeSource.Instance> getInstance() {
		return this.instance.map(pair -> new MultiNoiseBiomeSource.Instance((MultiNoiseBiomeSource.Preset)pair.getSecond(), (Registry<Biome>)pair.getFirst()));
	}

	public boolean matchesInstance(MultiNoiseBiomeSource.Preset instance) {
		return this.instance.isPresent() && Objects.equals(((Pair)this.instance.get()).getSecond(), instance);
	}

	@Override
	public Biome getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noiseSampler) {
		return this.getBiomeAtPoint(noiseSampler.sample(x, y, z));
	}

	@Debug
	public Biome getBiomeAtPoint(MultiNoiseUtil.NoiseValuePoint point) {
		return this.biomeEntries.getValue(point, () -> BuiltinBiomes.THE_VOID);
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
		double d = (double)VanillaTerrainParameters.getNormalizedWeirdness(m);
		DecimalFormat decimalFormat = new DecimalFormat("0.000");
		info.add(
			"Multinoise C: "
				+ decimalFormat.format((double)f)
				+ " E: "
				+ decimalFormat.format((double)g)
				+ " T: "
				+ decimalFormat.format((double)h)
				+ " H: "
				+ decimalFormat.format((double)l)
				+ " W: "
				+ decimalFormat.format((double)m)
		);
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
		if (noiseSampler instanceof NoiseColumnSampler noiseColumnSampler) {
			TerrainNoisePoint terrainNoisePoint = noiseColumnSampler.createTerrainNoisePoint(pos.getX(), pos.getZ(), f, m, g);
			info.add(
				"Terrain PV: "
					+ decimalFormat.format(d)
					+ " O: "
					+ decimalFormat.format(terrainNoisePoint.offset())
					+ " F: "
					+ decimalFormat.format(terrainNoisePoint.factor())
					+ " P: "
					+ decimalFormat.format(terrainNoisePoint.peaks())
			);
		}
	}

	static final class Instance {
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
							.forGetter(MultiNoiseBiomeSource.Instance::getPreset),
						RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter(MultiNoiseBiomeSource.Instance::getBiomeRegistry)
					)
					.apply(instance, instance.stable(MultiNoiseBiomeSource.Instance::new))
		);
		private final MultiNoiseBiomeSource.Preset preset;
		private final Registry<Biome> biomeRegistry;

		Instance(MultiNoiseBiomeSource.Preset preset, Registry<Biome> biomeRegistry) {
			this.preset = preset;
			this.biomeRegistry = biomeRegistry;
		}

		public MultiNoiseBiomeSource.Preset getPreset() {
			return this.preset;
		}

		public Registry<Biome> getBiomeRegistry() {
			return this.biomeRegistry;
		}

		public MultiNoiseBiomeSource getBiomeSource() {
			return this.preset.getBiomeSource(this.biomeRegistry);
		}
	}

	public static class Preset {
		static final Map<Identifier, MultiNoiseBiomeSource.Preset> BY_IDENTIFIER = Maps.<Identifier, MultiNoiseBiomeSource.Preset>newHashMap();
		public static final MultiNoiseBiomeSource.Preset NETHER = new MultiNoiseBiomeSource.Preset(
			new Identifier("nether"),
			(preset, biomeRegistry) -> new MultiNoiseBiomeSource(
					new MultiNoiseUtil.Entries<>(
						ImmutableList.of(
							Pair.of(MultiNoiseUtil.createNoiseHypercube(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> biomeRegistry.getOrThrow(BiomeKeys.NETHER_WASTES)),
							Pair.of(MultiNoiseUtil.createNoiseHypercube(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> biomeRegistry.getOrThrow(BiomeKeys.SOUL_SAND_VALLEY)),
							Pair.of(MultiNoiseUtil.createNoiseHypercube(0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> biomeRegistry.getOrThrow(BiomeKeys.CRIMSON_FOREST)),
							Pair.of(MultiNoiseUtil.createNoiseHypercube(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.375F), () -> biomeRegistry.getOrThrow(BiomeKeys.WARPED_FOREST)),
							Pair.of(MultiNoiseUtil.createNoiseHypercube(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.175F), () -> biomeRegistry.getOrThrow(BiomeKeys.BASALT_DELTAS))
						)
					),
					Optional.of(Pair.of(biomeRegistry, preset))
				)
		);
		public static final MultiNoiseBiomeSource.Preset OVERWORLD = new MultiNoiseBiomeSource.Preset(new Identifier("overworld"), (preset, registry) -> {
			Builder<Pair<MultiNoiseUtil.NoiseHypercube, Supplier<Biome>>> builder = ImmutableList.builder();
			new VanillaBiomeParameters().writeVanillaBiomeParameters(pair -> builder.add(pair.mapSecond(registryKey -> () -> (Biome)registry.getOrThrow(registryKey))));
			return new MultiNoiseBiomeSource(new MultiNoiseUtil.Entries<>(builder.build()), Optional.of(Pair.of(registry, preset)));
		});
		final Identifier id;
		private final BiFunction<MultiNoiseBiomeSource.Preset, Registry<Biome>, MultiNoiseBiomeSource> biomeSourceFunction;

		public Preset(Identifier id, BiFunction<MultiNoiseBiomeSource.Preset, Registry<Biome>, MultiNoiseBiomeSource> biomeSourceFunction) {
			this.id = id;
			this.biomeSourceFunction = biomeSourceFunction;
			BY_IDENTIFIER.put(id, this);
		}

		public MultiNoiseBiomeSource getBiomeSource(Registry<Biome> biomeRegistry) {
			return (MultiNoiseBiomeSource)this.biomeSourceFunction.apply(this, biomeRegistry);
		}
	}
}
