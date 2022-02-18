package net.minecraft;

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
import net.minecraft.world.gen.noise.NoiseType;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import org.slf4j.Logger;

public final class class_6916 {
	private static final Codec<NoiseType> field_37062 = Registry.field_37230.getCodec().dispatch(NoiseType::method_41062, Function.identity());
	protected static final double field_37060 = 1000000.0;
	static final Codec<Double> field_37063 = Codec.doubleRange(-1000000.0, 1000000.0);
	public static final Codec<NoiseType> field_37061 = Codec.either(field_37063, field_37062)
		.xmap(
			either -> either.map(class_6916::method_40480, Function.identity()),
			noiseType -> noiseType instanceof class_6916.class_6923 lv ? Either.left(lv.value()) : Either.right(noiseType)
		);

	public static Codec<? extends NoiseType> method_41066(Registry<Codec<? extends NoiseType>> registry) {
		method_41067(registry, "blend_alpha", class_6916.class_6919.field_37079);
		method_41067(registry, "blend_offset", class_6916.class_6921.field_37081);
		method_41067(registry, "beardifier", class_6916.class_7049.field_37078);
		method_41067(registry, "old_blended_noise", InterpolatedNoiseSampler.field_37206);

		for (class_6916.class_6927.class_6928 lv : class_6916.class_6927.class_6928.values()) {
			method_41067(registry, lv.asString(), lv.field_37089);
		}

		method_41067(registry, "noise", class_6916.class_6931.field_37091);
		method_41067(registry, "end_islands", class_6916.class_6924.field_37085);
		method_41067(registry, "weird_scaled_sampler", class_6916.class_6944.field_37064);
		method_41067(registry, "shifted_noise", class_6916.class_6940.field_37097);
		method_41067(registry, "range_choice", class_6916.class_6933.field_37093);
		method_41067(registry, "shift_a", class_6916.class_6937.field_37095);
		method_41067(registry, "shift_b", class_6916.class_6938.field_37096);
		method_41067(registry, "shift", class_6916.class_6934.field_37094);
		method_41067(registry, "blend_density", class_6916.class_6920.field_37080);
		method_41067(registry, "clamp", class_6916.class_6922.field_37082);

		for (class_6916.class_6925.class_6926 lv2 : class_6916.class_6925.class_6926.values()) {
			method_41067(registry, lv2.asString(), lv2.field_37087);
		}

		method_41067(registry, "slide", class_6916.class_6941.field_37099);

		for (class_6916.class_7055.class_6918 lv3 : class_6916.class_7055.class_6918.values()) {
			method_41067(registry, lv3.asString(), lv3.field_37111);
		}

		method_41067(registry, "terrain_shaper_spline", class_6916.class_6942.field_37100);
		method_41067(registry, "constant", class_6916.class_6923.field_37084);
		return method_41067(registry, "y_clamped_gradient", class_6916.class_6945.field_37074);
	}

	private static Codec<? extends NoiseType> method_41067(Registry<Codec<? extends NoiseType>> registry, String string, Codec<? extends NoiseType> codec) {
		return Registry.register(registry, string, codec);
	}

	static <A, O> Codec<O> method_41064(Codec<A> codec, Function<A, O> function, Function<O, A> function2) {
		return codec.fieldOf("argument").xmap(function, function2).codec();
	}

	static <O> Codec<O> method_41069(Function<NoiseType, O> function, Function<O, NoiseType> function2) {
		return method_41064(NoiseType.field_37059, function, function2);
	}

	static <O> Codec<O> method_41068(BiFunction<NoiseType, NoiseType, O> biFunction, Function<O, NoiseType> function, Function<O, NoiseType> function2) {
		return RecordCodecBuilder.create(
			instance -> instance.group(NoiseType.field_37059.fieldOf("argument1").forGetter(function), NoiseType.field_37059.fieldOf("argument2").forGetter(function2))
					.apply(instance, biFunction)
		);
	}

	static <O> Codec<O> method_41065(MapCodec<O> mapCodec) {
		return mapCodec.codec();
	}

	private class_6916() {
	}

	public static NoiseType method_40483(NoiseType noiseType) {
		return new class_6916.class_6927(class_6916.class_6927.class_6928.INTERPOLATED, noiseType);
	}

	public static NoiseType method_40499(NoiseType noiseType) {
		return new class_6916.class_6927(class_6916.class_6927.class_6928.FLAT_CACHE, noiseType);
	}

	public static NoiseType method_40504(NoiseType noiseType) {
		return new class_6916.class_6927(class_6916.class_6927.class_6928.CACHE2D, noiseType);
	}

	public static NoiseType method_40507(NoiseType noiseType) {
		return new class_6916.class_6927(class_6916.class_6927.class_6928.CACHE_ONCE, noiseType);
	}

	public static NoiseType method_40510(NoiseType noiseType) {
		return new class_6916.class_6927(class_6916.class_6927.class_6928.CACHE_ALL_IN_CELL, noiseType);
	}

