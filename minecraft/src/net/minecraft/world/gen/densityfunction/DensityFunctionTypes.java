package net.minecraft.world.gen.densityfunction;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.function.ToFloatFunction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.math.random.AtomicSimpleRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;
import org.slf4j.Logger;

public final class DensityFunctionTypes {
	private static final Codec<DensityFunction> DYNAMIC_RANGE = Registry.DENSITY_FUNCTION_TYPE
		.getCodec()
		.dispatch(densityFunction -> densityFunction.getCodec().codec(), Function.identity());
	protected static final double field_37060 = 1000000.0;
	static final Codec<Double> CONSTANT_RANGE = Codec.doubleRange(-1000000.0, 1000000.0);
	public static final Codec<DensityFunction> CODEC = Codec.either(CONSTANT_RANGE, DYNAMIC_RANGE)
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

		for (DensityFunctionTypes.class_6925.Type type2 : DensityFunctionTypes.class_6925.Type.values()) {
			register(registry, type2.asString(), type2.codec);
		}

		for (DensityFunctionTypes.Operation.Type type3 : DensityFunctionTypes.Operation.Type.values()) {
			register(registry, type3.asString(), type3.codec);
		}

		register(registry, "spline", DensityFunctionTypes.Spline.CODEC);
		register(registry, "constant", DensityFunctionTypes.Constant.CODEC);
		return register(registry, "y_clamped_gradient", DensityFunctionTypes.YClampedGradient.CODEC);
	}

	private static Codec<? extends DensityFunction> register(
		Registry<Codec<? extends DensityFunction>> registry, String id, CodecHolder<? extends DensityFunction> codecHolder
	) {
		return Registry.register(registry, id, codecHolder.codec());
	}

	static <A, O> CodecHolder<O> method_41064(Codec<A> codec, Function<A, O> function, Function<O, A> function2) {
		return CodecHolder.of(codec.fieldOf("argument").xmap(function, function2));
	}

	static <O> CodecHolder<O> method_41069(Function<DensityFunction, O> function, Function<O, DensityFunction> function2) {
		return method_41064(DensityFunction.FUNCTION_CODEC, function, function2);
	}

	static <O> CodecHolder<O> method_41068(
		BiFunction<DensityFunction, DensityFunction, O> biFunction, Function<O, DensityFunction> function, Function<O, DensityFunction> function2
	) {
		return CodecHolder.of(
			RecordCodecBuilder.mapCodec(
				instance -> instance.group(
							DensityFunction.FUNCTION_CODEC.fieldOf("argument1").forGetter(function), DensityFunction.FUNCTION_CODEC.fieldOf("argument2").forGetter(function2)
						)
						.apply(instance, biFunction)
			)
		);
	}

	static <O> CodecHolder<O> method_41065(MapCodec<O> mapCodec) {
		return CodecHolder.of(mapCodec);
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
		RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, @Deprecated double xzScale, double yScale, double d, double e
	) {
		return method_40484(new DensityFunctionTypes.Noise(new DensityFunction.class_7270(noiseParameters), xzScale, yScale), d, e);
	}

	public static DensityFunction noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, double yScale, double d, double e) {
		return noise(noiseParameters, 1.0, yScale, d, e);
	}

	public static DensityFunction noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, double d, double e) {
		return noise(noiseParameters, 1.0, 1.0, d, e);
	}

	public static DensityFunction shiftedNoise(
		DensityFunction densityFunction, DensityFunction densityFunction2, double d, RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters
	) {
		return new DensityFunctionTypes.ShiftedNoise(densityFunction, zero(), densityFunction2, d, 0.0, new DensityFunction.class_7270(noiseParameters));
	}

	public static DensityFunction noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters) {
		return method_40502(noiseParameters, 1.0, 1.0);
	}

	public static DensityFunction method_40502(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, double xzScale, double yScale) {
		return new DensityFunctionTypes.Noise(new DensityFunction.class_7270(noiseParameters), xzScale, yScale);
	}

	public static DensityFunction noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, double yScale) {
		return method_40502(noiseParameters, 1.0, yScale);
	}

	public static DensityFunction rangeChoice(
		DensityFunction densityFunction, double d, double e, DensityFunction densityFunction2, DensityFunction densityFunction3
	) {
		return new DensityFunctionTypes.RangeChoice(densityFunction, d, e, densityFunction2, densityFunction3);
	}

	public static DensityFunction shiftA(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters) {
		return new DensityFunctionTypes.ShiftA(new DensityFunction.class_7270(noiseParameters));
	}

	public static DensityFunction shiftB(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters) {
		return new DensityFunctionTypes.ShiftB(new DensityFunction.class_7270(noiseParameters));
	}

	public static DensityFunction shift(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters) {
		return new DensityFunctionTypes.Shift(new DensityFunction.class_7270(noiseParameters));
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
		return new DensityFunctionTypes.WeirdScaledSampler(densityFunction, new DensityFunction.class_7270(registryEntry), rarityValueMapper);
	}

	public static DensityFunction add(DensityFunction densityFunction, DensityFunction densityFunction2) {
		return DensityFunctionTypes.Operation.create(DensityFunctionTypes.Operation.Type.ADD, densityFunction, densityFunction2);
	}

	public static DensityFunction mul(DensityFunction densityFunction, DensityFunction densityFunction2) {
		return DensityFunctionTypes.Operation.create(DensityFunctionTypes.Operation.Type.MUL, densityFunction, densityFunction2);
	}

	public static DensityFunction min(DensityFunction densityFunction, DensityFunction densityFunction2) {
		return DensityFunctionTypes.Operation.create(DensityFunctionTypes.Operation.Type.MIN, densityFunction, densityFunction2);
	}

	public static DensityFunction max(DensityFunction densityFunction, DensityFunction densityFunction2) {
		return DensityFunctionTypes.Operation.create(DensityFunctionTypes.Operation.Type.MAX, densityFunction, densityFunction2);
	}

	public static DensityFunction spline(net.minecraft.util.math.Spline<DensityFunctionTypes.Spline.class_7136, DensityFunctionTypes.Spline.class_7135> spline) {
		return new DensityFunctionTypes.Spline(spline);
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

	public static DensityFunction method_40490(DensityFunction densityFunction, DensityFunctionTypes.class_6925.Type type) {
		return DensityFunctionTypes.class_6925.method_41079(type, densityFunction);
	}

	private static DensityFunction method_40484(DensityFunction densityFunction, double d, double e) {
		double f = (d + e) * 0.5;
		double g = (e - d) * 0.5;
		return add(constant(f), mul(constant(g), densityFunction));
	}

	public static DensityFunction blendAlpha() {
		return DensityFunctionTypes.BlendAlpha.INSTANCE;
	}

	public static DensityFunction blendOffset() {
		return DensityFunctionTypes.BlendOffset.INSTANCE;
	}

	public static DensityFunction method_40488(DensityFunction densityFunction, DensityFunction densityFunction2, DensityFunction densityFunction3) {
		if (densityFunction2 instanceof DensityFunctionTypes.Constant constant) {
			return method_42359(densityFunction, constant.value, densityFunction3);
		} else {
			DensityFunction densityFunction4 = cacheOnce(densityFunction);
			DensityFunction densityFunction5 = add(mul(densityFunction4, constant(-1.0)), constant(1.0));
			return add(mul(densityFunction2, densityFunction5), mul(densityFunction3, densityFunction4));
		}
	}

	public static DensityFunction method_42359(DensityFunction densityFunction, double d, DensityFunction densityFunction2) {
		return add(mul(densityFunction, add(densityFunction2, constant(-d))), constant(d));
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

		public static final CodecHolder<DensityFunction> CODEC = CodecHolder.of(MapCodec.unit(INSTANCE));

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
		public CodecHolder<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	static record BlendDensity(DensityFunction input) implements DensityFunctionTypes.class_6943 {
		static final CodecHolder<DensityFunctionTypes.BlendDensity> CODEC = DensityFunctionTypes.method_41069(
			DensityFunctionTypes.BlendDensity::new, DensityFunctionTypes.BlendDensity::input
		);

		@Override
		public double method_40518(DensityFunction.NoisePos noisePos, double d) {
			return noisePos.getBlender().method_39338(noisePos, d);
		}

		@Override
		public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
			return visitor.apply(new DensityFunctionTypes.BlendDensity(this.input.apply(visitor)));
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
		public CodecHolder<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	protected static enum BlendOffset implements DensityFunction.class_6913 {
		INSTANCE;

		public static final CodecHolder<DensityFunction> CODEC = CodecHolder.of(MapCodec.unit(INSTANCE));

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
		public CodecHolder<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	protected static record Clamp(DensityFunction input, double minValue, double maxValue) implements DensityFunctionTypes.class_6932 {
		private static final MapCodec<DensityFunctionTypes.Clamp> field_37083 = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						DensityFunction.CODEC.fieldOf("input").forGetter(DensityFunctionTypes.Clamp::input),
						DensityFunctionTypes.CONSTANT_RANGE.fieldOf("min").forGetter(DensityFunctionTypes.Clamp::minValue),
						DensityFunctionTypes.CONSTANT_RANGE.fieldOf("max").forGetter(DensityFunctionTypes.Clamp::maxValue)
					)
					.apply(instance, DensityFunctionTypes.Clamp::new)
		);
		public static final CodecHolder<DensityFunctionTypes.Clamp> CODEC = DensityFunctionTypes.method_41065(field_37083);

		@Override
		public double apply(double density) {
			return MathHelper.clamp(density, this.minValue, this.maxValue);
		}

		@Override
		public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
			return new DensityFunctionTypes.Clamp(this.input.apply(visitor), this.minValue, this.maxValue);
		}

		@Override
		public CodecHolder<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	static record Constant(double value) implements DensityFunction.class_6913 {
		static final CodecHolder<DensityFunctionTypes.Constant> CODEC = DensityFunctionTypes.method_41064(
			DensityFunctionTypes.CONSTANT_RANGE, DensityFunctionTypes.Constant::new, DensityFunctionTypes.Constant::value
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
		public CodecHolder<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	protected static final class EndIslands implements DensityFunction.class_6913 {
		public static final CodecHolder<DensityFunctionTypes.EndIslands> CODEC = CodecHolder.of(MapCodec.unit(new DensityFunctionTypes.EndIslands(0L)));
		private static final float field_37677 = -0.9F;
		private final SimplexNoiseSampler field_36554;

		public EndIslands(long seed) {
			AbstractRandom abstractRandom = new AtomicSimpleRandom(seed);
			abstractRandom.skip(17292);
			this.field_36554 = new SimplexNoiseSampler(abstractRandom);
		}

		private static float method_41529(SimplexNoiseSampler simplexNoiseSampler, int i, int j) {
			int k = i / 2;
			int l = j / 2;
			int m = i % 2;
			int n = j % 2;
			float f = 100.0F - MathHelper.sqrt((float)(i * i + j * j)) * 8.0F;
			f = MathHelper.clamp(f, -100.0F, 80.0F);

			for (int o = -12; o <= 12; o++) {
				for (int p = -12; p <= 12; p++) {
					long q = (long)(k + o);
					long r = (long)(l + p);
					if (q * q + r * r > 4096L && simplexNoiseSampler.sample((double)q, (double)r) < -0.9F) {
						float g = (MathHelper.abs((float)q) * 3439.0F + MathHelper.abs((float)r) * 147.0F) % 13.0F + 9.0F;
						float h = (float)(m - o * 2);
						float s = (float)(n - p * 2);
						float t = 100.0F - MathHelper.sqrt(h * h + s * s) * g;
						t = MathHelper.clamp(t, -100.0F, 80.0F);
						f = Math.max(f, t);
					}
				}
			}

			return f;
		}

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return ((double)method_41529(this.field_36554, pos.blockX() / 8, pos.blockZ() / 8) - 8.0) / 128.0;
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
		public CodecHolder<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	protected static record Noise(DensityFunction.class_7270 noise, @Deprecated double xzScale, double yScale) implements DensityFunction {
		public static final MapCodec<DensityFunctionTypes.Noise> field_37090 = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						DensityFunction.class_7270.field_38248.fieldOf("noise").forGetter(DensityFunctionTypes.Noise::noise),
						Codec.DOUBLE.fieldOf("xz_scale").forGetter(DensityFunctionTypes.Noise::xzScale),
						Codec.DOUBLE.fieldOf("y_scale").forGetter(DensityFunctionTypes.Noise::yScale)
					)
					.apply(instance, DensityFunctionTypes.Noise::new)
		);
		public static final CodecHolder<DensityFunctionTypes.Noise> CODEC = DensityFunctionTypes.method_41065(field_37090);

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return this.noise.method_42356((double)pos.blockX() * this.xzScale, (double)pos.blockY() * this.yScale, (double)pos.blockZ() * this.xzScale);
		}

		@Override
		public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			arg.method_40478(ds, this);
		}

		@Override
		public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
			return visitor.apply(new DensityFunctionTypes.Noise(visitor.method_42358(this.noise), this.xzScale, this.yScale));
		}

		@Override
		public double minValue() {
			return -this.maxValue();
		}

		@Override
		public double maxValue() {
			return this.noise.method_42355();
		}

		@Override
		public CodecHolder<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	interface Operation extends DensityFunction {
		Logger LOGGER = LogUtils.getLogger();

		static DensityFunctionTypes.Operation create(DensityFunctionTypes.Operation.Type type, DensityFunction argument1, DensityFunction argument2) {
			double d = argument1.minValue();
			double e = argument2.minValue();
			double f = argument1.maxValue();
			double g = argument2.maxValue();
			if (type == DensityFunctionTypes.Operation.Type.MIN || type == DensityFunctionTypes.Operation.Type.MAX) {
				boolean bl = d >= g;
				boolean bl2 = e >= f;
				if (bl || bl2) {
					LOGGER.warn("Creating a " + type + " function between two non-overlapping inputs: " + argument1 + " and " + argument2);
				}
			}
			double h = switch (type) {
				case ADD -> d + e;
				case MAX -> Math.max(d, e);
				case MIN -> Math.min(d, e);
				case MUL -> d > 0.0 && e > 0.0 ? d * e : (f < 0.0 && g < 0.0 ? f * g : Math.min(d * g, f * e));
			};

			double i = switch (type) {
				case ADD -> f + g;
				case MAX -> Math.max(f, g);
				case MIN -> Math.min(f, g);
				case MUL -> d > 0.0 && e > 0.0 ? f * g : (f < 0.0 && g < 0.0 ? d * e : Math.max(d * e, f * g));
			};
			if (type == DensityFunctionTypes.Operation.Type.MUL || type == DensityFunctionTypes.Operation.Type.ADD) {
				if (argument1 instanceof DensityFunctionTypes.Constant constant) {
					return new DensityFunctionTypes.class_6929(
						type == DensityFunctionTypes.Operation.Type.ADD ? DensityFunctionTypes.class_6929.SpecificType.ADD : DensityFunctionTypes.class_6929.SpecificType.MUL,
						argument2,
						h,
						i,
						constant.value
					);
				}

				if (argument2 instanceof DensityFunctionTypes.Constant constant) {
					return new DensityFunctionTypes.class_6929(
						type == DensityFunctionTypes.Operation.Type.ADD ? DensityFunctionTypes.class_6929.SpecificType.ADD : DensityFunctionTypes.class_6929.SpecificType.MUL,
						argument1,
						h,
						i,
						constant.value
					);
				}
			}

			return new DensityFunctionTypes.class_6917(type, argument1, argument2, h, i);
		}

		DensityFunctionTypes.Operation.Type type();

		DensityFunction argument1();

		DensityFunction argument2();

		@Override
		default CodecHolder<? extends DensityFunction> getCodec() {
			return this.type().codec;
		}

		public static enum Type implements StringIdentifiable {
			ADD("add"),
			MUL("mul"),
			MIN("min"),
			MAX("max");

			final CodecHolder<DensityFunctionTypes.Operation> codec = DensityFunctionTypes.method_41068(
				(densityFunction, densityFunction2) -> DensityFunctionTypes.Operation.create(this, densityFunction, densityFunction2),
				DensityFunctionTypes.Operation::argument1,
				DensityFunctionTypes.Operation::argument2
			);
			private final String name;

			private Type(String name) {
				this.name = name;
			}

			@Override
			public String asString() {
				return this.name;
			}
		}
	}

	static record RangeChoice(DensityFunction input, double minInclusive, double maxExclusive, DensityFunction whenInRange, DensityFunction whenOutOfRange)
		implements DensityFunction {
		public static final MapCodec<DensityFunctionTypes.RangeChoice> field_37092 = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						DensityFunction.FUNCTION_CODEC.fieldOf("input").forGetter(DensityFunctionTypes.RangeChoice::input),
						DensityFunctionTypes.CONSTANT_RANGE.fieldOf("min_inclusive").forGetter(DensityFunctionTypes.RangeChoice::minInclusive),
						DensityFunctionTypes.CONSTANT_RANGE.fieldOf("max_exclusive").forGetter(DensityFunctionTypes.RangeChoice::maxExclusive),
						DensityFunction.FUNCTION_CODEC.fieldOf("when_in_range").forGetter(DensityFunctionTypes.RangeChoice::whenInRange),
						DensityFunction.FUNCTION_CODEC.fieldOf("when_out_of_range").forGetter(DensityFunctionTypes.RangeChoice::whenOutOfRange)
					)
					.apply(instance, DensityFunctionTypes.RangeChoice::new)
		);
		public static final CodecHolder<DensityFunctionTypes.RangeChoice> CODEC = DensityFunctionTypes.method_41065(field_37092);

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
		public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
			return visitor.apply(
				new DensityFunctionTypes.RangeChoice(
					this.input.apply(visitor), this.minInclusive, this.maxExclusive, this.whenInRange.apply(visitor), this.whenOutOfRange.apply(visitor)
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
		public CodecHolder<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	@Debug
	public static record RegistryEntryHolder(RegistryEntry<DensityFunction> function) implements DensityFunction {
		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return this.function.value().sample(pos);
		}

		@Override
		public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			this.function.value().method_40470(ds, arg);
		}

		@Override
		public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
			return visitor.apply(new DensityFunctionTypes.RegistryEntryHolder(new RegistryEntry.Direct<>(this.function.value().apply(visitor))));
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
		public CodecHolder<? extends DensityFunction> getCodec() {
			throw new UnsupportedOperationException("Calling .codec() on HolderHolder");
		}
	}

	protected static record Shift(DensityFunction.class_7270 offsetNoise) implements DensityFunctionTypes.class_6939 {
		static final CodecHolder<DensityFunctionTypes.Shift> CODEC = DensityFunctionTypes.method_41064(
			DensityFunction.class_7270.field_38248, DensityFunctionTypes.Shift::new, DensityFunctionTypes.Shift::offsetNoise
		);

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return this.method_40525((double)pos.blockX(), (double)pos.blockY(), (double)pos.blockZ());
		}

		@Override
		public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
			return visitor.apply(new DensityFunctionTypes.Shift(visitor.method_42358(this.offsetNoise)));
		}

		@Override
		public CodecHolder<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	protected static record ShiftA(DensityFunction.class_7270 offsetNoise) implements DensityFunctionTypes.class_6939 {
		static final CodecHolder<DensityFunctionTypes.ShiftA> CODEC = DensityFunctionTypes.method_41064(
			DensityFunction.class_7270.field_38248, DensityFunctionTypes.ShiftA::new, DensityFunctionTypes.ShiftA::offsetNoise
		);

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return this.method_40525((double)pos.blockX(), 0.0, (double)pos.blockZ());
		}

		@Override
		public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
			return visitor.apply(new DensityFunctionTypes.ShiftA(visitor.method_42358(this.offsetNoise)));
		}

		@Override
		public CodecHolder<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	protected static record ShiftB(DensityFunction.class_7270 offsetNoise) implements DensityFunctionTypes.class_6939 {
		static final CodecHolder<DensityFunctionTypes.ShiftB> CODEC = DensityFunctionTypes.method_41064(
			DensityFunction.class_7270.field_38248, DensityFunctionTypes.ShiftB::new, DensityFunctionTypes.ShiftB::offsetNoise
		);

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return this.method_40525((double)pos.blockZ(), (double)pos.blockX(), 0.0);
		}

		@Override
		public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
			return visitor.apply(new DensityFunctionTypes.ShiftB(visitor.method_42358(this.offsetNoise)));
		}

		@Override
		public CodecHolder<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	protected static record ShiftedNoise(
		DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ, double xzScale, double yScale, DensityFunction.class_7270 noise
	) implements DensityFunction {
		private static final MapCodec<DensityFunctionTypes.ShiftedNoise> field_37098 = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						DensityFunction.FUNCTION_CODEC.fieldOf("shift_x").forGetter(DensityFunctionTypes.ShiftedNoise::shiftX),
						DensityFunction.FUNCTION_CODEC.fieldOf("shift_y").forGetter(DensityFunctionTypes.ShiftedNoise::shiftY),
						DensityFunction.FUNCTION_CODEC.fieldOf("shift_z").forGetter(DensityFunctionTypes.ShiftedNoise::shiftZ),
						Codec.DOUBLE.fieldOf("xz_scale").forGetter(DensityFunctionTypes.ShiftedNoise::xzScale),
						Codec.DOUBLE.fieldOf("y_scale").forGetter(DensityFunctionTypes.ShiftedNoise::yScale),
						DensityFunction.class_7270.field_38248.fieldOf("noise").forGetter(DensityFunctionTypes.ShiftedNoise::noise)
					)
					.apply(instance, DensityFunctionTypes.ShiftedNoise::new)
		);
		public static final CodecHolder<DensityFunctionTypes.ShiftedNoise> CODEC = DensityFunctionTypes.method_41065(field_37098);

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			double d = (double)pos.blockX() * this.xzScale + this.shiftX.sample(pos);
			double e = (double)pos.blockY() * this.yScale + this.shiftY.sample(pos);
			double f = (double)pos.blockZ() * this.xzScale + this.shiftZ.sample(pos);
			return this.noise.method_42356(d, e, f);
		}

		@Override
		public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			arg.method_40478(ds, this);
		}

		@Override
		public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
			return visitor.apply(
				new DensityFunctionTypes.ShiftedNoise(
					this.shiftX.apply(visitor), this.shiftY.apply(visitor), this.shiftZ.apply(visitor), this.xzScale, this.yScale, visitor.method_42358(this.noise)
				)
			);
		}

		@Override
		public double minValue() {
			return -this.maxValue();
		}

		@Override
		public double maxValue() {
			return this.noise.method_42355();
		}

		@Override
		public CodecHolder<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	public static record Spline(net.minecraft.util.math.Spline<DensityFunctionTypes.Spline.class_7136, DensityFunctionTypes.Spline.class_7135> spline)
		implements DensityFunction {
		private static final Codec<net.minecraft.util.math.Spline<DensityFunctionTypes.Spline.class_7136, DensityFunctionTypes.Spline.class_7135>> field_37678 = net.minecraft.util.math.Spline.createCodec(
			DensityFunctionTypes.Spline.class_7135.field_37679
		);
		private static final MapCodec<DensityFunctionTypes.Spline> field_37256 = field_37678.fieldOf("spline")
			.xmap(DensityFunctionTypes.Spline::new, DensityFunctionTypes.Spline::spline);
		public static final CodecHolder<DensityFunctionTypes.Spline> CODEC = DensityFunctionTypes.method_41065(field_37256);

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return (double)this.spline.apply(new DensityFunctionTypes.Spline.class_7136(pos));
		}

		@Override
		public double minValue() {
			return (double)this.spline.min();
		}

		@Override
		public double maxValue() {
			return (double)this.spline.max();
		}

		@Override
		public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			arg.method_40478(ds, this);
		}

		@Override
		public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
			return visitor.apply(new DensityFunctionTypes.Spline(this.spline.method_41187(arg -> arg.method_41530(visitor))));
		}

		@Override
		public CodecHolder<? extends DensityFunction> getCodec() {
			return CODEC;
		}

		public static record class_7135(RegistryEntry<DensityFunction> function) implements ToFloatFunction<DensityFunctionTypes.Spline.class_7136> {
			public static final Codec<DensityFunctionTypes.Spline.class_7135> field_37679 = DensityFunction.REGISTRY_ENTRY_CODEC
				.xmap(DensityFunctionTypes.Spline.class_7135::new, DensityFunctionTypes.Spline.class_7135::function);

			public String toString() {
				Optional<RegistryKey<DensityFunction>> optional = this.function.getKey();
				if (optional.isPresent()) {
					RegistryKey<DensityFunction> registryKey = (RegistryKey<DensityFunction>)optional.get();
					if (registryKey == DensityFunctions.CONTINENTS_OVERWORLD) {
						return "continents";
					}

					if (registryKey == DensityFunctions.EROSION_OVERWORLD) {
						return "erosion";
					}

					if (registryKey == DensityFunctions.RIDGES_OVERWORLD) {
						return "weirdness";
					}

					if (registryKey == DensityFunctions.RIDGES_FOLDED_OVERWORLD) {
						return "ridges";
					}
				}

				return "Coordinate[" + this.function + "]";
			}

			public float apply(DensityFunctionTypes.Spline.class_7136 arg) {
				return (float)this.function.value().sample(arg.context());
			}

			@Override
			public float min() {
				return (float)this.function.value().minValue();
			}

			@Override
			public float max() {
				return (float)this.function.value().maxValue();
			}

			public DensityFunctionTypes.Spline.class_7135 method_41530(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
				return new DensityFunctionTypes.Spline.class_7135(new RegistryEntry.Direct<>(this.function.value().apply(densityFunctionVisitor)));
			}
		}

		public static record class_7136(DensityFunction.NoisePos context) {
		}
	}

	protected static record WeirdScaledSampler(
		DensityFunction input, DensityFunction.class_7270 noise, DensityFunctionTypes.WeirdScaledSampler.RarityValueMapper rarityValueMapper
	) implements DensityFunctionTypes.class_6943 {
		private static final MapCodec<DensityFunctionTypes.WeirdScaledSampler> field_37065 = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						DensityFunction.FUNCTION_CODEC.fieldOf("input").forGetter(DensityFunctionTypes.WeirdScaledSampler::input),
						DensityFunction.class_7270.field_38248.fieldOf("noise").forGetter(DensityFunctionTypes.WeirdScaledSampler::noise),
						DensityFunctionTypes.WeirdScaledSampler.RarityValueMapper.CODEC
							.fieldOf("rarity_value_mapper")
							.forGetter(DensityFunctionTypes.WeirdScaledSampler::rarityValueMapper)
					)
					.apply(instance, DensityFunctionTypes.WeirdScaledSampler::new)
		);
		public static final CodecHolder<DensityFunctionTypes.WeirdScaledSampler> CODEC = DensityFunctionTypes.method_41065(field_37065);

		@Override
		public double method_40518(DensityFunction.NoisePos noisePos, double d) {
			double e = this.rarityValueMapper.scaleFunction.get(d);
			return e * Math.abs(this.noise.method_42356((double)noisePos.blockX() / e, (double)noisePos.blockY() / e, (double)noisePos.blockZ() / e));
		}

		@Override
		public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
			return visitor.apply(new DensityFunctionTypes.WeirdScaledSampler(this.input.apply(visitor), visitor.method_42358(this.noise), this.rarityValueMapper));
		}

		@Override
		public double minValue() {
			return 0.0;
		}

		@Override
		public double maxValue() {
			return this.rarityValueMapper.field_37072 * this.noise.method_42355();
		}

		@Override
		public CodecHolder<? extends DensityFunction> getCodec() {
			return CODEC;
		}

		public static enum RarityValueMapper implements StringIdentifiable {
			TYPE1("type_1", DensityFunctions.CaveScaler::scaleTunnels, 2.0),
			TYPE2("type_2", DensityFunctions.CaveScaler::scaleCaves, 3.0);

			public static final com.mojang.serialization.Codec<DensityFunctionTypes.WeirdScaledSampler.RarityValueMapper> CODEC = StringIdentifiable.createCodec(
				DensityFunctionTypes.WeirdScaledSampler.RarityValueMapper::values
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
		default CodecHolder<? extends DensityFunction> getCodec() {
			return this.type().codec;
		}

		@Override
		default DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
			return visitor.apply(new DensityFunctionTypes.class_6927(this.type(), this.wrapped().apply(visitor)));
		}
	}

	static record YClampedGradient(int fromY, int toY, double fromValue, double toValue) implements DensityFunction.class_6913 {
		private static final MapCodec<DensityFunctionTypes.YClampedGradient> field_37075 = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Codec.intRange(DimensionType.MIN_HEIGHT * 2, DimensionType.MAX_COLUMN_HEIGHT * 2)
							.fieldOf("from_y")
							.forGetter(DensityFunctionTypes.YClampedGradient::fromY),
						Codec.intRange(DimensionType.MIN_HEIGHT * 2, DimensionType.MAX_COLUMN_HEIGHT * 2).fieldOf("to_y").forGetter(DensityFunctionTypes.YClampedGradient::toY),
						DensityFunctionTypes.CONSTANT_RANGE.fieldOf("from_value").forGetter(DensityFunctionTypes.YClampedGradient::fromValue),
						DensityFunctionTypes.CONSTANT_RANGE.fieldOf("to_value").forGetter(DensityFunctionTypes.YClampedGradient::toValue)
					)
					.apply(instance, DensityFunctionTypes.YClampedGradient::new)
		);
		public static final CodecHolder<DensityFunctionTypes.YClampedGradient> CODEC = DensityFunctionTypes.method_41065(field_37075);

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
		public CodecHolder<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}

	static record class_6917(DensityFunctionTypes.Operation.Type type, DensityFunction argument1, DensityFunction argument2, double minValue, double maxValue)
		implements DensityFunctionTypes.Operation {
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
		public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
			return visitor.apply(DensityFunctionTypes.Operation.create(this.type, this.argument1.apply(visitor), this.argument2.apply(visitor)));
		}
	}

	protected static record class_6925(DensityFunctionTypes.class_6925.Type type, DensityFunction input, double minValue, double maxValue)
		implements DensityFunctionTypes.class_6932 {
		public static DensityFunctionTypes.class_6925 method_41079(DensityFunctionTypes.class_6925.Type type, DensityFunction densityFunction) {
			double d = densityFunction.minValue();
			double e = method_40521(type, d);
			double f = method_40521(type, densityFunction.maxValue());
			return type != DensityFunctionTypes.class_6925.Type.ABS && type != DensityFunctionTypes.class_6925.Type.SQUARE
				? new DensityFunctionTypes.class_6925(type, densityFunction, e, f)
				: new DensityFunctionTypes.class_6925(type, densityFunction, Math.max(0.0, d), Math.max(e, f));
		}

		private static double method_40521(DensityFunctionTypes.class_6925.Type type, double d) {
			return switch (type) {
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
		public double apply(double density) {
			return method_40521(this.type, density);
		}

		public DensityFunctionTypes.class_6925 apply(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
			return method_41079(this.type, this.input.apply(densityFunctionVisitor));
		}

		@Override
		public CodecHolder<? extends DensityFunction> getCodec() {
			return this.type.codec;
		}

		static enum Type implements StringIdentifiable {
			ABS("abs"),
			SQUARE("square"),
			CUBE("cube"),
			HALF_NEGATIVE("half_negative"),
			QUARTER_NEGATIVE("quarter_negative"),
			SQUEEZE("squeeze");

			private final String name;
			final CodecHolder<DensityFunctionTypes.class_6925> codec = DensityFunctionTypes.method_41069(
				densityFunction -> DensityFunctionTypes.class_6925.method_41079(this, densityFunction), DensityFunctionTypes.class_6925::input
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
			final CodecHolder<DensityFunctionTypes.Wrapper> codec = DensityFunctionTypes.method_41069(
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

	static record class_6929(DensityFunctionTypes.class_6929.SpecificType specificType, DensityFunction input, double minValue, double maxValue, double argument)
		implements DensityFunctionTypes.class_6932,
		DensityFunctionTypes.Operation {
		@Override
		public DensityFunctionTypes.Operation.Type type() {
			return this.specificType == DensityFunctionTypes.class_6929.SpecificType.MUL
				? DensityFunctionTypes.Operation.Type.MUL
				: DensityFunctionTypes.Operation.Type.ADD;
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
		public double apply(double density) {
			return switch (this.specificType) {
				case MUL -> density * this.argument;
				case ADD -> density + this.argument;
			};
		}

		@Override
		public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
			DensityFunction densityFunction = this.input.apply(visitor);
			double d = densityFunction.minValue();
			double e = densityFunction.maxValue();
			double f;
			double g;
			if (this.specificType == DensityFunctionTypes.class_6929.SpecificType.ADD) {
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

		static enum SpecificType {
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

		double apply(double density);
	}

	interface class_6939 extends DensityFunction {
		DensityFunction.class_7270 offsetNoise();

		@Override
		default double minValue() {
			return -this.maxValue();
		}

		@Override
		default double maxValue() {
			return this.offsetNoise().method_42355() * 4.0;
		}

		default double method_40525(double d, double e, double f) {
			return this.offsetNoise().method_42356(d * 0.25, e * 0.25, f * 0.25) * 4.0;
		}

		@Override
		default void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			arg.method_40478(ds, this);
		}
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
		CodecHolder<DensityFunction> CODEC = CodecHolder.of(MapCodec.unit(DensityFunctionTypes.Beardifier.INSTANCE));

		@Override
		default CodecHolder<? extends DensityFunction> getCodec() {
			return CODEC;
		}
	}
}
