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
import net.minecraft.world.gen.chunk.Blender;

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
	private final MultiNoiseUtil.Entries<Supplier<Biome>> biomeEntries;
	private final Optional<MultiNoiseBiomeSource.Instance> instance;

	private MultiNoiseBiomeSource(MultiNoiseUtil.Entries<Supplier<Biome>> entries) {
		this(entries, Optional.empty());
	}

	MultiNoiseBiomeSource(MultiNoiseUtil.Entries<Supplier<Biome>> biomeEntries, Optional<MultiNoiseBiomeSource.Instance> instance) {
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
		return this.instance;
	}

	public boolean matchesInstance(MultiNoiseBiomeSource.Preset instance) {
		return this.instance.isPresent() && Objects.equals(((MultiNoiseBiomeSource.Instance)this.instance.get()).preset(), instance);
	}

	@Override
	public Biome getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
		return this.getBiomeAtPoint(noise.sample(x, y, z));
	}

	@Debug
	public Biome getBiomeAtPoint(MultiNoiseUtil.NoiseValuePoint point) {
		return (Biome)this.biomeEntries.method_39529(point, () -> BuiltinBiomes.THE_VOID).get();
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
			TerrainNoisePoint terrainNoisePoint = noiseColumnSampler.createTerrainNoisePoint(pos.getX(), pos.getZ(), f, m, g, Blender.getNoBlending());
			info.add(
				"Terrain PV: "
					+ decimalFormat.format(d)
					+ " O: "
					+ decimalFormat.format(terrainNoisePoint.offset())
					+ " F: "
					+ decimalFormat.format(terrainNoisePoint.factor())
					+ " JA: "
					+ decimalFormat.format(terrainNoisePoint.peaks())
			);
		}
	}

	static record Instance() {
		private final MultiNoiseBiomeSource.Preset preset;
		private final Registry<Biome> biomeRegistry;
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
						RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter(MultiNoiseBiomeSource.Instance::biomeRegistry)
					)
					.apply(instance, instance.stable(MultiNoiseBiomeSource.Instance::new))
		);

		Instance(MultiNoiseBiomeSource.Preset preset, Registry<Biome> biomeRegistry) {
			this.preset = preset;
			this.biomeRegistry = biomeRegistry;
		}

		public MultiNoiseBiomeSource getBiomeSource() {
			return this.preset.getBiomeSource(this, true);
		}
	}

	public static class Preset {
		static final Map<Identifier, MultiNoiseBiomeSource.Preset> BY_IDENTIFIER = Maps.<Identifier, MultiNoiseBiomeSource.Preset>newHashMap();
		public static final MultiNoiseBiomeSource.Preset NETHER = new MultiNoiseBiomeSource.Preset(
			new Identifier("nether"),
			registry -> new MultiNoiseUtil.Entries(
					ImmutableList.of(
						Pair.of(MultiNoiseUtil.createNoiseHypercube(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> registry.getOrThrow(BiomeKeys.NETHER_WASTES)),
						Pair.of(MultiNoiseUtil.createNoiseHypercube(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> registry.getOrThrow(BiomeKeys.SOUL_SAND_VALLEY)),
						Pair.of(MultiNoiseUtil.createNoiseHypercube(0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> registry.getOrThrow(BiomeKeys.CRIMSON_FOREST)),
						Pair.of(MultiNoiseUtil.createNoiseHypercube(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.375F), () -> registry.getOrThrow(BiomeKeys.WARPED_FOREST)),
						Pair.of(MultiNoiseUtil.createNoiseHypercube(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.175F), () -> registry.getOrThrow(BiomeKeys.BASALT_DELTAS))
					)
				)
		);
		public static final MultiNoiseBiomeSource.Preset OVERWORLD = new MultiNoiseBiomeSource.Preset(new Identifier("overworld"), registry -> {
			Builder<Pair<MultiNoiseUtil.NoiseHypercube, Supplier<Biome>>> builder = ImmutableList.builder();
			new VanillaBiomeParameters().writeVanillaBiomeParameters(pair -> builder.add(pair.mapSecond(registryKey -> () -> (Biome)registry.getOrThrow(registryKey))));
			return new MultiNoiseUtil.Entries(builder.build());
		});
		final Identifier id;
		private final Function<Registry<Biome>, MultiNoiseUtil.Entries<Supplier<Biome>>> biomeSourceFunction;

		public Preset(Identifier id, Function<Registry<Biome>, MultiNoiseUtil.Entries<Supplier<Biome>>> biomeSourceFunction) {
			this.id = id;
			this.biomeSourceFunction = biomeSourceFunction;
			BY_IDENTIFIER.put(id, this);
		}

		MultiNoiseBiomeSource getBiomeSource(MultiNoiseBiomeSource.Instance instance, boolean useInstance) {
			MultiNoiseUtil.Entries<Supplier<Biome>> entries = (MultiNoiseUtil.Entries<Supplier<Biome>>)this.biomeSourceFunction.apply(instance.biomeRegistry());
			return new MultiNoiseBiomeSource(entries, useInstance ? Optional.of(instance) : Optional.empty());
		}

		public MultiNoiseBiomeSource getBiomeSource(Registry<Biome> biomeRegistry, boolean useInstance) {
			return this.getBiomeSource(new MultiNoiseBiomeSource.Instance(this, biomeRegistry), useInstance);
		}

		public MultiNoiseBiomeSource getBiomeSource(Registry<Biome> biomeRegistry) {
			return this.getBiomeSource(biomeRegistry, true);
		}
	}
}