	public static NoiseType method_40496(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry, @Deprecated double d, double e, double f, double g) {
		return method_40484(new class_6916.class_6931(registryEntry, null, d, e), f, g);
	}

	public static NoiseType method_40497(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry, double d, double e, double f) {
		return method_40496(registryEntry, 1.0, d, e, f);
	}

	public static NoiseType method_40495(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry, double d, double e) {
		return method_40496(registryEntry, 1.0, 1.0, d, e);
	}

	public static NoiseType method_40487(
		NoiseType noiseType, NoiseType noiseType2, double d, RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry
	) {
		return new class_6916.class_6940(noiseType, method_40479(), noiseType2, d, 0.0, registryEntry, null);
	}

	public static NoiseType method_40493(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry) {
		return method_40502(registryEntry, 1.0, 1.0);
	}

	public static NoiseType method_40502(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry, double d, double e) {
		return new class_6916.class_6931(registryEntry, null, d, e);
	}

	public static NoiseType method_40494(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry, double d) {
		return method_40502(registryEntry, 1.0, d);
	}

	public static NoiseType method_40485(NoiseType noiseType, double d, double e, NoiseType noiseType2, NoiseType noiseType3) {
		return new class_6916.class_6933(noiseType, d, e, noiseType2, noiseType3);
	}

	public static NoiseType method_40501(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry) {
		return new class_6916.class_6937(registryEntry, null);
	}

	public static NoiseType method_40506(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry) {
		return new class_6916.class_6938(registryEntry, null);
	}

	public static NoiseType method_40509(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry) {
		return new class_6916.class_6934(registryEntry, null);
	}

	public static NoiseType method_40512(NoiseType noiseType) {
		return new class_6916.class_6920(noiseType);
	}

	public static NoiseType method_40482(long l) {
		return new class_6916.class_6924(l);
	}

	public static NoiseType method_40491(
		NoiseType noiseType, RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry, class_6916.class_6944.class_7048 arg
	) {
		return new class_6916.class_6944(noiseType, registryEntry, null, arg);
	}

	public static NoiseType method_40492(GenerationShapeConfig generationShapeConfig, NoiseType noiseType) {
		return new class_6916.class_6941(generationShapeConfig, noiseType);
	}

	public static NoiseType method_40486(NoiseType noiseType, NoiseType noiseType2) {
		return class_6916.class_7055.method_41097(class_6916.class_7055.class_6918.ADD, noiseType, noiseType2);
	}

	public static NoiseType method_40500(NoiseType noiseType, NoiseType noiseType2) {
		return class_6916.class_7055.method_41097(class_6916.class_7055.class_6918.MUL, noiseType, noiseType2);
	}

	public static NoiseType method_40505(NoiseType noiseType, NoiseType noiseType2) {
		return class_6916.class_7055.method_41097(class_6916.class_7055.class_6918.MIN, noiseType, noiseType2);
	}

	public static NoiseType method_40508(NoiseType noiseType, NoiseType noiseType2) {
		return class_6916.class_7055.method_41097(class_6916.class_7055.class_6918.MAX, noiseType, noiseType2);
	}

	public static NoiseType method_40489(NoiseType noiseType, NoiseType noiseType2, NoiseType noiseType3, class_6916.class_6942.class_7054 arg, double d, double e) {
		return new class_6916.class_6942(noiseType, noiseType2, noiseType3, null, arg, d, e);
	}

	public static NoiseType method_40479() {
		return class_6916.class_6923.field_36553;
	}

	public static NoiseType method_40480(double d) {
		return new class_6916.class_6923(d);
	}

	public static NoiseType method_40481(int i, int j, double d, double e) {
		return new class_6916.class_6945(i, j, d, e);
	}

	public static NoiseType method_40490(NoiseType noiseType, class_6916.class_6925.class_6926 arg) {
		return class_6916.class_6925.method_41079(arg, noiseType);
	}

	private static NoiseType method_40484(NoiseType noiseType, double d, double e) {
		double f = (d + e) * 0.5;
		double g = (e - d) * 0.5;
		return method_40486(method_40480(f), method_40500(method_40480(g), noiseType));
	}

	public static NoiseType method_40498() {
		return class_6916.class_6919.INSTANCE;
	}

	public static NoiseType method_40503() {
		return class_6916.class_6921.INSTANCE;
	}

	public static NoiseType method_40488(NoiseType noiseType, NoiseType noiseType2, NoiseType noiseType3) {
		NoiseType noiseType4 = method_40507(noiseType);
		NoiseType noiseType5 = method_40486(method_40500(noiseType4, method_40480(-1.0)), method_40480(1.0));
		return method_40486(method_40500(noiseType2, noiseType5), method_40500(noiseType3, noiseType4));
	}

