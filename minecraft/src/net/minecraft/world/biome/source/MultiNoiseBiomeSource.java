package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.class_6466;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryLookupCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.VanillaBiomeParameters;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.NoiseColumnSampler;

public class MultiNoiseBiomeSource extends BiomeSource {
	private static final MultiNoiseBiomeSource.NoiseParameters DEFAULT_NOISE_PARAMETERS = new MultiNoiseBiomeSource.NoiseParameters(-7, ImmutableList.of(1.0, 1.0));
	/**
	 * Used to parse a custom biome source, when a preset hasn't been provided.
	 */
	public static final MapCodec<MultiNoiseBiomeSource> CUSTOM_CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.LONG.fieldOf("seed").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.seed),
					RecordCodecBuilder.create(
							instancex -> instancex.group(
										MultiNoiseUtil.NoiseHypercube.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), Biome.REGISTRY_CODEC.fieldOf("biome").forGetter(Pair::getSecond)
									)
									.apply(instancex, Pair::of)
						)
						.listOf()
						.xmap(MultiNoiseUtil.Entries::new, MultiNoiseUtil.Entries::getEntries)
						.fieldOf("biomes")
						.forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.biomeEntries),
					MultiNoiseBiomeSource.NoiseParameters.CODEC
						.fieldOf("temperature_noise")
						.forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.temperatureNoiseParameters),
					MultiNoiseBiomeSource.NoiseParameters.CODEC.fieldOf("humidity_noise").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.humidityNoiseParameters),
					MultiNoiseBiomeSource.NoiseParameters.CODEC
						.fieldOf("continentalness_noise")
						.forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.continentalnessNoiseParameters),
					MultiNoiseBiomeSource.NoiseParameters.CODEC.fieldOf("erosion_noise").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.erosionNoiseParameters),
					MultiNoiseBiomeSource.NoiseParameters.CODEC.fieldOf("weirdness_noise").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.weirdnessNoiseParameters),
					Codec.INT.fieldOf("min_quart_y").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.minQuartY),
					Codec.INT.fieldOf("max_quart_y").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.maxQuartY)
				)
				.apply(instance, MultiNoiseBiomeSource::new)
	);
	public static final Codec<MultiNoiseBiomeSource> CODEC = Codec.mapEither(MultiNoiseBiomeSource.Instance.CODEC, CUSTOM_CODEC)
		.<MultiNoiseBiomeSource>xmap(
			either -> either.map(MultiNoiseBiomeSource.Instance::getBiomeSource, Function.identity()),
			multiNoiseBiomeSource -> (Either)multiNoiseBiomeSource.getInstance().map(Either::left).orElseGet(() -> Either.right(multiNoiseBiomeSource))
		)
		.codec();
	private final MultiNoiseBiomeSource.NoiseParameters temperatureNoiseParameters;
	private final MultiNoiseBiomeSource.NoiseParameters humidityNoiseParameters;
	private final MultiNoiseBiomeSource.NoiseParameters continentalnessNoiseParameters;
	private final MultiNoiseBiomeSource.NoiseParameters erosionNoiseParameters;
	private final MultiNoiseBiomeSource.NoiseParameters weirdnessNoiseParameters;
	private final DoublePerlinNoiseSampler temperatureNoise;
	private final DoublePerlinNoiseSampler humidityNoise;
	private final DoublePerlinNoiseSampler continentalnessNoise;
	private final DoublePerlinNoiseSampler erosionNoise;
	private final DoublePerlinNoiseSampler weirdnessNoise;
	private final DoublePerlinNoiseSampler locationOffsetNoise;
	private final class_6466 field_34194 = new class_6466();
	private final MultiNoiseUtil.Entries<Biome> biomeEntries;
	private final boolean threeDimensionalSampling;
	private final int minQuartY;
	private final int maxQuartY;
	private final long seed;
	private final Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> instance;

	public MultiNoiseBiomeSource(long seed, MultiNoiseUtil.Entries biomeEntries, Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> instance) {
		this(
			seed,
			biomeEntries,
			DEFAULT_NOISE_PARAMETERS,
			DEFAULT_NOISE_PARAMETERS,
			DEFAULT_NOISE_PARAMETERS,
			DEFAULT_NOISE_PARAMETERS,
			DEFAULT_NOISE_PARAMETERS,
			0,
			32,
			false,
			instance
		);
	}

	private MultiNoiseBiomeSource(
		long seed,
		MultiNoiseUtil.Entries biomeEntries,
		MultiNoiseBiomeSource.NoiseParameters temperatureNoiseParameters,
		MultiNoiseBiomeSource.NoiseParameters humidityNoiseParameters,
		MultiNoiseBiomeSource.NoiseParameters continentalnessNoiseParameters,
		MultiNoiseBiomeSource.NoiseParameters erosionNoiseParameters,
		MultiNoiseBiomeSource.NoiseParameters weirdnessNoiseParameters,
		int minQuartY,
		int maxQuartY
	) {
		this(
			seed,
			biomeEntries,
			temperatureNoiseParameters,
			humidityNoiseParameters,
			continentalnessNoiseParameters,
			erosionNoiseParameters,
			weirdnessNoiseParameters,
			minQuartY,
			maxQuartY,
			false,
			Optional.empty()
		);
	}

	public MultiNoiseBiomeSource(
		long seed,
		MultiNoiseUtil.Entries<Biome> biomeEntries,
		MultiNoiseBiomeSource.NoiseParameters temperatureNoiseParameters,
		MultiNoiseBiomeSource.NoiseParameters humidityNoiseParameters,
		MultiNoiseBiomeSource.NoiseParameters continentalnessNoiseParameters,
		MultiNoiseBiomeSource.NoiseParameters erosionNoiseParameters,
		MultiNoiseBiomeSource.NoiseParameters weirdnessNoiseParameters,
		int minQuartY,
		int maxQuartY,
		boolean threeDimensionalSampling,
		Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> instance
	) {
		super(biomeEntries.getEntries().stream().map(Pair::getSecond));
		this.seed = seed;
		this.erosionNoiseParameters = erosionNoiseParameters;
		this.instance = instance;
		this.temperatureNoiseParameters = temperatureNoiseParameters;
		this.humidityNoiseParameters = humidityNoiseParameters;
		this.continentalnessNoiseParameters = continentalnessNoiseParameters;
		this.weirdnessNoiseParameters = weirdnessNoiseParameters;
		this.temperatureNoise = DoublePerlinNoiseSampler.create(
			new ChunkRandom(seed), temperatureNoiseParameters.getFirstOctave(), temperatureNoiseParameters.getAmplitudes()
		);
		this.humidityNoise = DoublePerlinNoiseSampler.create(
			new ChunkRandom(seed + 1L), humidityNoiseParameters.getFirstOctave(), humidityNoiseParameters.getAmplitudes()
		);
		this.continentalnessNoise = DoublePerlinNoiseSampler.create(
			new ChunkRandom(seed + 2L), continentalnessNoiseParameters.getFirstOctave(), continentalnessNoiseParameters.getAmplitudes()
		);
		this.erosionNoise = DoublePerlinNoiseSampler.create(
			new ChunkRandom(seed + 3L), erosionNoiseParameters.getFirstOctave(), erosionNoiseParameters.getAmplitudes()
		);
		this.weirdnessNoise = DoublePerlinNoiseSampler.create(
			new ChunkRandom(seed + 4L), weirdnessNoiseParameters.getFirstOctave(), weirdnessNoiseParameters.getAmplitudes()
		);
		this.locationOffsetNoise = DoublePerlinNoiseSampler.create(new ChunkRandom(seed + 5L), -3, 1.0, 1.0, 1.0, 0.0);
		this.biomeEntries = biomeEntries;
		this.threeDimensionalSampling = threeDimensionalSampling;
		this.minQuartY = minQuartY;
		this.maxQuartY = maxQuartY;
	}

	public static MultiNoiseBiomeSource createVanillaSource(Registry<Biome> biomeRegistry, long seed) {
		ImmutableList<Pair<MultiNoiseUtil.NoiseHypercube, Supplier<Biome>>> immutableList = createVanillaBiomeEntries(biomeRegistry);
		MultiNoiseBiomeSource.NoiseParameters noiseParameters = new MultiNoiseBiomeSource.NoiseParameters(-9, 1.5, 0.0, 1.0, 0.0, 0.0, 0.0);
		MultiNoiseBiomeSource.NoiseParameters noiseParameters2 = new MultiNoiseBiomeSource.NoiseParameters(-7, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0);
		MultiNoiseBiomeSource.NoiseParameters noiseParameters3 = new MultiNoiseBiomeSource.NoiseParameters(-9, 1.0, 1.0, 2.0, 2.0, 2.0, 1.0, 1.0, 1.0, 1.0);
		MultiNoiseBiomeSource.NoiseParameters noiseParameters4 = new MultiNoiseBiomeSource.NoiseParameters(-9, 1.0, 1.0, 0.0, 1.0, 1.0);
		MultiNoiseBiomeSource.NoiseParameters noiseParameters5 = new MultiNoiseBiomeSource.NoiseParameters(-7, 1.0, 2.0, 1.0, 0.0, 0.0, 0.0);
		return new MultiNoiseBiomeSource(
			seed,
			new MultiNoiseUtil.Entries<>(immutableList),
			noiseParameters,
			noiseParameters2,
			noiseParameters3,
			noiseParameters4,
			noiseParameters5,
			-16,
			48,
			false,
			Optional.empty()
		);
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec() {
		return CODEC;
	}

	@Override
	public BiomeSource withSeed(long seed) {
		return new MultiNoiseBiomeSource(
			seed,
			this.biomeEntries,
			this.temperatureNoiseParameters,
			this.humidityNoiseParameters,
			this.continentalnessNoiseParameters,
			this.erosionNoiseParameters,
			this.weirdnessNoiseParameters,
			this.minQuartY,
			this.maxQuartY,
			this.threeDimensionalSampling,
			this.instance
		);
	}

	private Optional<MultiNoiseBiomeSource.Instance> getInstance() {
		return this.instance
			.map(pair -> new MultiNoiseBiomeSource.Instance((MultiNoiseBiomeSource.Preset)pair.getSecond(), (Registry<Biome>)pair.getFirst(), this.seed));
	}

	public boolean matchesInstance(long seed) {
		return this.seed == seed && this.instance.isPresent() && Objects.equals(((Pair)this.instance.get()).getSecond(), MultiNoiseBiomeSource.Preset.NETHER);
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		double d = (double)biomeX + this.sampleLocationOffsetNoise(biomeX, 0, biomeZ);
		double e = (double)biomeY + this.sampleLocationOffsetNoise(biomeY, biomeZ, biomeX);
		double f = (double)biomeZ + this.sampleLocationOffsetNoise(biomeZ, biomeX, 0);
		float g = (float)this.sampleContinentalnessNoise(d, 0.0, f);
		float h = (float)this.sampleErosionNoise(d, 0.0, f);
		float i = (float)this.sampleWeirdnessNoise(d, 0.0, f);
		double j = (double)this.field_34194.getOffset(this.field_34194.createTerrainNoisePoint(g, h, i));
		double k = 1.0;
		double l = -0.51875;
		double m = NoiseColumnSampler.getDepth(1.0, -0.51875, (double)(biomeY * 4)) + j;
		MultiNoiseUtil.NoiseValuePoint noiseValuePoint = MultiNoiseUtil.createNoiseValuePoint(
			(float)this.sampleTemperatureNoise(d, e, f), (float)this.sampleHumidityNoise(d, e, f), g, h, (float)m, i
		);
		return this.biomeEntries.getValue(noiseValuePoint, () -> BuiltinBiomes.THE_VOID);
	}

	@Override
	public BiomeSource.class_6482 method_37845(int i, int j) {
		double d = (double)i + this.sampleLocationOffsetNoise(i, 0, j);
		double e = (double)j + this.sampleLocationOffsetNoise(j, i, 0);
		float f = (float)this.sampleContinentalnessNoise(d, 0.0, e);
		float g = (float)this.sampleWeirdnessNoise(d, 0.0, e);
		float h = (float)this.sampleErosionNoise(d, 0.0, e);
		class_6466.TerrainNoisePoint terrainNoisePoint = this.field_34194.createTerrainNoisePoint(f, h, g);
		boolean bl = class_6466.method_37848(f, g);
		return new BiomeSource.class_6482((double)this.field_34194.getOffset(terrainNoisePoint), (double)this.field_34194.getFactor(terrainNoisePoint), bl);
	}

	public double sampleLocationOffsetNoise(int x, int y, int z) {
		return this.locationOffsetNoise.sample((double)x, (double)y, (double)z) * 4.0;
	}

	public double sampleTemperatureNoise(double x, double y, double z) {
		return this.temperatureNoise.sample(x, y, z);
	}

	public double sampleHumidityNoise(double x, double y, double z) {
		return this.humidityNoise.sample(x, y, z);
	}

	public double sampleContinentalnessNoise(double x, double y, double z) {
		if (SharedConstants.DEBUG_BIOME_SOURCE) {
			if (SharedConstants.method_37481((int)x * 4, (int)z * 4)) {
				return -1.0;
			} else {
				double d = MathHelper.fractionalPart(x / 2048.0) * 2.0 - 1.0;
				return d * d * (double)(d < 0.0 ? -1 : 1);
			}
		} else if (SharedConstants.field_34289) {
			double d = x * 0.01;
			return Math.sin(d + 0.5 * Math.sin(d));
		} else {
			return this.continentalnessNoise.sample(x, y, z);
		}
	}

	public double sampleErosionNoise(double x, double y, double z) {
		if (SharedConstants.DEBUG_BIOME_SOURCE) {
			if (SharedConstants.method_37481((int)x * 4, (int)z * 4)) {
				return -1.0;
			} else {
				double d = MathHelper.fractionalPart(z / 256.0) * 2.0 - 1.0;
				return d * d * (double)(d < 0.0 ? -1 : 1);
			}
		} else if (SharedConstants.field_34289) {
			double d = z * 0.01;
			return Math.sin(d + 0.5 * Math.sin(d));
		} else {
			return this.erosionNoise.sample(x, y, z);
		}
	}

	public double sampleWeirdnessNoise(double x, double y, double z) {
		return this.weirdnessNoise.sample(x, y, z);
	}

	public static ImmutableList<Pair<MultiNoiseUtil.NoiseHypercube, Supplier<Biome>>> createVanillaBiomeEntries(Registry<Biome> biomeRegistry) {
		Builder<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> builder = ImmutableList.builder();
		new VanillaBiomeParameters().writeVanillaBiomeParameters(builder);
		return (ImmutableList<Pair<MultiNoiseUtil.NoiseHypercube, Supplier<Biome>>>)builder.build()
			.stream()
			.map(entry -> entry.mapSecond(biomeKey -> () -> biomeRegistry.getOrThrow(biomeKey)))
			.collect(ImmutableList.toImmutableList());
	}

	@Override
	public void addDebugInfo(List<String> info, BlockPos pos) {
		int i = BiomeCoords.fromBlock(pos.getX());
		int j = BiomeCoords.fromBlock(pos.getY());
		int k = BiomeCoords.fromBlock(pos.getZ());
		double d = this.sampleContinentalnessNoise((double)i, 0.0, (double)k);
		double e = this.sampleErosionNoise((double)i, 0.0, (double)k);
		double f = this.sampleTemperatureNoise((double)i, (double)j, (double)k);
		double g = this.sampleHumidityNoise((double)i, (double)j, (double)k);
		double h = this.sampleWeirdnessNoise((double)i, 0.0, (double)k);
		double l = (double)class_6466.method_37731((float)h);
		BiomeSource.class_6482 lv = this.method_37845(i, k);
		DecimalFormat decimalFormat = new DecimalFormat("0.000");
		info.add(
			"Multinoise C: "
				+ decimalFormat.format(d)
				+ " E: "
				+ decimalFormat.format(e)
				+ " T: "
				+ decimalFormat.format(f)
				+ " H: "
				+ decimalFormat.format(g)
				+ " W: "
				+ decimalFormat.format(h)
		);
		info.add(
			"Terrain PV: "
				+ decimalFormat.format(l)
				+ " O: "
				+ decimalFormat.format(lv.field_34300)
				+ " F: "
				+ decimalFormat.format(lv.field_34301)
				+ (lv.field_34302 ? " coast" : "")
		);
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
						RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter(MultiNoiseBiomeSource.Instance::getBiomeRegistry),
						Codec.LONG.fieldOf("seed").stable().forGetter(MultiNoiseBiomeSource.Instance::getSeed)
					)
					.apply(instance, instance.stable(MultiNoiseBiomeSource.Instance::new))
		);
		private final MultiNoiseBiomeSource.Preset preset;
		private final Registry<Biome> biomeRegistry;
		private final long seed;

		Instance(MultiNoiseBiomeSource.Preset preset, Registry<Biome> biomeRegistry, long seed) {
			this.preset = preset;
			this.biomeRegistry = biomeRegistry;
			this.seed = seed;
		}

		public MultiNoiseBiomeSource.Preset getPreset() {
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

	public static class NoiseParameters {
		private final int firstOctave;
		private final DoubleList amplitudes;
		public static final Codec<MultiNoiseBiomeSource.NoiseParameters> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.INT.fieldOf("firstOctave").forGetter(MultiNoiseBiomeSource.NoiseParameters::getFirstOctave),
						Codec.DOUBLE.listOf().fieldOf("amplitudes").forGetter(MultiNoiseBiomeSource.NoiseParameters::getAmplitudes)
					)
					.apply(instance, MultiNoiseBiomeSource.NoiseParameters::new)
		);

		public NoiseParameters(int firstOctave, List<Double> amplitudes) {
			this.firstOctave = firstOctave;
			this.amplitudes = new DoubleArrayList(amplitudes);
		}

		public NoiseParameters(int firstOctave, double... amplitudes) {
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
		static final Map<Identifier, MultiNoiseBiomeSource.Preset> BY_IDENTIFIER = Maps.<Identifier, MultiNoiseBiomeSource.Preset>newHashMap();
		public static final MultiNoiseBiomeSource.Preset NETHER = new MultiNoiseBiomeSource.Preset(
			new Identifier("nether"),
			(preset, biomeRegistry, seed) -> new MultiNoiseBiomeSource(
					seed,
					new MultiNoiseUtil.Entries(
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
		public static final MultiNoiseBiomeSource.Preset OVERWORLD = new MultiNoiseBiomeSource.Preset(
			new Identifier("overworld"), (preset, biomeRegistry, seed) -> MultiNoiseBiomeSource.createVanillaSource(biomeRegistry, seed)
		);
		final Identifier id;
		private final Function3<MultiNoiseBiomeSource.Preset, Registry<Biome>, Long, MultiNoiseBiomeSource> biomeSourceFunction;

		public Preset(Identifier id, Function3<MultiNoiseBiomeSource.Preset, Registry<Biome>, Long, MultiNoiseBiomeSource> biomeSourceFunction) {
			this.id = id;
			this.biomeSourceFunction = biomeSourceFunction;
			BY_IDENTIFIER.put(id, this);
		}

		public MultiNoiseBiomeSource getBiomeSource(Registry<Biome> biomeRegistry, long seed) {
			return this.biomeSourceFunction.apply(this, biomeRegistry, seed);
		}
	}
}
