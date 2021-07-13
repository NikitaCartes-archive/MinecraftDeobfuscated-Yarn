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
import net.minecraft.class_6452;
import net.minecraft.class_6461;
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
										class_6452.MixedNoisePoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), Biome.REGISTRY_CODEC.fieldOf("biome").forGetter(Pair::getSecond)
									)
									.apply(instancex, Pair::of)
						)
						.listOf()
						.xmap(class_6452.class_6455::new, class_6452.class_6455::method_37636)
						.fieldOf("biomes")
						.forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.biomePoints),
					MultiNoiseBiomeSource.NoiseParameters.CODEC
						.fieldOf("temperature_noise")
						.forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.temperatureNoiseParameters),
					MultiNoiseBiomeSource.NoiseParameters.CODEC.fieldOf("humidity_noise").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.humidityNoiseParameters),
					MultiNoiseBiomeSource.NoiseParameters.CODEC.fieldOf("continentalness_noise").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.field_34189),
					MultiNoiseBiomeSource.NoiseParameters.CODEC.fieldOf("erosion_noise").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.field_34190),
					MultiNoiseBiomeSource.NoiseParameters.CODEC.fieldOf("weirdness_noise").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.weirdnessNoiseParameters),
					Codec.INT.fieldOf("min_quart_y").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.field_34195),
					Codec.INT.fieldOf("max_quart_y").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.field_34196)
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
	private final MultiNoiseBiomeSource.NoiseParameters field_34189;
	private final MultiNoiseBiomeSource.NoiseParameters field_34190;
	private final MultiNoiseBiomeSource.NoiseParameters weirdnessNoiseParameters;
	private final DoublePerlinNoiseSampler temperatureNoise;
	private final DoublePerlinNoiseSampler humidityNoise;
	private final DoublePerlinNoiseSampler field_34191;
	private final DoublePerlinNoiseSampler field_34192;
	private final DoublePerlinNoiseSampler weirdnessNoise;
	private final DoublePerlinNoiseSampler field_34193;
	private final class_6466 field_34194 = new class_6466();
	private final class_6452.class_6455<Biome> biomePoints;
	private final boolean threeDimensionalSampling;
	private final int field_34195;
	private final int field_34196;
	private final long seed;
	private final Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> instance;

	public MultiNoiseBiomeSource(long l, class_6452.class_6455 arg, Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> optional) {
		this(
			l,
			arg,
			DEFAULT_NOISE_PARAMETERS,
			DEFAULT_NOISE_PARAMETERS,
			DEFAULT_NOISE_PARAMETERS,
			DEFAULT_NOISE_PARAMETERS,
			DEFAULT_NOISE_PARAMETERS,
			0,
			32,
			false,
			optional
		);
	}

	private MultiNoiseBiomeSource(
		long l,
		class_6452.class_6455 arg,
		MultiNoiseBiomeSource.NoiseParameters noiseParameters,
		MultiNoiseBiomeSource.NoiseParameters noiseParameters2,
		MultiNoiseBiomeSource.NoiseParameters noiseParameters3,
		MultiNoiseBiomeSource.NoiseParameters noiseParameters4,
		MultiNoiseBiomeSource.NoiseParameters noiseParameters5,
		int i,
		int j
	) {
		this(l, arg, noiseParameters, noiseParameters2, noiseParameters3, noiseParameters4, noiseParameters5, i, j, false, Optional.empty());
	}

	public MultiNoiseBiomeSource(
		long l,
		class_6452.class_6455<Biome> arg,
		MultiNoiseBiomeSource.NoiseParameters noiseParameters,
		MultiNoiseBiomeSource.NoiseParameters noiseParameters2,
		MultiNoiseBiomeSource.NoiseParameters noiseParameters3,
		MultiNoiseBiomeSource.NoiseParameters noiseParameters4,
		MultiNoiseBiomeSource.NoiseParameters noiseParameters5,
		int i,
		int j,
		boolean bl,
		Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> optional
	) {
		super(arg.method_37636().stream().map(Pair::getSecond));
		this.seed = l;
		this.field_34190 = noiseParameters4;
		this.instance = optional;
		this.temperatureNoiseParameters = noiseParameters;
		this.humidityNoiseParameters = noiseParameters2;
		this.field_34189 = noiseParameters3;
		this.weirdnessNoiseParameters = noiseParameters5;
		this.temperatureNoise = DoublePerlinNoiseSampler.create(new ChunkRandom(l), noiseParameters.getFirstOctave(), noiseParameters.getAmplitudes());
		this.humidityNoise = DoublePerlinNoiseSampler.create(new ChunkRandom(l + 1L), noiseParameters2.getFirstOctave(), noiseParameters2.getAmplitudes());
		this.field_34191 = DoublePerlinNoiseSampler.create(new ChunkRandom(l + 2L), noiseParameters3.getFirstOctave(), noiseParameters3.getAmplitudes());
		this.field_34192 = DoublePerlinNoiseSampler.create(new ChunkRandom(l + 3L), noiseParameters4.getFirstOctave(), noiseParameters4.getAmplitudes());
		this.weirdnessNoise = DoublePerlinNoiseSampler.create(new ChunkRandom(l + 4L), noiseParameters5.getFirstOctave(), noiseParameters5.getAmplitudes());
		this.field_34193 = DoublePerlinNoiseSampler.create(new ChunkRandom(l + 5L), -3, 1.0, 1.0, 1.0, 0.0);
		this.biomePoints = arg;
		this.threeDimensionalSampling = bl;
		this.field_34195 = i;
		this.field_34196 = j;
	}

	public static MultiNoiseBiomeSource method_35242(Registry<Biome> registry, long l) {
		ImmutableList<Pair<class_6452.MixedNoisePoint, Supplier<Biome>>> immutableList = method_35241(registry);
		MultiNoiseBiomeSource.NoiseParameters noiseParameters = new MultiNoiseBiomeSource.NoiseParameters(-9, 1.0, 1.0, 0.0, 1.0, 1.0, 0.0);
		MultiNoiseBiomeSource.NoiseParameters noiseParameters2 = new MultiNoiseBiomeSource.NoiseParameters(-7, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0);
		MultiNoiseBiomeSource.NoiseParameters noiseParameters3 = new MultiNoiseBiomeSource.NoiseParameters(-9, 1.0, 1.0, 2.0, 2.0, 2.0, 1.0, 1.0, 1.0, 1.0);
		MultiNoiseBiomeSource.NoiseParameters noiseParameters4 = new MultiNoiseBiomeSource.NoiseParameters(-9, 1.0, 1.0, 0.0, 1.0, 1.0);
		MultiNoiseBiomeSource.NoiseParameters noiseParameters5 = new MultiNoiseBiomeSource.NoiseParameters(-7, 1.0, 2.0, 1.0, 0.0, 0.0, 0.0);
		return new MultiNoiseBiomeSource(
			l,
			new class_6452.class_6455<>(immutableList),
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
			this.biomePoints,
			this.temperatureNoiseParameters,
			this.humidityNoiseParameters,
			this.field_34189,
			this.field_34190,
			this.weirdnessNoiseParameters,
			this.field_34195,
			this.field_34196,
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
		double d = (double)biomeX + this.method_37684(biomeX, 0, biomeZ);
		double e = (double)biomeY + this.method_37684(biomeY, biomeZ, biomeX);
		double f = (double)biomeZ + this.method_37684(biomeZ, biomeX, 0);
		float g = (float)this.method_37694(d, 0.0, f);
		float h = (float)this.method_37696(d, 0.0, f);
		float i = (float)this.method_37699(d, 0.0, f);
		double j = (double)this.field_34194.method_37734(this.field_34194.method_37732(g, h, i));
		double k = 1.0;
		double l = -0.51875;
		double m = NoiseColumnSampler.method_37764(1.0, -0.51875, (double)(biomeY * 4)) + j;
		class_6452.class_6460 lv = class_6452.method_37622((float)this.method_37683(d, e, f), (float)this.method_37691(d, e, f), g, h, (float)m, i);
		return this.biomePoints.method_37640(lv, () -> BuiltinBiomes.THE_VOID);
	}

	@Override
	public double[] method_37612(int i, int j) {
		double d = (double)i + this.method_37684(i, 0, j);
		double e = (double)j + this.method_37684(j, i, 0);
		float f = (float)this.method_37694(d, 0.0, e);
		float g = (float)this.method_37699(d, 0.0, e);
		float h = (float)this.method_37696(d, 0.0, e);
		class_6466.class_6467 lv = this.field_34194.method_37732(f, h, g);
		return new double[]{(double)this.field_34194.method_37734(lv), (double)this.field_34194.method_37742(lv)};
	}

	public double method_37684(int i, int j, int k) {
		return this.field_34193.sample((double)i, (double)j, (double)k) * 4.0;
	}

	public double method_37683(double d, double e, double f) {
		return this.temperatureNoise.sample(d, e, f);
	}

	public double method_37691(double d, double e, double f) {
		return this.humidityNoise.sample(d, e, f);
	}

	public double method_37694(double d, double e, double f) {
		if (SharedConstants.field_34061) {
			return SharedConstants.method_37481((int)d * 4, (int)f * 4) ? -1.0 : MathHelper.fractionalPart(d / 2048.0) * 2.0 - 1.0;
		} else {
			return this.field_34191.sample(d, e, f);
		}
	}

	public double method_37696(double d, double e, double f) {
		if (SharedConstants.field_34061) {
			return SharedConstants.method_37481((int)d * 4, (int)f * 4) ? -1.0 : MathHelper.fractionalPart(f / 256.0) * 2.0 - 1.0;
		} else {
			return this.field_34192.sample(d, e, f);
		}
	}

	public double method_37699(double d, double e, double f) {
		return this.weirdnessNoise.sample(d, e, f);
	}

	public static ImmutableList<Pair<class_6452.MixedNoisePoint, Supplier<Biome>>> method_35241(Registry<Biome> registry) {
		Builder<Pair<class_6452.MixedNoisePoint, RegistryKey<Biome>>> builder = ImmutableList.builder();
		new class_6461().method_37705(builder);
		return (ImmutableList<Pair<class_6452.MixedNoisePoint, Supplier<Biome>>>)builder.build()
			.stream()
			.map(pair -> pair.mapSecond(registryKey -> () -> registry.getOrThrow(registryKey)))
			.collect(ImmutableList.toImmutableList());
	}

	@Override
	public void method_37617(List<String> list, BlockPos blockPos) {
		int i = BiomeCoords.fromBlock(blockPos.getX());
		int j = BiomeCoords.fromBlock(blockPos.getY());
		int k = BiomeCoords.fromBlock(blockPos.getZ());
		double d = this.method_37694((double)i, (double)j, (double)k);
		double e = this.method_37696((double)i, (double)j, (double)k);
		double f = this.method_37683((double)i, (double)j, (double)k);
		double g = this.method_37691((double)i, (double)j, (double)k);
		double h = this.method_37699((double)i, (double)j, (double)k);
		double l = (double)class_6466.method_37731((float)h);
		double[] ds = this.method_37612(i, k);
		double m = ds[0];
		double n = ds[1];
		DecimalFormat decimalFormat = new DecimalFormat("0.000");
		list.add(
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
		list.add("Terrain PV: " + decimalFormat.format(l) + " O: " + decimalFormat.format(m) + " F: " + decimalFormat.format(n));
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
					new class_6452.class_6455(
						ImmutableList.of(
							Pair.of(class_6452.method_37623(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> biomeRegistry.getOrThrow(BiomeKeys.NETHER_WASTES)),
							Pair.of(class_6452.method_37623(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> biomeRegistry.getOrThrow(BiomeKeys.SOUL_SAND_VALLEY)),
							Pair.of(class_6452.method_37623(0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> biomeRegistry.getOrThrow(BiomeKeys.CRIMSON_FOREST)),
							Pair.of(class_6452.method_37623(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.375F), () -> biomeRegistry.getOrThrow(BiomeKeys.WARPED_FOREST)),
							Pair.of(class_6452.method_37623(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.175F), () -> biomeRegistry.getOrThrow(BiomeKeys.BASALT_DELTAS))
						)
					),
					Optional.of(Pair.of(biomeRegistry, preset))
				)
		);
		public static final MultiNoiseBiomeSource.Preset field_34197 = new MultiNoiseBiomeSource.Preset(
			new Identifier("overworld"), (preset, registry, long_) -> MultiNoiseBiomeSource.method_35242(registry, long_)
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
