package net.minecraft.world.gen.densityfunction;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import java.util.Arrays;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ToFloatFunction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.util.VanillaTerrainParameters;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import org.slf4j.Logger;

public final class DensityFunctionTypes {
	private static final Codec<DensityFunction> field_37062 = Registry.DENSITY_FUNCTION_TYPE.getCodec().dispatch(DensityFunction::getCodec, Function.identity());
	protected static final double field_37060 = 1000000.0;
	static final Codec<Double> field_37063 = Codec.doubleRange(-1000000.0, 1000000.0);
	public static final Codec<DensityFunction> field_37061 = Codec.either(field_37063, field_37062)
		.xmap(
			either -> either.map(DensityFunctionTypes::constant, Function.identity()),
			densityFunction -> densityFunction instanceof DensityFunctionTypes.Constant constant ? Either.left(constant.value()) : Either.right(densityFunction)
		);

	public static Codec<? extends DensityFunction> registerAndGetDefault(Registry<Codec<? extends DensityFunction>> registry) {
		register(registry, "blend_alpha", DensityFunctionTypes.BlendAlpha.CODEC);
		register(registry, "blend_offset", DensityFunctionTypes.BlendOffset.CODEC);
		register(registry, "beardifier", DensityFunctionTypes.Beardifier.CODEC);
		register(registry, "old_blended_noise", InterpolatedNoiseSampler.CODEC);

		for (DensityFunctionTypes.class_6927.Type type : DensityFunctionTypes.class_6927.Type.values()) {
			register(registry, type.asString(), type.codec);
		}

		register(registry, "noise", DensityFunctionTypes.Noise.CODEC);
		register(registry, "end_islands", DensityFunctionTypes.EndIslands.CODEC);
		register(registry, "weird_scaled_sampler", DensityFunctionTypes.WeirdScaledSampler.CODEC);
		register(registry, "shifted_noise", DensityFunctionTypes.ShiftedNoise.CODEC);
		register(registry, "range_choice", DensityFunctionTypes.RangeChoice.CODEC);
		register(registry, "shift_a", DensityFunctionTypes.ShiftA.CODEC);
		register(registry, "shift_b", DensityFunctionTypes.ShiftB.CODEC);
		register(registry, "shift", DensityFunctionTypes.Shift.CODEC);
		register(registry, "blend_density", DensityFunctionTypes.BlendDensity.CODEC);
		register(registry, "clamp", DensityFunctionTypes.Clamp.CODEC);

		for (DensityFunctionTypes.class_6925.class_6926 lv : DensityFunctionTypes.class_6925.class_6926.values()) {
			register(registry, lv.asString(), lv.codec);
		}

		register(registry, "slide", DensityFunctionTypes.Slide.CODEC);

		for (DensityFunctionTypes.class_7055.class_6918 lv2 : DensityFunctionTypes.class_7055.class_6918.values()) {
			register(registry, lv2.asString(), lv2.codec);
		}

		register(registry, "spline", DensityFunctionTypes.Spline.CODEC);
		register(registry, "terrain_shaper_spline", DensityFunctionTypes.TerrainShaperSpline.CODEC);
		register(registry, "constant", DensityFunctionTypes.Constant.CODEC);
		return register(registry, "y_clamped_gradient", DensityFunctionTypes.YClampedGradient.CODEC);
	}

	private static Codec<? extends DensityFunction> register(
		Registry<Codec<? extends DensityFunction>> registry, String id, Codec<? extends DensityFunction> codec
	) {
		return Registry.register(registry, id, codec);
	}

	static <A, O> Codec<O> method_41064(Codec<A> codec, Function<A, O> function, Function<O, A> function2) {
		return codec.fieldOf("argument").xmap(function, function2).codec();
	}

	static <O> Codec<O> method_41069(Function<DensityFunction, O> function, Function<O, DensityFunction> function2) {
		return method_41064(DensityFunction.field_37059, function, function2);
	}

	static <O> Codec<O> method_41068(
		BiFunction<DensityFunction, DensityFunction, O> biFunction, Function<O, DensityFunction> function, Function<O, DensityFunction> function2
	) {
		return RecordCodecBuilder.create(
			instance -> instance.group(
						DensityFunction.field_37059.fieldOf("argument1").forGetter(function), DensityFunction.field_37059.fieldOf("argument2").forGetter(function2)
					)
					.apply(instance, biFunction)
		);
	}

	static <O> Codec<O> method_41065(MapCodec<O> mapCodec) {
		return mapCodec.codec();
	}

	private DensityFunctionTypes() {
	}

	public static DensityFunction interpolated(DensityFunction inputFunction) {
		return new DensityFunctionTypes.class_6927(DensityFunctionTypes.class_6927.Type.INTERPOLATED, inputFunction);
	}

	public static DensityFunction flatCache(DensityFunction inputFunction) {
		return new DensityFunctionTypes.class_6927(DensityFunctionTypes.class_6927.Type.FLAT_CACHE, inputFunction);
	}

	public static DensityFunction cache2d(DensityFunction inputFunction) {
		return new DensityFunctionTypes.class_6927(DensityFunctionTypes.class_6927.Type.CACHE2D, inputFunction);
	}

	public static DensityFunction cacheOnce(DensityFunction inputFunction) {
		return new DensityFunctionTypes.class_6927(DensityFunctionTypes.class_6927.Type.CACHE_ONCE, inputFunction);
	}

	public static DensityFunction cacheAllInCell(DensityFunction inputFunction) {
		return new DensityFunctionTypes.class_6927(DensityFunctionTypes.class_6927.Type.CACHE_ALL_IN_CELL, inputFunction);
	}

	public static DensityFunction noise(
		RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, @Deprecated double d, double e, double f, double g
	) {
		return method_40484(new DensityFunctionTypes.Noise(noiseParameters, null, d, e), f, g);
	}