	static record class_6917(class_6916.class_7055.class_6918 type, NoiseType argument1, NoiseType argument2, double minValue, double maxValue)
		implements class_6916.class_7055 {
		@Override
		public double sample(NoiseType.NoisePos pos) {
			double d = this.argument1.sample(pos);

			return switch (this.type) {
				case ADD -> d + this.argument2.sample(pos);
				case MAX -> d > this.argument2.maxValue() ? d : Math.max(d, this.argument2.sample(pos));
				case MIN -> d < this.argument2.minValue() ? d : Math.min(d, this.argument2.sample(pos));
				case MUL -> d == 0.0 ? 0.0 : d * this.argument2.sample(pos);
			};
		}

		@Override
		public void method_40470(double[] ds, NoiseType.class_6911 arg) {
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
		public NoiseType method_40469(NoiseType.class_6915 arg) {
			return (NoiseType)arg.apply(class_6916.class_7055.method_41097(this.type, this.argument1.method_40469(arg), this.argument2.method_40469(arg)));
		}
	}

	protected static enum class_6919 implements NoiseType.class_6913 {
		INSTANCE;

		public static final Codec<NoiseType> field_37079 = Codec.unit(INSTANCE);

		@Override
		public double sample(NoiseType.NoisePos pos) {
			return 1.0;
		}

		@Override
		public void method_40470(double[] ds, NoiseType.class_6911 arg) {
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
		public Codec<? extends NoiseType> method_41062() {
			return field_37079;
		}
	}

	static record class_6920(NoiseType input) implements class_6916.class_6943 {
		static final Codec<class_6916.class_6920> field_37080 = class_6916.method_41069(class_6916.class_6920::new, class_6916.class_6920::input);

		@Override
		public double method_40518(NoiseType.NoisePos noisePos, double d) {
			return noisePos.getBlender().method_39338(noisePos, d);
		}

		@Override
		public NoiseType method_40469(NoiseType.class_6915 arg) {
			return (NoiseType)arg.apply(new class_6916.class_6920(this.input.method_40469(arg)));
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
		public Codec<? extends NoiseType> method_41062() {
			return field_37080;
		}
	}

	protected static enum class_6921 implements NoiseType.class_6913 {
		INSTANCE;

		public static final Codec<NoiseType> field_37081 = Codec.unit(INSTANCE);

		@Override
		public double sample(NoiseType.NoisePos pos) {
			return 0.0;
		}

		@Override
		public void method_40470(double[] ds, NoiseType.class_6911 arg) {
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
		public Codec<? extends NoiseType> method_41062() {
			return field_37081;
		}
	}

	protected static record class_6922(NoiseType input, double minValue, double maxValue) implements class_6916.class_6932 {
		private static final MapCodec<class_6916.class_6922> field_37083 = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						NoiseType.field_37057.fieldOf("input").forGetter(class_6916.class_6922::input),
						class_6916.field_37063.fieldOf("min").forGetter(class_6916.class_6922::minValue),
						class_6916.field_37063.fieldOf("max").forGetter(class_6916.class_6922::maxValue)
					)
					.apply(instance, class_6916.class_6922::new)
		);
		public static final Codec<class_6916.class_6922> field_37082 = class_6916.method_41065(field_37083);

		@Override
		public double method_40520(double d) {
			return MathHelper.clamp(d, this.minValue, this.maxValue);
		}

		@Override
		public NoiseType method_40469(NoiseType.class_6915 arg) {
			return new class_6916.class_6922(this.input.method_40469(arg), this.minValue, this.maxValue);
		}

		@Override
		public Codec<? extends NoiseType> method_41062() {
			return field_37082;
		}
	}

	static record class_6923(double value) implements NoiseType.class_6913 {
		static final Codec<class_6916.class_6923> field_37084 = class_6916.method_41064(
			class_6916.field_37063, class_6916.class_6923::new, class_6916.class_6923::value
		);
		static final class_6916.class_6923 field_36553 = new class_6916.class_6923(0.0);

		@Override
		public double sample(NoiseType.NoisePos pos) {
			return this.value;
		}

		@Override
		public void method_40470(double[] ds, NoiseType.class_6911 arg) {
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
		public Codec<? extends NoiseType> method_41062() {
			return field_37084;
		}
	}

	protected static final class class_6924 implements NoiseType.class_6913 {
		public static final Codec<class_6916.class_6924> field_37085 = Codec.unit(new class_6916.class_6924(0L));
		final SimplexNoiseSampler field_36554;

		public class_6924(long l) {
			AbstractRandom abstractRandom = new AtomicSimpleRandom(l);
			abstractRandom.skip(17292);
			this.field_36554 = new SimplexNoiseSampler(abstractRandom);
		}

		@Override
		public double sample(NoiseType.NoisePos pos) {
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
		public Codec<? extends NoiseType> method_41062() {
			return field_37085;
		}
	}

	protected static record class_6925(class_6916.class_6925.class_6926 type, NoiseType input, double minValue, double maxValue) implements class_6916.class_6932 {
		public static class_6916.class_6925 method_41079(class_6916.class_6925.class_6926 arg, NoiseType noiseType) {
			double d = noiseType.minValue();
			double e = method_40521(arg, d);
			double f = method_40521(arg, noiseType.maxValue());
			return arg != class_6916.class_6925.class_6926.ABS && arg != class_6916.class_6925.class_6926.SQUARE
				? new class_6916.class_6925(arg, noiseType, e, f)
				: new class_6916.class_6925(arg, noiseType, Math.max(0.0, d), Math.max(e, f));
		}

		private static double method_40521(class_6916.class_6925.class_6926 arg, double d) {
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
		public double method_40520(double d) {
			return method_40521(this.type, d);
		}

		public class_6916.class_6925 method_40469(NoiseType.class_6915 arg) {
			return method_41079(this.type, this.input.method_40469(arg));
		}

		@Override
		public Codec<? extends NoiseType> method_41062() {
			return this.type.field_37087;
		}

		static enum class_6926 implements StringIdentifiable {
			ABS("abs"),
			SQUARE("square"),
			CUBE("cube"),
			HALF_NEGATIVE("half_negative"),
			QUARTER_NEGATIVE("quarter_negative"),
			SQUEEZE("squeeze");

			private final String field_37086;
			final Codec<class_6916.class_6925> field_37087 = class_6916.method_41069(
				noiseType -> class_6916.class_6925.method_41079(this, noiseType), class_6916.class_6925::input
			);

			private class_6926(String string2) {
				this.field_37086 = string2;
			}

			@Override
			public String asString() {
				return this.field_37086;
			}
		}
	}

	protected static record class_6927(class_6916.class_6927.class_6928 type, NoiseType wrapped) implements class_6916.class_7052 {
		@Override
		public double sample(NoiseType.NoisePos pos) {
			return this.wrapped.sample(pos);
		}

		@Override
		public void method_40470(double[] ds, NoiseType.class_6911 arg) {
			this.wrapped.method_40470(ds, arg);
		}

		@Override
		public NoiseType method_40469(NoiseType.class_6915 arg) {
			return (NoiseType)arg.apply(new class_6916.class_6927(this.type, this.wrapped.method_40469(arg)));
		}

		@Override
		public double minValue() {
			return this.wrapped.minValue();
		}

		@Override
		public double maxValue() {
			return this.wrapped.maxValue();
		}

		static enum class_6928 implements StringIdentifiable {
			INTERPOLATED("interpolated"),
			FLAT_CACHE("flat_cache"),
			CACHE2D("cache_2d"),
			CACHE_ONCE("cache_once"),
			CACHE_ALL_IN_CELL("cache_all_in_cell");

			private final String field_37088;
			final Codec<class_6916.class_7052> field_37089 = class_6916.method_41069(
				noiseType -> new class_6916.class_6927(this, noiseType), class_6916.class_7052::wrapped
			);

			private class_6928(String string2) {
				this.field_37088 = string2;
			}

			@Override
			public String asString() {
				return this.field_37088;
			}
		}
	}

	static record class_6929(class_6916.class_6929.class_6930 specificType, NoiseType input, double minValue, double maxValue, double argument)
		implements class_6916.class_6932,
		class_6916.class_7055 {
		@Override
		public class_6916.class_7055.class_6918 type() {
			return this.specificType == class_6916.class_6929.class_6930.MUL ? class_6916.class_7055.class_6918.MUL : class_6916.class_7055.class_6918.ADD;
		}

		@Override
		public NoiseType argument1() {
			return class_6916.method_40480(this.argument);
		}

		@Override
		public NoiseType argument2() {
			return this.input;
		}

		@Override
		public double method_40520(double d) {
			return switch (this.specificType) {
				case MUL -> d * this.argument;
				case ADD -> d + this.argument;
			};
		}

		@Override
		public NoiseType method_40469(NoiseType.class_6915 arg) {
			NoiseType noiseType = this.input.method_40469(arg);
			double d = noiseType.minValue();
			double e = noiseType.maxValue();
			double f;
			double g;
			if (this.specificType == class_6916.class_6929.class_6930.ADD) {
				f = d + this.argument;
				g = e + this.argument;
			} else if (this.argument >= 0.0) {
				f = d * this.argument;
				g = e * this.argument;
			} else {
				f = e * this.argument;
				g = d * this.argument;
			}

			return new class_6916.class_6929(this.specificType, noiseType, f, g, this.argument);
		}

		static enum class_6930 {
			MUL,
			ADD;
		}
	}

	protected static record class_6931(
		RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Nullable DoublePerlinNoiseSampler noise, @Deprecated double xzScale, double yScale
	) implements NoiseType.class_6913 {
		public static final MapCodec<class_6916.class_6931> field_37090 = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("noise").forGetter(class_6916.class_6931::noiseData),
						Codec.DOUBLE.fieldOf("xz_scale").forGetter(class_6916.class_6931::xzScale),
						Codec.DOUBLE.fieldOf("y_scale").forGetter(class_6916.class_6931::yScale)
					)
					.apply(instance, class_6916.class_6931::method_41084)
		);
		public static final Codec<class_6916.class_6931> field_37091 = class_6916.method_41065(field_37090);

		public static class_6916.class_6931 method_41084(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry, @Deprecated double d, double e) {
			return new class_6916.class_6931(registryEntry, null, d, e);
		}

		@Override
		public double sample(NoiseType.NoisePos pos) {
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
		public Codec<? extends NoiseType> method_41062() {
			return field_37091;
		}
	}

	interface class_6932 extends NoiseType {
		NoiseType input();

		@Override
		default double sample(NoiseType.NoisePos pos) {
			return this.method_40520(this.input().sample(pos));
		}

		@Override
		default void method_40470(double[] ds, NoiseType.class_6911 arg) {
			this.input().method_40470(ds, arg);

			for (int i = 0; i < ds.length; i++) {
				ds[i] = this.method_40520(ds[i]);
			}
		}

		double method_40520(double d);
	}

	static record class_6933(NoiseType input, double minInclusive, double maxExclusive, NoiseType whenInRange, NoiseType whenOutOfRange) implements NoiseType {
		public static final MapCodec<class_6916.class_6933> field_37092 = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						NoiseType.field_37059.fieldOf("input").forGetter(class_6916.class_6933::input),
						class_6916.field_37063.fieldOf("min_inclusive").forGetter(class_6916.class_6933::minInclusive),
						class_6916.field_37063.fieldOf("max_exclusive").forGetter(class_6916.class_6933::maxExclusive),
						NoiseType.field_37059.fieldOf("when_in_range").forGetter(class_6916.class_6933::whenInRange),
						NoiseType.field_37059.fieldOf("when_out_of_range").forGetter(class_6916.class_6933::whenOutOfRange)
					)
					.apply(instance, class_6916.class_6933::new)
		);
		public static final Codec<class_6916.class_6933> field_37093 = class_6916.method_41065(field_37092);

		@Override
		public double sample(NoiseType.NoisePos pos) {
			double d = this.input.sample(pos);
			return d >= this.minInclusive && d < this.maxExclusive ? this.whenInRange.sample(pos) : this.whenOutOfRange.sample(pos);
		}

		@Override
		public void method_40470(double[] ds, NoiseType.class_6911 arg) {
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
		public NoiseType method_40469(NoiseType.class_6915 arg) {
			return (NoiseType)arg.apply(
				new class_6916.class_6933(
					this.input.method_40469(arg), this.minInclusive, this.maxExclusive, this.whenInRange.method_40469(arg), this.whenOutOfRange.method_40469(arg)
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
		public Codec<? extends NoiseType> method_41062() {
			return field_37093;
		}
	}

	static record class_6934(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Nullable DoublePerlinNoiseSampler offsetNoise)
		implements class_6916.class_6939 {
		static final Codec<class_6916.class_6934> field_37094 = class_6916.method_41064(
			DoublePerlinNoiseSampler.NoiseParameters.CODEC, registryEntry -> new class_6916.class_6934(registryEntry, null), class_6916.class_6934::noiseData
		);

		@Override
		public double sample(NoiseType.NoisePos pos) {
			return this.method_40525((double)pos.blockX(), (double)pos.blockY(), (double)pos.blockZ());
		}

		@Override
		public class_6916.class_6939 method_41086(DoublePerlinNoiseSampler doublePerlinNoiseSampler) {
			return new class_6916.class_6934(this.noiseData, doublePerlinNoiseSampler);
		}

		@Override
		public Codec<? extends NoiseType> method_41062() {
			return field_37094;
		}
	}

	protected static record class_6937(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Nullable DoublePerlinNoiseSampler offsetNoise)
		implements class_6916.class_6939 {
		static final Codec<class_6916.class_6937> field_37095 = class_6916.method_41064(
			DoublePerlinNoiseSampler.NoiseParameters.CODEC, registryEntry -> new class_6916.class_6937(registryEntry, null), class_6916.class_6937::noiseData
		);

		@Override
		public double sample(NoiseType.NoisePos pos) {
			return this.method_40525((double)pos.blockX(), 0.0, (double)pos.blockZ());
		}

		@Override
		public class_6916.class_6939 method_41086(DoublePerlinNoiseSampler doublePerlinNoiseSampler) {
			return new class_6916.class_6937(this.noiseData, doublePerlinNoiseSampler);
		}

		@Override
		public Codec<? extends NoiseType> method_41062() {
			return field_37095;
		}
	}

	protected static record class_6938(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Nullable DoublePerlinNoiseSampler offsetNoise)
		implements class_6916.class_6939 {
		static final Codec<class_6916.class_6938> field_37096 = class_6916.method_41064(
			DoublePerlinNoiseSampler.NoiseParameters.CODEC, registryEntry -> new class_6916.class_6938(registryEntry, null), class_6916.class_6938::noiseData
		);

		@Override
		public double sample(NoiseType.NoisePos pos) {
			return this.method_40525((double)pos.blockZ(), (double)pos.blockX(), 0.0);
		}

		@Override
		public class_6916.class_6939 method_41086(DoublePerlinNoiseSampler doublePerlinNoiseSampler) {
			return new class_6916.class_6938(this.noiseData, doublePerlinNoiseSampler);
		}

		@Override
		public Codec<? extends NoiseType> method_41062() {
			return field_37096;
		}
	}

	interface class_6939 extends NoiseType.class_6913 {
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

		class_6916.class_6939 method_41086(DoublePerlinNoiseSampler doublePerlinNoiseSampler);
	}

	protected static record class_6940(
		NoiseType shiftX,
		NoiseType shiftY,
		NoiseType shiftZ,
		double xzScale,
		double yScale,
		RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData,
		@Nullable DoublePerlinNoiseSampler noise
	) implements NoiseType {
		private static final MapCodec<class_6916.class_6940> field_37098 = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						NoiseType.field_37059.fieldOf("shift_x").forGetter(class_6916.class_6940::shiftX),
						NoiseType.field_37059.fieldOf("shift_y").forGetter(class_6916.class_6940::shiftY),
						NoiseType.field_37059.fieldOf("shift_z").forGetter(class_6916.class_6940::shiftZ),
						Codec.DOUBLE.fieldOf("xz_scale").forGetter(class_6916.class_6940::xzScale),
						Codec.DOUBLE.fieldOf("y_scale").forGetter(class_6916.class_6940::yScale),
						DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("noise").forGetter(class_6916.class_6940::noiseData)
					)
					.apply(instance, class_6916.class_6940::method_41091)
		);
		public static final Codec<class_6916.class_6940> field_37097 = class_6916.method_41065(field_37098);

		public static class_6916.class_6940 method_41091(
			NoiseType noiseType, NoiseType noiseType2, NoiseType noiseType3, double d, double e, RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry
		) {
			return new class_6916.class_6940(noiseType, noiseType2, noiseType3, d, e, registryEntry, null);
		}

		@Override
		public double sample(NoiseType.NoisePos pos) {
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
		public void method_40470(double[] ds, NoiseType.class_6911 arg) {
			arg.method_40478(ds, this);
		}

		@Override
		public NoiseType method_40469(NoiseType.class_6915 arg) {
			return (NoiseType)arg.apply(
				new class_6916.class_6940(
					this.shiftX.method_40469(arg), this.shiftY.method_40469(arg), this.shiftZ.method_40469(arg), this.xzScale, this.yScale, this.noiseData, this.noise
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
		public Codec<? extends NoiseType> method_41062() {
			return field_37097;
		}
	}

	protected static record class_6941(@Nullable GenerationShapeConfig settings, NoiseType input) implements class_6916.class_6943 {
		public static final Codec<class_6916.class_6941> field_37099 = class_6916.method_41069(
			noiseType -> new class_6916.class_6941(null, noiseType), class_6916.class_6941::input
		);

		@Override
		public double method_40518(NoiseType.NoisePos noisePos, double d) {
			return this.settings == null ? d : class_6954.method_40542(this.settings, d, (double)noisePos.blockY());
		}

		@Override
		public NoiseType method_40469(NoiseType.class_6915 arg) {
			return (NoiseType)arg.apply(new class_6916.class_6941(this.settings, this.input.method_40469(arg)));
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
		public Codec<? extends NoiseType> method_41062() {
			return field_37099;
		}
	}

	public static record class_6942(
		NoiseType continentalness,
		NoiseType erosion,
		NoiseType weirdness,
		@Nullable VanillaTerrainParameters shaper,
		class_6916.class_6942.class_7054 spline,
		double minValue,
		double maxValue
	) implements NoiseType {
		private static final MapCodec<class_6916.class_6942> field_37101 = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						NoiseType.field_37059.fieldOf("continentalness").forGetter(class_6916.class_6942::continentalness),
						NoiseType.field_37059.fieldOf("erosion").forGetter(class_6916.class_6942::erosion),
						NoiseType.field_37059.fieldOf("weirdness").forGetter(class_6916.class_6942::weirdness),
						class_6916.class_6942.class_7054.field_37105.fieldOf("spline").forGetter(class_6916.class_6942::spline),
						class_6916.field_37063.fieldOf("min_value").forGetter(class_6916.class_6942::minValue),
						class_6916.field_37063.fieldOf("max_value").forGetter(class_6916.class_6942::maxValue)
					)
					.apply(instance, class_6916.class_6942::method_41094)
		);
		public static final Codec<class_6916.class_6942> field_37100 = class_6916.method_41065(field_37101);

		public static class_6916.class_6942 method_41094(
			NoiseType noiseType, NoiseType noiseType2, NoiseType noiseType3, class_6916.class_6942.class_7054 arg, double d, double e
		) {
			return new class_6916.class_6942(noiseType, noiseType2, noiseType3, null, arg, d, e);
		}

		@Override
		public double sample(NoiseType.NoisePos pos) {
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
		public void method_40470(double[] ds, NoiseType.class_6911 arg) {
			for (int i = 0; i < ds.length; i++) {
				ds[i] = this.sample(arg.method_40477(i));
			}
		}

		@Override
		public NoiseType method_40469(NoiseType.class_6915 arg) {
			return (NoiseType)arg.apply(
				new class_6916.class_6942(
					this.continentalness.method_40469(arg),
					this.erosion.method_40469(arg),
					this.weirdness.method_40469(arg),
					this.shaper,
					this.spline,
					this.minValue,
					this.maxValue
				)
			);
		}

		@Override
		public Codec<? extends NoiseType> method_41062() {
			return field_37100;
		}

		interface class_7053 {
			float apply(VanillaTerrainParameters vanillaTerrainParameters, VanillaTerrainParameters.NoisePoint noisePoint);
		}

		public static enum class_7054 implements StringIdentifiable {
			OFFSET("offset", VanillaTerrainParameters::getOffset),
			FACTOR("factor", VanillaTerrainParameters::getFactor),
			JAGGEDNESS("jaggedness", VanillaTerrainParameters::getPeak);

			private static final Map<String, class_6916.class_6942.class_7054> field_37106 = (Map<String, class_6916.class_6942.class_7054>)Arrays.stream(values())
				.collect(Collectors.toMap(class_6916.class_6942.class_7054::asString, arg -> arg));
			public static final Codec<class_6916.class_6942.class_7054> field_37105 = StringIdentifiable.createCodec(
				class_6916.class_6942.class_7054::values, field_37106::get
			);
			private final String field_37107;
			final class_6916.class_6942.class_7053 field_37108;

			private class_7054(String string2, class_6916.class_6942.class_7053 arg) {
				this.field_37107 = string2;
				this.field_37108 = arg;
			}

			@Override
			public String asString() {
				return this.field_37107;
			}
		}
	}

	interface class_6943 extends NoiseType {
		NoiseType input();

		@Override
		default double sample(NoiseType.NoisePos pos) {
			return this.method_40518(pos, this.input().sample(pos));
		}

		@Override
		default void method_40470(double[] ds, NoiseType.class_6911 arg) {
			this.input().method_40470(ds, arg);

			for (int i = 0; i < ds.length; i++) {
				ds[i] = this.method_40518(arg.method_40477(i), ds[i]);
			}
		}

		double method_40518(NoiseType.NoisePos noisePos, double d);
	}

	protected static record class_6944(
		NoiseType input,
		RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData,
		@Nullable DoublePerlinNoiseSampler noise,
		class_6916.class_6944.class_7048 rarityValueMapper
	) implements class_6916.class_6943 {
		private static final MapCodec<class_6916.class_6944> field_37065 = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						NoiseType.field_37059.fieldOf("input").forGetter(class_6916.class_6944::input),
						DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("noise").forGetter(class_6916.class_6944::noiseData),
						class_6916.class_6944.class_7048.field_37068.fieldOf("rarity_value_mapper").forGetter(class_6916.class_6944::rarityValueMapper)
					)
					.apply(instance, class_6916.class_6944::method_41073)
		);
		public static final Codec<class_6916.class_6944> field_37064 = class_6916.method_41065(field_37065);

		public static class_6916.class_6944 method_41073(
			NoiseType noiseType, RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry, class_6916.class_6944.class_7048 arg
		) {
			return new class_6916.class_6944(noiseType, registryEntry, null, arg);
		}

		@Override
		public double method_40518(NoiseType.NoisePos noisePos, double d) {
			if (this.noise == null) {
				return 0.0;
			} else {
				double e = this.rarityValueMapper.field_37071.get(d);
				return e * Math.abs(this.noise.sample((double)noisePos.blockX() / e, (double)noisePos.blockY() / e, (double)noisePos.blockZ() / e));
			}
		}

		@Override
		public NoiseType method_40469(NoiseType.class_6915 arg) {
			this.input.method_40469(arg);
			return (NoiseType)arg.apply(new class_6916.class_6944(this.input.method_40469(arg), this.noiseData, this.noise, this.rarityValueMapper));
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
		public Codec<? extends NoiseType> method_41062() {
			return field_37064;
		}

		public static enum class_7048 implements StringIdentifiable {
			TYPE1("type_1", class_6954.CaveScaler::scaleTunnels, 2.0),
			TYPE2("type_2", class_6954.CaveScaler::scaleCaves, 3.0);

			private static final Map<String, class_6916.class_6944.class_7048> field_37069 = (Map<String, class_6916.class_6944.class_7048>)Arrays.stream(values())
				.collect(Collectors.toMap(class_6916.class_6944.class_7048::asString, arg -> arg));
			public static final Codec<class_6916.class_6944.class_7048> field_37068 = StringIdentifiable.createCodec(
				class_6916.class_6944.class_7048::values, field_37069::get
			);
			private final String field_37070;
			final Double2DoubleFunction field_37071;
			final double field_37072;

			private class_7048(String string2, Double2DoubleFunction double2DoubleFunction, double d) {
				this.field_37070 = string2;
				this.field_37071 = double2DoubleFunction;
				this.field_37072 = d;
			}

			@Override
			public String asString() {
				return this.field_37070;
			}
		}
	}

	static record class_6945(int fromY, int toY, double fromValue, double toValue) implements NoiseType.class_6913 {
		private static final MapCodec<class_6916.class_6945> field_37075 = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Codec.intRange(DimensionType.MIN_HEIGHT * 2, DimensionType.MAX_COLUMN_HEIGHT * 2).fieldOf("from_y").forGetter(class_6916.class_6945::fromY),
						Codec.intRange(DimensionType.MIN_HEIGHT * 2, DimensionType.MAX_COLUMN_HEIGHT * 2).fieldOf("to_y").forGetter(class_6916.class_6945::toY),
						class_6916.field_37063.fieldOf("from_value").forGetter(class_6916.class_6945::fromValue),
						class_6916.field_37063.fieldOf("to_value").forGetter(class_6916.class_6945::toValue)
					)
					.apply(instance, class_6916.class_6945::new)
		);
		public static final Codec<class_6916.class_6945> field_37074 = class_6916.method_41065(field_37075);

		@Override
		public double sample(NoiseType.NoisePos pos) {
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
		public Codec<? extends NoiseType> method_41062() {
			return field_37074;
		}
	}

	protected static enum class_7049 implements class_6916.class_7050 {
		INSTANCE;

		@Override
		public double sample(NoiseType.NoisePos pos) {
			return 0.0;
		}

		@Override
		public void method_40470(double[] ds, NoiseType.class_6911 arg) {
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

	public interface class_7050 extends NoiseType.class_6913 {
		Codec<NoiseType> field_37078 = Codec.unit(class_6916.class_7049.INSTANCE);

		@Override
		default Codec<? extends NoiseType> method_41062() {
			return field_37078;
		}
	}

	protected static record class_7051(RegistryEntry<NoiseType> function) implements NoiseType {
		@Override
		public double sample(NoiseType.NoisePos pos) {
			return this.function.value().sample(pos);
		}

		@Override
		public void method_40470(double[] ds, NoiseType.class_6911 arg) {
			this.function.value().method_40470(ds, arg);
		}

		@Override
		public NoiseType method_40469(NoiseType.class_6915 arg) {
			return (NoiseType)arg.apply(new class_6916.class_7051(new RegistryEntry.Direct<>(this.function.value().method_40469(arg))));
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
		public Codec<? extends NoiseType> method_41062() {
			throw new UnsupportedOperationException("Calling .codec() on HolderHolder");
		}
	}

	public interface class_7052 extends NoiseType {
		class_6916.class_6927.class_6928 type();

		NoiseType wrapped();

		@Override
		default Codec<? extends NoiseType> method_41062() {
			return this.type().field_37089;
		}
	}

	interface class_7055 extends NoiseType {
		Logger field_37110 = LogUtils.getLogger();

		static class_6916.class_7055 method_41097(class_6916.class_7055.class_6918 arg, NoiseType noiseType, NoiseType noiseType2) {
			double d = noiseType.minValue();
			double e = noiseType2.minValue();
			double f = noiseType.maxValue();
			double g = noiseType2.maxValue();
			if (arg == class_6916.class_7055.class_6918.MIN || arg == class_6916.class_7055.class_6918.MAX) {
				boolean bl = d >= g;
				boolean bl2 = e >= f;
				if (bl || bl2) {
					field_37110.warn("Creating a " + arg + " function between two non-overlapping inputs: " + noiseType + " and " + noiseType2);
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
			if (arg == class_6916.class_7055.class_6918.MUL || arg == class_6916.class_7055.class_6918.ADD) {
				if (noiseType instanceof class_6916.class_6923 lv) {
					return new class_6916.class_6929(
						arg == class_6916.class_7055.class_6918.ADD ? class_6916.class_6929.class_6930.ADD : class_6916.class_6929.class_6930.MUL, noiseType2, h, i, lv.value
					);
				}

				if (noiseType2 instanceof class_6916.class_6923 lv) {
					return new class_6916.class_6929(
						arg == class_6916.class_7055.class_6918.ADD ? class_6916.class_6929.class_6930.ADD : class_6916.class_6929.class_6930.MUL, noiseType, h, i, lv.value
					);
				}
			}

			return new class_6916.class_6917(arg, noiseType, noiseType2, h, i);
		}

		class_6916.class_7055.class_6918 type();

		NoiseType argument1();

		NoiseType argument2();

		@Override
		default Codec<? extends NoiseType> method_41062() {
			return this.type().field_37111;
		}

		public static enum class_6918 implements StringIdentifiable {
			ADD("add"),
			MUL("mul"),
			MIN("min"),
			MAX("max");

			final Codec<class_6916.class_7055> field_37111 = class_6916.method_41068(
				(noiseType, noiseType2) -> class_6916.class_7055.method_41097(this, noiseType, noiseType2),
				class_6916.class_7055::argument1,
				class_6916.class_7055::argument2
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