	public static DensityFunction noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, double d, double e, double f) {
		return noise(noiseParameters, 1.0, d, e, f);
	}

	public static DensityFunction noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, double d, double e) {
		return noise(noiseParameters, 1.0, 1.0, d, e);
	}

	public static DensityFunction shiftedNoise(
		DensityFunction densityFunction, DensityFunction densityFunction2, double d, RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters
	) {
		return new DensityFunctionTypes.ShiftedNoise(densityFunction, zero(), densityFunction2, d, 0.0, noiseParameters, null);
	}

	public static DensityFunction noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters) {
		return method_40502(noiseParameters, 1.0, 1.0);
	}

	public static DensityFunction method_40502(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, double d, double e) {
		return new DensityFunctionTypes.Noise(noiseParameters, null, d, e);
	}

	public static DensityFunction noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, double d) {
		return method_40502(noiseParameters, 1.0, d);
	}

	public static DensityFunction rangeChoice(
		DensityFunction densityFunction, double d, double e, DensityFunction densityFunction2, DensityFunction densityFunction3
	) {
		return new DensityFunctionTypes.RangeChoice(densityFunction, d, e, densityFunction2, densityFunction3);
	}

	public static DensityFunction shiftA(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters) {
		return new DensityFunctionTypes.ShiftA(noiseParameters, null);
	}

	public static DensityFunction shiftB(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters) {
		return new DensityFunctionTypes.ShiftB(noiseParameters, null);
	}

	public static DensityFunction shift(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters) {
		return new DensityFunctionTypes.Shift(noiseParameters, null);
	}

	public static DensityFunction blendDensity(DensityFunction densityFunction) {
		return new DensityFunctionTypes.BlendDensity(densityFunction);
	}

	public static DensityFunction endIslands(long seed) {
		return new DensityFunctionTypes.EndIslands(seed);
	}

	public static DensityFunction weirdScaledSampler(
		DensityFunction densityFunction,
		RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry,
		DensityFunctionTypes.WeirdScaledSampler.RarityValueMapper rarityValueMapper
	) {
		return new DensityFunctionTypes.WeirdScaledSampler(densityFunction, registryEntry, null, rarityValueMapper);
	}

	public static DensityFunction slide(GenerationShapeConfig generationShapeConfig, DensityFunction densityFunction) {
		return new DensityFunctionTypes.Slide(generationShapeConfig, densityFunction);
	}

	public static DensityFunction method_40486(DensityFunction densityFunction, DensityFunction densityFunction2) {
		return DensityFunctionTypes.class_7055.method_41097(DensityFunctionTypes.class_7055.class_6918.ADD, densityFunction, densityFunction2);
	}

	public static DensityFunction method_40500(DensityFunction densityFunction, DensityFunction densityFunction2) {
		return DensityFunctionTypes.class_7055.method_41097(DensityFunctionTypes.class_7055.class_6918.MUL, densityFunction, densityFunction2);
	}

	public static DensityFunction method_40505(DensityFunction densityFunction, DensityFunction densityFunction2) {
		return DensityFunctionTypes.class_7055.method_41097(DensityFunctionTypes.class_7055.class_6918.MIN, densityFunction, densityFunction2);
	}

	public static DensityFunction method_40508(DensityFunction densityFunction, DensityFunction densityFunction2) {
		return DensityFunctionTypes.class_7055.method_41097(DensityFunctionTypes.class_7055.class_6918.MAX, densityFunction, densityFunction2);
	}

	public static DensityFunction method_40489(
		DensityFunction densityFunction,
		DensityFunction densityFunction2,
		DensityFunction densityFunction3,
		DensityFunctionTypes.TerrainShaperSpline.class_7054 arg,
		double d,
		double e
	) {
		return new DensityFunctionTypes.TerrainShaperSpline(densityFunction, densityFunction2, densityFunction3, null, arg, d, e);
	}

	public static DensityFunction zero() {
		return DensityFunctionTypes.Constant.ZERO;
	}

	public static DensityFunction constant(double density) {
		return new DensityFunctionTypes.Constant(density);
	}

	public static DensityFunction yClampedGradient(int i, int j, double d, double e) {
		return new DensityFunctionTypes.YClampedGradient(i, j, d, e);
	}

	public static DensityFunction method_40490(DensityFunction densityFunction, DensityFunctionTypes.class_6925.class_6926 arg) {
		return DensityFunctionTypes.class_6925.method_41079(arg, densityFunction);
	}

	private static DensityFunction method_40484(DensityFunction densityFunction, double d, double e) {
		double f = (d + e) * 0.5;
		double g = (e - d) * 0.5;
		return method_40486(constant(f), method_40500(constant(g), densityFunction));
	}

	public static DensityFunction blendAlpha() {
		return DensityFunctionTypes.BlendAlpha.INSTANCE;
	}

	public static DensityFunction blendOffset() {
		return DensityFunctionTypes.BlendOffset.INSTANCE;
	}

	public static DensityFunction method_40488(DensityFunction densityFunction, DensityFunction densityFunction2, DensityFunction densityFunction3) {
		DensityFunction densityFunction4 = cacheOnce(densityFunction);
		DensityFunction densityFunction5 = method_40486(method_40500(densityFunction4, constant(-1.0)), constant(1.0));
		return method_40486(method_40500(densityFunction2, densityFunction5), method_40500(densityFunction3, densityFunction4));
	}

	protected static enum Beardifier implements DensityFunctionTypes.class_7050 {
		INSTANCE;

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return 0.0;
		}

		@Override
		public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			Arrays.fill(ds, 0.0);
		}

		@Override
		public double minValue() {
			return 0.0;
		}

		@Override
		public double maxValue() {
			return 0.0;
		}
	}

	protected static enum BlendAlpha implements DensityFunction.class_6913 {
		INSTANCE;

		public static final Codec<DensityFunction> CODEC = Codec.unit(INSTANCE);

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return 1.0;
		}

		@Override
		public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			Arrays.fill(ds, 1.0);
		}

		@Override
		public double minValue() {
			return 1.0;
		}

		@Override
		public double maxValue() {
			return 1.0;
		}

		@Override
		public Codec<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	static record BlendDensity(DensityFunction input) implements DensityFunctionTypes.class_6943 {
		static final Codec<DensityFunctionTypes.BlendDensity> CODEC = DensityFunctionTypes.method_41069(
			DensityFunctionTypes.BlendDensity::new, DensityFunctionTypes.BlendDensity::input
		);

		@Override
		public double method_40518(DensityFunction.NoisePos noisePos, double d) {
			return noisePos.getBlender().method_39338(noisePos, d);
		}

		@Override
		public DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
			return (DensityFunction)densityFunctionVisitor.apply(new DensityFunctionTypes.BlendDensity(this.input.method_40469(densityFunctionVisitor)));
		}

		@Override
		public double minValue() {
			return Double.NEGATIVE_INFINITY;
		}

		@Override
		public double maxValue() {
			return Double.POSITIVE_INFINITY;
		}

		@Override
		public Codec<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	protected static enum BlendOffset implements DensityFunction.class_6913 {
		INSTANCE;

		public static final Codec<DensityFunction> CODEC = Codec.unit(INSTANCE);

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return 0.0;
		}

		@Override
		public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			Arrays.fill(ds, 0.0);
		}

		@Override
		public double minValue() {
			return 0.0;
		}

		@Override
		public double maxValue() {
			return 0.0;
		}

		@Override
		public Codec<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	protected static record Clamp(DensityFunction input, double minValue, double maxValue) implements DensityFunctionTypes.class_6932 {
		private static final MapCodec<DensityFunctionTypes.Clamp> field_37083 = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						DensityFunction.field_37057.fieldOf("input").forGetter(DensityFunctionTypes.Clamp::input),
						DensityFunctionTypes.field_37063.fieldOf("min").forGetter(DensityFunctionTypes.Clamp::minValue),
						DensityFunctionTypes.field_37063.fieldOf("max").forGetter(DensityFunctionTypes.Clamp::maxValue)
					)
					.apply(instance, DensityFunctionTypes.Clamp::new)
		);
		public static final Codec<DensityFunctionTypes.Clamp> CODEC = DensityFunctionTypes.method_41065(field_37083);

		@Override
		public double apply(double d) {
			return MathHelper.clamp(d, this.minValue, this.maxValue);
		}

		@Override
		public DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
			return new DensityFunctionTypes.Clamp(this.input.method_40469(densityFunctionVisitor), this.minValue, this.maxValue);
		}

		@Override
		public Codec<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	static record Constant(double value) implements DensityFunction.class_6913 {
		static final Codec<DensityFunctionTypes.Constant> CODEC = DensityFunctionTypes.method_41064(
			DensityFunctionTypes.field_37063, DensityFunctionTypes.Constant::new, DensityFunctionTypes.Constant::value
		);
		static final DensityFunctionTypes.Constant ZERO = new DensityFunctionTypes.Constant(0.0);

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return this.value;
		}

		@Override
		public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			Arrays.fill(ds, this.value);
		}

		@Override
		public double minValue() {
			return this.value;
		}

		@Override
		public double maxValue() {
			return this.value;
		}

		@Override
		public Codec<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	protected static final class EndIslands implements DensityFunction.class_6913 {
		public static final Codec<DensityFunctionTypes.EndIslands> CODEC = Codec.unit(new DensityFunctionTypes.EndIslands(0L));
		final SimplexNoiseSampler field_36554;

		public EndIslands(long seed) {
			AbstractRandom abstractRandom = new AtomicSimpleRandom(seed);
			abstractRandom.skip(17292);
			this.field_36554 = new SimplexNoiseSampler(abstractRandom);
		}

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return ((double)TheEndBiomeSource.getNoiseAt(this.field_36554, pos.blockX() / 8, pos.blockZ() / 8) - 8.0) / 128.0;
		}

		@Override
		public double minValue() {
			return -0.84375;
		}

		@Override
		public double maxValue() {
			return 0.5625;
		}

		@Override
		public Codec<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	protected static record Noise(
		RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Nullable DoublePerlinNoiseSampler noise, @Deprecated double xzScale, double yScale
	) implements DensityFunction.class_6913 {
		public static final MapCodec<DensityFunctionTypes.Noise> field_37090 = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("noise").forGetter(DensityFunctionTypes.Noise::noiseData),
						Codec.DOUBLE.fieldOf("xz_scale").forGetter(DensityFunctionTypes.Noise::xzScale),
						Codec.DOUBLE.fieldOf("y_scale").forGetter(DensityFunctionTypes.Noise::yScale)
					)
					.apply(instance, DensityFunctionTypes.Noise::method_41084)
		);
		public static final Codec<DensityFunctionTypes.Noise> CODEC = DensityFunctionTypes.method_41065(field_37090);

		public static DensityFunctionTypes.Noise method_41084(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry, @Deprecated double d, double e) {
			return new DensityFunctionTypes.Noise(registryEntry, null, d, e);
		}

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return this.noise == null
				? 0.0
				: this.noise.sample((double)pos.blockX() * this.xzScale, (double)pos.blockY() * this.yScale, (double)pos.blockZ() * this.xzScale);
		}

		@Override
		public double minValue() {
			return -this.maxValue();
		}

		@Override
		public double maxValue() {
			return this.noise == null ? 2.0 : this.noise.method_40554();
		}

		@Override
		public Codec<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	static record RangeChoice(DensityFunction input, double minInclusive, double maxExclusive, DensityFunction whenInRange, DensityFunction whenOutOfRange)
		implements DensityFunction {
		public static final MapCodec<DensityFunctionTypes.RangeChoice> field_37092 = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						DensityFunction.field_37059.fieldOf("input").forGetter(DensityFunctionTypes.RangeChoice::input),
						DensityFunctionTypes.field_37063.fieldOf("min_inclusive").forGetter(DensityFunctionTypes.RangeChoice::minInclusive),
						DensityFunctionTypes.field_37063.fieldOf("max_exclusive").forGetter(DensityFunctionTypes.RangeChoice::maxExclusive),
						DensityFunction.field_37059.fieldOf("when_in_range").forGetter(DensityFunctionTypes.RangeChoice::whenInRange),
						DensityFunction.field_37059.fieldOf("when_out_of_range").forGetter(DensityFunctionTypes.RangeChoice::whenOutOfRange)
					)
					.apply(instance, DensityFunctionTypes.RangeChoice::new)
		);
		public static final Codec<DensityFunctionTypes.RangeChoice> CODEC = DensityFunctionTypes.method_41065(field_37092);

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			double d = this.input.sample(pos);
			return d >= this.minInclusive && d < this.maxExclusive ? this.whenInRange.sample(pos) : this.whenOutOfRange.sample(pos);
		}

		@Override
		public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			this.input.method_40470(ds, arg);

			for (int i = 0; i < ds.length; i++) {
				double d = ds[i];
				if (d >= this.minInclusive && d < this.maxExclusive) {
					ds[i] = this.whenInRange.sample(arg.method_40477(i));
				} else {
					ds[i] = this.whenOutOfRange.sample(arg.method_40477(i));
				}
			}
		}

		@Override
		public DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
			return (DensityFunction)densityFunctionVisitor.apply(
				new DensityFunctionTypes.RangeChoice(
					this.input.method_40469(densityFunctionVisitor),
					this.minInclusive,
					this.maxExclusive,
					this.whenInRange.method_40469(densityFunctionVisitor),
					this.whenOutOfRange.method_40469(densityFunctionVisitor)
				)
			);
		}

		@Override
		public double minValue() {
			return Math.min(this.whenInRange.minValue(), this.whenOutOfRange.minValue());
		}

		@Override
		public double maxValue() {
			return Math.max(this.whenInRange.maxValue(), this.whenOutOfRange.maxValue());
		}

		@Override
		public Codec<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	static record Shift(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Nullable DoublePerlinNoiseSampler offsetNoise)
		implements DensityFunctionTypes.class_6939 {
		static final Codec<DensityFunctionTypes.Shift> CODEC = DensityFunctionTypes.method_41064(
			DoublePerlinNoiseSampler.NoiseParameters.CODEC, registryEntry -> new DensityFunctionTypes.Shift(registryEntry, null), DensityFunctionTypes.Shift::noiseData
		);

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return this.method_40525((double)pos.blockX(), (double)pos.blockY(), (double)pos.blockZ());
		}

		@Override
		public DensityFunctionTypes.class_6939 method_41086(DoublePerlinNoiseSampler doublePerlinNoiseSampler) {
			return new DensityFunctionTypes.Shift(this.noiseData, doublePerlinNoiseSampler);
		}

		@Override
		public Codec<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	protected static record ShiftA(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Nullable DoublePerlinNoiseSampler offsetNoise)
		implements DensityFunctionTypes.class_6939 {
		static final Codec<DensityFunctionTypes.ShiftA> CODEC = DensityFunctionTypes.method_41064(
			DoublePerlinNoiseSampler.NoiseParameters.CODEC,
			registryEntry -> new DensityFunctionTypes.ShiftA(registryEntry, null),
			DensityFunctionTypes.ShiftA::noiseData
		);

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return this.method_40525((double)pos.blockX(), 0.0, (double)pos.blockZ());
		}

		@Override
		public DensityFunctionTypes.class_6939 method_41086(DoublePerlinNoiseSampler doublePerlinNoiseSampler) {
			return new DensityFunctionTypes.ShiftA(this.noiseData, doublePerlinNoiseSampler);
		}

		@Override
		public Codec<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	protected static record ShiftB(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Nullable DoublePerlinNoiseSampler offsetNoise)
		implements DensityFunctionTypes.class_6939 {
		static final Codec<DensityFunctionTypes.ShiftB> CODEC = DensityFunctionTypes.method_41064(
			DoublePerlinNoiseSampler.NoiseParameters.CODEC,
			registryEntry -> new DensityFunctionTypes.ShiftB(registryEntry, null),
			DensityFunctionTypes.ShiftB::noiseData
		);

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return this.method_40525((double)pos.blockZ(), (double)pos.blockX(), 0.0);
		}

		@Override
		public DensityFunctionTypes.class_6939 method_41086(DoublePerlinNoiseSampler doublePerlinNoiseSampler) {
			return new DensityFunctionTypes.ShiftB(this.noiseData, doublePerlinNoiseSampler);
		}

		@Override
		public Codec<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	protected static record ShiftedNoise(
		DensityFunction shiftX,
		DensityFunction shiftY,
		DensityFunction shiftZ,
		double xzScale,
		double yScale,
		RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData,
		@Nullable DoublePerlinNoiseSampler noise
	) implements DensityFunction {
		private static final MapCodec<DensityFunctionTypes.ShiftedNoise> field_37098 = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						DensityFunction.field_37059.fieldOf("shift_x").forGetter(DensityFunctionTypes.ShiftedNoise::shiftX),
						DensityFunction.field_37059.fieldOf("shift_y").forGetter(DensityFunctionTypes.ShiftedNoise::shiftY),
						DensityFunction.field_37059.fieldOf("shift_z").forGetter(DensityFunctionTypes.ShiftedNoise::shiftZ),
						Codec.DOUBLE.fieldOf("xz_scale").forGetter(DensityFunctionTypes.ShiftedNoise::xzScale),
						Codec.DOUBLE.fieldOf("y_scale").forGetter(DensityFunctionTypes.ShiftedNoise::yScale),
						DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("noise").forGetter(DensityFunctionTypes.ShiftedNoise::noiseData)
					)
					.apply(instance, DensityFunctionTypes.ShiftedNoise::create)
		);
		public static final Codec<DensityFunctionTypes.ShiftedNoise> CODEC = DensityFunctionTypes.method_41065(field_37098);

		public static DensityFunctionTypes.ShiftedNoise create(
			DensityFunction shiftX,
			DensityFunction shiftY,
			DensityFunction shiftZ,
			double xzScale,
			double yScale,
			RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData
		) {
			return new DensityFunctionTypes.ShiftedNoise(shiftX, shiftY, shiftZ, xzScale, yScale, noiseData, null);
		}

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			if (this.noise == null) {
				return 0.0;
			} else {
				double d = (double)pos.blockX() * this.xzScale + this.shiftX.sample(pos);
				double e = (double)pos.blockY() * this.yScale + this.shiftY.sample(pos);
				double f = (double)pos.blockZ() * this.xzScale + this.shiftZ.sample(pos);
				return this.noise.sample(d, e, f);
			}
		}

		@Override
		public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			arg.method_40478(ds, this);
		}

		@Override
		public DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
			return (DensityFunction)densityFunctionVisitor.apply(
				new DensityFunctionTypes.ShiftedNoise(
					this.shiftX.method_40469(densityFunctionVisitor),
					this.shiftY.method_40469(densityFunctionVisitor),
					this.shiftZ.method_40469(densityFunctionVisitor),
					this.xzScale,
					this.yScale,
					this.noiseData,
					this.noise
				)
			);
		}

		@Override
		public double minValue() {
			return -this.maxValue();
		}

		@Override
		public double maxValue() {
			return this.noise == null ? 2.0 : this.noise.method_40554();
		}

		@Override
		public Codec<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	protected static record Slide(@Nullable GenerationShapeConfig settings, DensityFunction input) implements DensityFunctionTypes.class_6943 {
		public static final Codec<DensityFunctionTypes.Slide> CODEC = DensityFunctionTypes.method_41069(
			densityFunction -> new DensityFunctionTypes.Slide(null, densityFunction), DensityFunctionTypes.Slide::input
		);

		@Override
		public double method_40518(DensityFunction.NoisePos noisePos, double d) {
			return this.settings == null ? d : DensityFunctions.method_40542(this.settings, d, (double)noisePos.blockY());
		}

		@Override
		public DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
			return (DensityFunction)densityFunctionVisitor.apply(new DensityFunctionTypes.Slide(this.settings, this.input.method_40469(densityFunctionVisitor)));
		}

		@Override
		public double minValue() {
			return this.settings == null
				? this.input.minValue()
				: Math.min(this.input.minValue(), Math.min(this.settings.bottomSlide().target(), this.settings.topSlide().target()));
		}

		@Override
		public double maxValue() {
			return this.settings == null
				? this.input.maxValue()
				: Math.max(this.input.maxValue(), Math.max(this.settings.bottomSlide().target(), this.settings.topSlide().target()));
		}

		@Override
		public Codec<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	public static record Spline(net.minecraft.util.math.Spline<VanillaTerrainParameters.class_7075> spline, double minValue, double maxValue)
		implements DensityFunction {
		private static final MapCodec<DensityFunctionTypes.Spline> field_37256 = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						VanillaTerrainParameters.field_37252.fieldOf("spline").forGetter(DensityFunctionTypes.Spline::spline),
						DensityFunctionTypes.field_37063.fieldOf("min_value").forGetter(DensityFunctionTypes.Spline::minValue),
						DensityFunctionTypes.field_37063.fieldOf("max_value").forGetter(DensityFunctionTypes.Spline::maxValue)
					)
					.apply(instance, DensityFunctionTypes.Spline::new)
		);
		public static final Codec<DensityFunctionTypes.Spline> CODEC = DensityFunctionTypes.method_41065(field_37256);

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return MathHelper.clamp((double)this.spline.apply(VanillaTerrainParameters.method_41191(pos)), this.minValue, this.maxValue);
		}

		@Override
		public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			arg.method_40478(ds, this);
		}

		@Override
		public DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
			return (DensityFunction)densityFunctionVisitor.apply(
				new DensityFunctionTypes.Spline(
					this.spline
						.method_41187(
							toFloatFunction -> (ToFloatFunction<VanillaTerrainParameters.class_7075>)(toFloatFunction instanceof VanillaTerrainParameters.class_7074 lv
									? lv.method_41194(densityFunctionVisitor)
									: toFloatFunction)
						),
					this.minValue,
					this.maxValue
				)
			);
		}

		@Override
		public Codec<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	@Deprecated
	public static record TerrainShaperSpline(
		DensityFunction continentalness,
		DensityFunction erosion,
		DensityFunction weirdness,
		@Nullable VanillaTerrainParameters shaper,
		DensityFunctionTypes.TerrainShaperSpline.class_7054 spline,
		double minValue,
		double maxValue
	) implements DensityFunction {
		private static final MapCodec<DensityFunctionTypes.TerrainShaperSpline> field_37101 = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						DensityFunction.field_37059.fieldOf("continentalness").forGetter(DensityFunctionTypes.TerrainShaperSpline::continentalness),
						DensityFunction.field_37059.fieldOf("erosion").forGetter(DensityFunctionTypes.TerrainShaperSpline::erosion),
						DensityFunction.field_37059.fieldOf("weirdness").forGetter(DensityFunctionTypes.TerrainShaperSpline::weirdness),
						DensityFunctionTypes.TerrainShaperSpline.class_7054.CODEC.fieldOf("spline").forGetter(DensityFunctionTypes.TerrainShaperSpline::spline),
						DensityFunctionTypes.field_37063.fieldOf("min_value").forGetter(DensityFunctionTypes.TerrainShaperSpline::minValue),
						DensityFunctionTypes.field_37063.fieldOf("max_value").forGetter(DensityFunctionTypes.TerrainShaperSpline::maxValue)
					)
					.apply(instance, DensityFunctionTypes.TerrainShaperSpline::method_41094)
		);
		public static final Codec<DensityFunctionTypes.TerrainShaperSpline> CODEC = DensityFunctionTypes.method_41065(field_37101);

		public static DensityFunctionTypes.TerrainShaperSpline method_41094(
			DensityFunction densityFunction,
			DensityFunction densityFunction2,
			DensityFunction densityFunction3,
			DensityFunctionTypes.TerrainShaperSpline.class_7054 arg,
			double d,
			double e
		) {
			return new DensityFunctionTypes.TerrainShaperSpline(densityFunction, densityFunction2, densityFunction3, null, arg, d, e);
		}

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return this.shaper == null
				? 0.0
				: MathHelper.clamp(
					(double)this.spline
						.field_37108
						.apply(
							this.shaper,
							VanillaTerrainParameters.createNoisePoint((float)this.continentalness.sample(pos), (float)this.erosion.sample(pos), (float)this.weirdness.sample(pos))
						),
					this.minValue,
					this.maxValue
				);
		}

		@Override
		public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			for (int i = 0; i < ds.length; i++) {
				ds[i] = this.sample(arg.method_40477(i));
			}
		}

		@Override
		public DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
			return (DensityFunction)densityFunctionVisitor.apply(
				new DensityFunctionTypes.TerrainShaperSpline(
					this.continentalness.method_40469(densityFunctionVisitor),
					this.erosion.method_40469(densityFunctionVisitor),
					this.weirdness.method_40469(densityFunctionVisitor),
					this.shaper,
					this.spline,
					this.minValue,
					this.maxValue
				)
			);
		}

		@Override
		public Codec<? extends DensityFunction> getCodec() {
			return CODEC;
		}

		interface class_7053 {
			float apply(VanillaTerrainParameters vanillaTerrainParameters, VanillaTerrainParameters.NoisePoint noisePoint);
		}

		public static enum class_7054 implements StringIdentifiable {
			OFFSET("offset", VanillaTerrainParameters::getOffset),
			FACTOR("factor", VanillaTerrainParameters::getFactor),
			JAGGEDNESS("jaggedness", VanillaTerrainParameters::getPeak);

			private static final Map<String, DensityFunctionTypes.TerrainShaperSpline.class_7054> field_37106 = (Map<String, DensityFunctionTypes.TerrainShaperSpline.class_7054>)Arrays.stream(
					values()
				)
				.collect(Collectors.toMap(DensityFunctionTypes.TerrainShaperSpline.class_7054::asString, arg -> arg));
			public static final Codec<DensityFunctionTypes.TerrainShaperSpline.class_7054> CODEC = StringIdentifiable.createCodec(
				DensityFunctionTypes.TerrainShaperSpline.class_7054::values, field_37106::get
			);
			private final String name;
			final DensityFunctionTypes.TerrainShaperSpline.class_7053 field_37108;

			private class_7054(String name, DensityFunctionTypes.TerrainShaperSpline.class_7053 arg) {
				this.name = name;
				this.field_37108 = arg;
			}

			@Override
			public String asString() {
				return this.name;
			}
		}
	}

	protected static record WeirdScaledSampler(
		DensityFunction input,
		RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData,
		@Nullable DoublePerlinNoiseSampler noise,
		DensityFunctionTypes.WeirdScaledSampler.RarityValueMapper rarityValueMapper
	) implements DensityFunctionTypes.class_6943 {
		private static final MapCodec<DensityFunctionTypes.WeirdScaledSampler> field_37065 = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						DensityFunction.field_37059.fieldOf("input").forGetter(DensityFunctionTypes.WeirdScaledSampler::input),
						DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("noise").forGetter(DensityFunctionTypes.WeirdScaledSampler::noiseData),
						DensityFunctionTypes.WeirdScaledSampler.RarityValueMapper.CODEC
							.fieldOf("rarity_value_mapper")
							.forGetter(DensityFunctionTypes.WeirdScaledSampler::rarityValueMapper)
					)
					.apply(instance, DensityFunctionTypes.WeirdScaledSampler::create)
		);
		public static final Codec<DensityFunctionTypes.WeirdScaledSampler> CODEC = DensityFunctionTypes.method_41065(field_37065);

		public static DensityFunctionTypes.WeirdScaledSampler create(
			DensityFunction input,
			RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData,
			DensityFunctionTypes.WeirdScaledSampler.RarityValueMapper rarityValueMapper
		) {
			return new DensityFunctionTypes.WeirdScaledSampler(input, noiseData, null, rarityValueMapper);
		}

		@Override
		public double method_40518(DensityFunction.NoisePos noisePos, double d) {
			if (this.noise == null) {
				return 0.0;
			} else {
				double e = this.rarityValueMapper.scaleFunction.get(d);
				return e * Math.abs(this.noise.sample((double)noisePos.blockX() / e, (double)noisePos.blockY() / e, (double)noisePos.blockZ() / e));
			}
		}

		@Override
		public DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
			this.input.method_40469(densityFunctionVisitor);
			return (DensityFunction)densityFunctionVisitor.apply(
				new DensityFunctionTypes.WeirdScaledSampler(this.input.method_40469(densityFunctionVisitor), this.noiseData, this.noise, this.rarityValueMapper)
			);
		}

		@Override
		public double minValue() {
			return 0.0;
		}

		@Override
		public double maxValue() {
			return this.rarityValueMapper.field_37072 * (this.noise == null ? 2.0 : this.noise.method_40554());
		}

		@Override
		public Codec<? extends DensityFunction> getCodec() {
			return CODEC;
		}

		public static enum RarityValueMapper implements StringIdentifiable {
			TYPE1("type_1", DensityFunctions.CaveScaler::scaleTunnels, 2.0),
			TYPE2("type_2", DensityFunctions.CaveScaler::scaleCaves, 3.0);

			private static final Map<String, DensityFunctionTypes.WeirdScaledSampler.RarityValueMapper> TYPES_MAP = (Map<String, DensityFunctionTypes.WeirdScaledSampler.RarityValueMapper>)Arrays.stream(
					values()
				)
				.collect(Collectors.toMap(DensityFunctionTypes.WeirdScaledSampler.RarityValueMapper::asString, rarityValueMapper -> rarityValueMapper));
			public static final Codec<DensityFunctionTypes.WeirdScaledSampler.RarityValueMapper> CODEC = StringIdentifiable.createCodec(
				DensityFunctionTypes.WeirdScaledSampler.RarityValueMapper::values, TYPES_MAP::get
			);
			private final String name;
			final Double2DoubleFunction scaleFunction;
			final double field_37072;

			private RarityValueMapper(String name, Double2DoubleFunction scaleFunction, double d) {
				this.name = name;
				this.scaleFunction = scaleFunction;
				this.field_37072 = d;
			}

			@Override
			public String asString() {
				return this.name;
			}
		}
	}

	public interface Wrapper extends DensityFunction {
		DensityFunctionTypes.class_6927.Type type();

		DensityFunction wrapped();

		@Override
		default Codec<? extends DensityFunction> getCodec() {
			return this.type().codec;
		}
	}

	static record YClampedGradient(int fromY, int toY, double fromValue, double toValue) implements DensityFunction.class_6913 {
		private static final MapCodec<DensityFunctionTypes.YClampedGradient> field_37075 = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Codec.intRange(DimensionType.MIN_HEIGHT * 2, DimensionType.MAX_COLUMN_HEIGHT * 2)
							.fieldOf("from_y")
							.forGetter(DensityFunctionTypes.YClampedGradient::fromY),
						Codec.intRange(DimensionType.MIN_HEIGHT * 2, DimensionType.MAX_COLUMN_HEIGHT * 2).fieldOf("to_y").forGetter(DensityFunctionTypes.YClampedGradient::toY),
						DensityFunctionTypes.field_37063.fieldOf("from_value").forGetter(DensityFunctionTypes.YClampedGradient::fromValue),
						DensityFunctionTypes.field_37063.fieldOf("to_value").forGetter(DensityFunctionTypes.YClampedGradient::toValue)
					)
					.apply(instance, DensityFunctionTypes.YClampedGradient::new)
		);
		public static final Codec<DensityFunctionTypes.YClampedGradient> CODEC = DensityFunctionTypes.method_41065(field_37075);

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return MathHelper.clampedLerpFromProgress((double)pos.blockY(), (double)this.fromY, (double)this.toY, this.fromValue, this.toValue);
		}

		@Override
		public double minValue() {
			return Math.min(this.fromValue, this.toValue);
		}

		@Override
		public double maxValue() {
			return Math.max(this.fromValue, this.toValue);
		}

		@Override
		public Codec<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	static record class_6917(
		DensityFunctionTypes.class_7055.class_6918 type, DensityFunction argument1, DensityFunction argument2, double minValue, double maxValue
	) implements DensityFunctionTypes.class_7055 {
		@Override
		public double sample(DensityFunction.NoisePos pos) {
			double d = this.argument1.sample(pos);

			return switch (this.type) {
				case ADD -> d + this.argument2.sample(pos);
				case MAX -> d > this.argument2.maxValue() ? d : Math.max(d, this.argument2.sample(pos));
				case MIN -> d < this.argument2.minValue() ? d : Math.min(d, this.argument2.sample(pos));
				case MUL -> d == 0.0 ? 0.0 : d * this.argument2.sample(pos);
			};
		}

		@Override
		public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			this.argument1.method_40470(ds, arg);
			switch (this.type) {
				case ADD:
					double[] es = new double[ds.length];
					this.argument2.method_40470(es, arg);

					for (int i = 0; i < ds.length; i++) {
						ds[i] += es[i];
					}
					break;
				case MAX:
					double e = this.argument2.maxValue();

					for (int k = 0; k < ds.length; k++) {
						double f = ds[k];
						ds[k] = f > e ? f : Math.max(f, this.argument2.sample(arg.method_40477(k)));
					}
					break;
				case MIN:
					double e = this.argument2.minValue();

					for (int k = 0; k < ds.length; k++) {
						double f = ds[k];
						ds[k] = f < e ? f : Math.min(f, this.argument2.sample(arg.method_40477(k)));
					}
					break;
				case MUL:
					for (int j = 0; j < ds.length; j++) {
						double d = ds[j];
						ds[j] = d == 0.0 ? 0.0 : d * this.argument2.sample(arg.method_40477(j));
					}
			}
		}

		@Override
		public DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
			return (DensityFunction)densityFunctionVisitor.apply(
				DensityFunctionTypes.class_7055.method_41097(
					this.type, this.argument1.method_40469(densityFunctionVisitor), this.argument2.method_40469(densityFunctionVisitor)
				)
			);
		}
	}

	protected static record class_6925(DensityFunctionTypes.class_6925.class_6926 type, DensityFunction input, double minValue, double maxValue)
		implements DensityFunctionTypes.class_6932 {
		public static DensityFunctionTypes.class_6925 method_41079(DensityFunctionTypes.class_6925.class_6926 arg, DensityFunction densityFunction) {
			double d = densityFunction.minValue();
			double e = method_40521(arg, d);
			double f = method_40521(arg, densityFunction.maxValue());
			return arg != DensityFunctionTypes.class_6925.class_6926.ABS && arg != DensityFunctionTypes.class_6925.class_6926.SQUARE
				? new DensityFunctionTypes.class_6925(arg, densityFunction, e, f)
				: new DensityFunctionTypes.class_6925(arg, densityFunction, Math.max(0.0, d), Math.max(e, f));
		}

		private static double method_40521(DensityFunctionTypes.class_6925.class_6926 arg, double d) {
			return switch (arg) {
				case ABS -> Math.abs(d);
				case SQUARE -> d * d;
				case CUBE -> d * d * d;
				case HALF_NEGATIVE -> d > 0.0 ? d : d * 0.5;
				case QUARTER_NEGATIVE -> d > 0.0 ? d : d * 0.25;
				case SQUEEZE -> {
					double e = MathHelper.clamp(d, -1.0, 1.0);
					yield e / 2.0 - e * e * e / 24.0;
				}
			};
		}

		@Override
		public double apply(double d) {
			return method_40521(this.type, d);
		}

		public DensityFunctionTypes.class_6925 method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
			return method_41079(this.type, this.input.method_40469(densityFunctionVisitor));
		}

		@Override
		public Codec<? extends DensityFunction> getCodec() {
			return this.type.codec;
		}

		static enum class_6926 implements StringIdentifiable {
			ABS("abs"),
			SQUARE("square"),
			CUBE("cube"),
			HALF_NEGATIVE("half_negative"),
			QUARTER_NEGATIVE("quarter_negative"),
			SQUEEZE("squeeze");

			private final String name;
			final Codec<DensityFunctionTypes.class_6925> codec = DensityFunctionTypes.method_41069(
				densityFunction -> DensityFunctionTypes.class_6925.method_41079(this, densityFunction), DensityFunctionTypes.class_6925::input
			);

			private class_6926(String name) {
				this.name = name;
			}

			@Override
			public String asString() {
				return this.name;
			}
		}
	}

	protected static record class_6927(DensityFunctionTypes.class_6927.Type type, DensityFunction wrapped) implements DensityFunctionTypes.Wrapper {
		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return this.wrapped.sample(pos);
		}

		@Override
		public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			this.wrapped.method_40470(ds, arg);
		}

		@Override
		public DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
			return (DensityFunction)densityFunctionVisitor.apply(new DensityFunctionTypes.class_6927(this.type, this.wrapped.method_40469(densityFunctionVisitor)));
		}

		@Override
		public double minValue() {
			return this.wrapped.minValue();
		}

		@Override
		public double maxValue() {
			return this.wrapped.maxValue();
		}

		static enum Type implements StringIdentifiable {
			INTERPOLATED("interpolated"),
			FLAT_CACHE("flat_cache"),
			CACHE2D("cache_2d"),
			CACHE_ONCE("cache_once"),
			CACHE_ALL_IN_CELL("cache_all_in_cell");

			private final String name;
			final Codec<DensityFunctionTypes.Wrapper> codec = DensityFunctionTypes.method_41069(
				densityFunction -> new DensityFunctionTypes.class_6927(this, densityFunction), DensityFunctionTypes.Wrapper::wrapped
			);

			private Type(String name) {
				this.name = name;
			}

			@Override
			public String asString() {
				return this.name;
			}
		}
	}

	static record class_6929(DensityFunctionTypes.class_6929.class_6930 specificType, DensityFunction input, double minValue, double maxValue, double argument)
		implements DensityFunctionTypes.class_7055,
		DensityFunctionTypes.class_6932 {
		@Override
		public DensityFunctionTypes.class_7055.class_6918 type() {
			return this.specificType == DensityFunctionTypes.class_6929.class_6930.MUL
				? DensityFunctionTypes.class_7055.class_6918.MUL
				: DensityFunctionTypes.class_7055.class_6918.ADD;
		}

		@Override
		public DensityFunction argument1() {
			return DensityFunctionTypes.constant(this.argument);
		}

		@Override
		public DensityFunction argument2() {
			return this.input;
		}

		@Override
		public double apply(double d) {
			return switch (this.specificType) {
				case MUL -> d * this.argument;
				case ADD -> d + this.argument;
			};
		}

		@Override
		public DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
			DensityFunction densityFunction = this.input.method_40469(densityFunctionVisitor);
			double d = densityFunction.minValue();
			double e = densityFunction.maxValue();
			double f;
			double g;
			if (this.specificType == DensityFunctionTypes.class_6929.class_6930.ADD) {
				f = d + this.argument;
				g = e + this.argument;
			} else if (this.argument >= 0.0) {
				f = d * this.argument;
				g = e * this.argument;
			} else {
				f = e * this.argument;
				g = d * this.argument;
			}

			return new DensityFunctionTypes.class_6929(this.specificType, densityFunction, f, g, this.argument);
		}

		static enum class_6930 {
			MUL,
			ADD;
		}
	}

	interface class_6932 extends DensityFunction {
		DensityFunction input();

		@Override
		default double sample(DensityFunction.NoisePos pos) {
			return this.apply(this.input().sample(pos));
		}

		@Override
		default void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			this.input().method_40470(ds, arg);

			for (int i = 0; i < ds.length; i++) {
				ds[i] = this.apply(ds[i]);
			}
		}

		double apply(double d);
	}

	interface class_6939 extends DensityFunction.class_6913 {
		RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData();

		@Nullable
		DoublePerlinNoiseSampler offsetNoise();

		@Override
		default double minValue() {
			return -this.maxValue();
		}

		@Override
		default double maxValue() {
			DoublePerlinNoiseSampler doublePerlinNoiseSampler = this.offsetNoise();
			return (doublePerlinNoiseSampler == null ? 2.0 : doublePerlinNoiseSampler.method_40554()) * 4.0;
		}

		default double method_40525(double d, double e, double f) {
			DoublePerlinNoiseSampler doublePerlinNoiseSampler = this.offsetNoise();
			return doublePerlinNoiseSampler == null ? 0.0 : doublePerlinNoiseSampler.sample(d * 0.25, e * 0.25, f * 0.25) * 4.0;
		}

		DensityFunctionTypes.class_6939 method_41086(DoublePerlinNoiseSampler doublePerlinNoiseSampler);
	}

	interface class_6943 extends DensityFunction {
		DensityFunction input();

		@Override
		default double sample(DensityFunction.NoisePos pos) {
			return this.method_40518(pos, this.input().sample(pos));
		}

		@Override
		default void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			this.input().method_40470(ds, arg);

			for (int i = 0; i < ds.length; i++) {
				ds[i] = this.method_40518(arg.method_40477(i), ds[i]);
			}
		}

		double method_40518(DensityFunction.NoisePos noisePos, double d);
	}

	public interface class_7050 extends DensityFunction.class_6913 {
		Codec<DensityFunction> CODEC = Codec.unit(DensityFunctionTypes.Beardifier.INSTANCE);

		@Override
		default Codec<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	protected static record class_7051(RegistryEntry<DensityFunction> function) implements DensityFunction {
		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return this.function.value().sample(pos);
		}

		@Override
		public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			this.function.value().method_40470(ds, arg);
		}

		@Override
		public DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
			return (DensityFunction)densityFunctionVisitor.apply(
				new DensityFunctionTypes.class_7051(new RegistryEntry.Direct<>(this.function.value().method_40469(densityFunctionVisitor)))
			);
		}

		@Override
		public double minValue() {
			return this.function.value().minValue();
		}

		@Override
		public double maxValue() {
			return this.function.value().maxValue();
		}

		@Override
		public Codec<? extends DensityFunction> getCodec() {
			throw new UnsupportedOperationException("Calling .codec() on HolderHolder");
		}
	}

	interface class_7055 extends DensityFunction {
		Logger LOGGER = LogUtils.getLogger();

		static DensityFunctionTypes.class_7055 method_41097(
			DensityFunctionTypes.class_7055.class_6918 arg, DensityFunction densityFunction, DensityFunction densityFunction2
		) {
			double d = densityFunction.minValue();
			double e = densityFunction2.minValue();
			double f = densityFunction.maxValue();
			double g = densityFunction2.maxValue();
			if (arg == DensityFunctionTypes.class_7055.class_6918.MIN || arg == DensityFunctionTypes.class_7055.class_6918.MAX) {
				boolean bl = d >= g;
				boolean bl2 = e >= f;
				if (bl || bl2) {
					LOGGER.warn("Creating a " + arg + " function between two non-overlapping inputs: " + densityFunction + " and " + densityFunction2);
				}
			}
			double h = switch (arg) {
				case ADD -> d + e;
				case MAX -> Math.max(d, e);
				case MIN -> Math.min(d, e);
				case MUL -> d > 0.0 && e > 0.0 ? d * e : (f < 0.0 && g < 0.0 ? f * g : Math.min(d * g, f * e));
			};

			double i = switch (arg) {
				case ADD -> f + g;
				case MAX -> Math.max(f, g);
				case MIN -> Math.min(f, g);
				case MUL -> d > 0.0 && e > 0.0 ? f * g : (f < 0.0 && g < 0.0 ? d * e : Math.max(d * e, f * g));
			};
			if (arg == DensityFunctionTypes.class_7055.class_6918.MUL || arg == DensityFunctionTypes.class_7055.class_6918.ADD) {
				if (densityFunction instanceof DensityFunctionTypes.Constant constant) {
					return new DensityFunctionTypes.class_6929(
						arg == DensityFunctionTypes.class_7055.class_6918.ADD ? DensityFunctionTypes.class_6929.class_6930.ADD : DensityFunctionTypes.class_6929.class_6930.MUL,
						densityFunction2,
						h,
						i,
						constant.value
					);
				}

				if (densityFunction2 instanceof DensityFunctionTypes.Constant constant) {
					return new DensityFunctionTypes.class_6929(
						arg == DensityFunctionTypes.class_7055.class_6918.ADD ? DensityFunctionTypes.class_6929.class_6930.ADD : DensityFunctionTypes.class_6929.class_6930.MUL,
						densityFunction,
						h,
						i,
						constant.value
					);
				}
			}

			return new DensityFunctionTypes.class_6917(arg, densityFunction, densityFunction2, h, i);
		}

		DensityFunctionTypes.class_7055.class_6918 type();

		DensityFunction argument1();

		DensityFunction argument2();

		@Override
		default Codec<? extends DensityFunction> getCodec() {
			return this.type().codec;
		}

		public static enum class_6918 implements StringIdentifiable {
			ADD("add"),
			MUL("mul"),
			MIN("min"),
			MAX("max");

			final Codec<DensityFunctionTypes.class_7055> codec = DensityFunctionTypes.method_41068(
				(densityFunction, densityFunction2) -> DensityFunctionTypes.class_7055.method_41097(this, densityFunction, densityFunction2),
				DensityFunctionTypes.class_7055::argument1,
				DensityFunctionTypes.class_7055::argument2
			);
			private final String field_37112;

			private class_6918(String string2) {
				this.field_37112 = string2;
			}

			@Override
			public String asString() {
				return this.field_37112;
			}
		}
	}
}
