/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.densityfunction;

import com.mojang.datafixers.kinds.Applicative;
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
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctions;
import org.slf4j.Logger;

public final class DensityFunctionTypes {
    private static final Codec<DensityFunction> DYNAMIC_RANGE = Registry.DENSITY_FUNCTION_TYPE.getCodec().dispatch(densityFunction -> densityFunction.getCodec().codec(), Function.identity());
    protected static final double field_37060 = 1000000.0;
    static final Codec<Double> CONSTANT_RANGE = Codec.doubleRange(-1000000.0, 1000000.0);
    public static final Codec<DensityFunction> CODEC = Codec.either(CONSTANT_RANGE, DYNAMIC_RANGE).xmap(either -> either.map(DensityFunctionTypes::constant, Function.identity()), densityFunction -> {
        if (densityFunction instanceof Constant) {
            Constant constant = (Constant)densityFunction;
            return Either.left(constant.value());
        }
        return Either.right(densityFunction);
    });

    public static Codec<? extends DensityFunction> registerAndGetDefault(Registry<Codec<? extends DensityFunction>> registry) {
        DensityFunctionTypes.register(registry, "blend_alpha", BlendAlpha.CODEC);
        DensityFunctionTypes.register(registry, "blend_offset", BlendOffset.CODEC);
        DensityFunctionTypes.register(registry, "beardifier", Beardifier.CODEC);
        DensityFunctionTypes.register(registry, "old_blended_noise", InterpolatedNoiseSampler.CODEC);
        for (class_6927.Type type : class_6927.Type.values()) {
            DensityFunctionTypes.register(registry, type.asString(), type.codec);
        }
        DensityFunctionTypes.register(registry, "noise", Noise.CODEC);
        DensityFunctionTypes.register(registry, "end_islands", EndIslands.CODEC);
        DensityFunctionTypes.register(registry, "weird_scaled_sampler", WeirdScaledSampler.CODEC);
        DensityFunctionTypes.register(registry, "shifted_noise", ShiftedNoise.CODEC);
        DensityFunctionTypes.register(registry, "range_choice", RangeChoice.CODEC);
        DensityFunctionTypes.register(registry, "shift_a", ShiftA.CODEC);
        DensityFunctionTypes.register(registry, "shift_b", ShiftB.CODEC);
        DensityFunctionTypes.register(registry, "shift", Shift.CODEC);
        DensityFunctionTypes.register(registry, "blend_density", BlendDensity.CODEC);
        DensityFunctionTypes.register(registry, "clamp", Clamp.CODEC);
        for (Enum enum_ : class_6925.Type.values()) {
            DensityFunctionTypes.register(registry, ((class_6925.Type)enum_).asString(), ((class_6925.Type)enum_).codec);
        }
        for (Enum enum_ : Operation.Type.values()) {
            DensityFunctionTypes.register(registry, ((Operation.Type)enum_).asString(), ((Operation.Type)enum_).codec);
        }
        DensityFunctionTypes.register(registry, "spline", Spline.CODEC);
        DensityFunctionTypes.register(registry, "constant", Constant.CODEC);
        return DensityFunctionTypes.register(registry, "y_clamped_gradient", YClampedGradient.CODEC);
    }

    private static Codec<? extends DensityFunction> register(Registry<Codec<? extends DensityFunction>> registry, String id, CodecHolder<? extends DensityFunction> codecHolder) {
        return Registry.register(registry, id, codecHolder.codec());
    }

    static <A, O> CodecHolder<O> method_41064(Codec<A> codec, Function<A, O> function, Function<O, A> function2) {
        return CodecHolder.of(((MapCodec)codec.fieldOf("argument")).xmap(function, function2));
    }

    static <O> CodecHolder<O> method_41069(Function<DensityFunction, O> function, Function<O, DensityFunction> function2) {
        return DensityFunctionTypes.method_41064(DensityFunction.FUNCTION_CODEC, function, function2);
    }

    static <O> CodecHolder<O> method_41068(BiFunction<DensityFunction, DensityFunction, O> biFunction, Function<O, DensityFunction> function, Function<O, DensityFunction> function2) {
        return CodecHolder.of(RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)DensityFunction.FUNCTION_CODEC.fieldOf("argument1")).forGetter(function), ((MapCodec)DensityFunction.FUNCTION_CODEC.fieldOf("argument2")).forGetter(function2)).apply(instance, biFunction)));
    }

    static <O> CodecHolder<O> method_41065(MapCodec<O> mapCodec) {
        return CodecHolder.of(mapCodec);
    }

    private DensityFunctionTypes() {
    }

    public static DensityFunction interpolated(DensityFunction inputFunction) {
        return new class_6927(class_6927.Type.INTERPOLATED, inputFunction);
    }

    public static DensityFunction flatCache(DensityFunction inputFunction) {
        return new class_6927(class_6927.Type.FLAT_CACHE, inputFunction);
    }

    public static DensityFunction cache2d(DensityFunction inputFunction) {
        return new class_6927(class_6927.Type.CACHE2D, inputFunction);
    }

    public static DensityFunction cacheOnce(DensityFunction inputFunction) {
        return new class_6927(class_6927.Type.CACHE_ONCE, inputFunction);
    }

    public static DensityFunction cacheAllInCell(DensityFunction inputFunction) {
        return new class_6927(class_6927.Type.CACHE_ALL_IN_CELL, inputFunction);
    }

    public static DensityFunction noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, @Deprecated double xzScale, double yScale, double d, double e) {
        return DensityFunctionTypes.method_40484(new Noise(new DensityFunction.class_7270(noiseParameters), xzScale, yScale), d, e);
    }

    public static DensityFunction noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, double yScale, double d, double e) {
        return DensityFunctionTypes.noise(noiseParameters, 1.0, yScale, d, e);
    }

    public static DensityFunction noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, double d, double e) {
        return DensityFunctionTypes.noise(noiseParameters, 1.0, 1.0, d, e);
    }

    public static DensityFunction shiftedNoise(DensityFunction densityFunction, DensityFunction densityFunction2, double d, RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters) {
        return new ShiftedNoise(densityFunction, DensityFunctionTypes.zero(), densityFunction2, d, 0.0, new DensityFunction.class_7270(noiseParameters));
    }

    public static DensityFunction noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters) {
        return DensityFunctionTypes.method_40502(noiseParameters, 1.0, 1.0);
    }

    public static DensityFunction method_40502(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, double xzScale, double yScale) {
        return new Noise(new DensityFunction.class_7270(noiseParameters), xzScale, yScale);
    }

    public static DensityFunction noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, double yScale) {
        return DensityFunctionTypes.method_40502(noiseParameters, 1.0, yScale);
    }

    public static DensityFunction rangeChoice(DensityFunction densityFunction, double d, double e, DensityFunction densityFunction2, DensityFunction densityFunction3) {
        return new RangeChoice(densityFunction, d, e, densityFunction2, densityFunction3);
    }

    public static DensityFunction shiftA(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters) {
        return new ShiftA(new DensityFunction.class_7270(noiseParameters));
    }

    public static DensityFunction shiftB(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters) {
        return new ShiftB(new DensityFunction.class_7270(noiseParameters));
    }

    public static DensityFunction shift(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters) {
        return new Shift(new DensityFunction.class_7270(noiseParameters));
    }

    public static DensityFunction blendDensity(DensityFunction densityFunction) {
        return new BlendDensity(densityFunction);
    }

    public static DensityFunction endIslands(long seed) {
        return new EndIslands(seed);
    }

    public static DensityFunction weirdScaledSampler(DensityFunction densityFunction, RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry, WeirdScaledSampler.RarityValueMapper rarityValueMapper) {
        return new WeirdScaledSampler(densityFunction, new DensityFunction.class_7270(registryEntry), rarityValueMapper);
    }

    public static DensityFunction add(DensityFunction densityFunction, DensityFunction densityFunction2) {
        return Operation.create(Operation.Type.ADD, densityFunction, densityFunction2);
    }

    public static DensityFunction mul(DensityFunction densityFunction, DensityFunction densityFunction2) {
        return Operation.create(Operation.Type.MUL, densityFunction, densityFunction2);
    }

    public static DensityFunction min(DensityFunction densityFunction, DensityFunction densityFunction2) {
        return Operation.create(Operation.Type.MIN, densityFunction, densityFunction2);
    }

    public static DensityFunction max(DensityFunction densityFunction, DensityFunction densityFunction2) {
        return Operation.create(Operation.Type.MAX, densityFunction, densityFunction2);
    }

    public static DensityFunction spline(net.minecraft.util.math.Spline<Spline.class_7136, Spline.class_7135> spline) {
        return new Spline(spline);
    }

    public static DensityFunction zero() {
        return Constant.ZERO;
    }

    public static DensityFunction constant(double density) {
        return new Constant(density);
    }

    public static DensityFunction yClampedGradient(int i, int j, double d, double e) {
        return new YClampedGradient(i, j, d, e);
    }

    public static DensityFunction method_40490(DensityFunction densityFunction, class_6925.Type type) {
        return class_6925.method_41079(type, densityFunction);
    }

    private static DensityFunction method_40484(DensityFunction densityFunction, double d, double e) {
        double f = (d + e) * 0.5;
        double g = (e - d) * 0.5;
        return DensityFunctionTypes.add(DensityFunctionTypes.constant(f), DensityFunctionTypes.mul(DensityFunctionTypes.constant(g), densityFunction));
    }

    public static DensityFunction blendAlpha() {
        return BlendAlpha.INSTANCE;
    }

    public static DensityFunction blendOffset() {
        return BlendOffset.INSTANCE;
    }

    public static DensityFunction method_40488(DensityFunction densityFunction, DensityFunction densityFunction2, DensityFunction densityFunction3) {
        if (densityFunction2 instanceof Constant) {
            Constant constant = (Constant)densityFunction2;
            return DensityFunctionTypes.method_42359(densityFunction, constant.value, densityFunction3);
        }
        DensityFunction densityFunction4 = DensityFunctionTypes.cacheOnce(densityFunction);
        DensityFunction densityFunction5 = DensityFunctionTypes.add(DensityFunctionTypes.mul(densityFunction4, DensityFunctionTypes.constant(-1.0)), DensityFunctionTypes.constant(1.0));
        return DensityFunctionTypes.add(DensityFunctionTypes.mul(densityFunction2, densityFunction5), DensityFunctionTypes.mul(densityFunction3, densityFunction4));
    }

    public static DensityFunction method_42359(DensityFunction densityFunction, double d, DensityFunction densityFunction2) {
        return DensityFunctionTypes.add(DensityFunctionTypes.mul(densityFunction, DensityFunctionTypes.add(densityFunction2, DensityFunctionTypes.constant(-d))), DensityFunctionTypes.constant(d));
    }

    protected static enum BlendAlpha implements DensityFunction.class_6913
    {
        INSTANCE;

        public static final CodecHolder<DensityFunction> CODEC;

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

        static {
            CODEC = CodecHolder.of(MapCodec.unit(INSTANCE));
        }
    }

    protected static enum BlendOffset implements DensityFunction.class_6913
    {
        INSTANCE;

        public static final CodecHolder<DensityFunction> CODEC;

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

        static {
            CODEC = CodecHolder.of(MapCodec.unit(INSTANCE));
        }
    }

    protected static enum Beardifier implements class_7050
    {
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

    protected record class_6927(Type type, DensityFunction wrapped) implements Wrapper
    {
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

        static enum Type implements StringIdentifiable
        {
            INTERPOLATED("interpolated"),
            FLAT_CACHE("flat_cache"),
            CACHE2D("cache_2d"),
            CACHE_ONCE("cache_once"),
            CACHE_ALL_IN_CELL("cache_all_in_cell");

            private final String name;
            final CodecHolder<Wrapper> codec = DensityFunctionTypes.method_41069(densityFunction -> new class_6927(this, (DensityFunction)densityFunction), Wrapper::wrapped);

            private Type(String name) {
                this.name = name;
            }

            @Override
            public String asString() {
                return this.name;
            }
        }
    }

    protected record Noise(DensityFunction.class_7270 noise, @Deprecated double xzScale, double yScale) implements DensityFunction
    {
        public static final MapCodec<Noise> field_37090 = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)DensityFunction.class_7270.field_38248.fieldOf("noise")).forGetter(Noise::noise), ((MapCodec)Codec.DOUBLE.fieldOf("xz_scale")).forGetter(Noise::xzScale), ((MapCodec)Codec.DOUBLE.fieldOf("y_scale")).forGetter(Noise::yScale)).apply((Applicative<Noise, ?>)instance, Noise::new));
        public static final CodecHolder<Noise> CODEC = DensityFunctionTypes.method_41065(field_37090);

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
            return visitor.apply(new Noise(visitor.method_42358(this.noise), this.xzScale, this.yScale));
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

        @Deprecated
        public double xzScale() {
            return this.xzScale;
        }
    }

    protected static final class EndIslands
    implements DensityFunction.class_6913 {
        public static final CodecHolder<EndIslands> CODEC = CodecHolder.of(MapCodec.unit(new EndIslands(0L)));
        private static final float field_37677 = -0.9f;
        private final SimplexNoiseSampler field_36554;

        public EndIslands(long seed) {
            CheckedRandom random = new CheckedRandom(seed);
            random.skip(17292);
            this.field_36554 = new SimplexNoiseSampler(random);
        }

        private static float method_41529(SimplexNoiseSampler simplexNoiseSampler, int i, int j) {
            int k = i / 2;
            int l = j / 2;
            int m = i % 2;
            int n = j % 2;
            float f = 100.0f - MathHelper.sqrt(i * i + j * j) * 8.0f;
            f = MathHelper.clamp(f, -100.0f, 80.0f);
            for (int o = -12; o <= 12; ++o) {
                for (int p = -12; p <= 12; ++p) {
                    long q = k + o;
                    long r = l + p;
                    if (q * q + r * r <= 4096L || !(simplexNoiseSampler.sample(q, r) < (double)-0.9f)) continue;
                    float g = (MathHelper.abs(q) * 3439.0f + MathHelper.abs(r) * 147.0f) % 13.0f + 9.0f;
                    float h = m - o * 2;
                    float s = n - p * 2;
                    float t = 100.0f - MathHelper.sqrt(h * h + s * s) * g;
                    t = MathHelper.clamp(t, -100.0f, 80.0f);
                    f = Math.max(f, t);
                }
            }
            return f;
        }

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return ((double)EndIslands.method_41529(this.field_36554, pos.blockX() / 8, pos.blockZ() / 8) - 8.0) / 128.0;
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

    protected record WeirdScaledSampler(DensityFunction input, DensityFunction.class_7270 noise, RarityValueMapper rarityValueMapper) implements class_6943
    {
        private static final MapCodec<WeirdScaledSampler> field_37065 = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)DensityFunction.FUNCTION_CODEC.fieldOf("input")).forGetter(WeirdScaledSampler::input), ((MapCodec)DensityFunction.class_7270.field_38248.fieldOf("noise")).forGetter(WeirdScaledSampler::noise), ((MapCodec)RarityValueMapper.CODEC.fieldOf("rarity_value_mapper")).forGetter(WeirdScaledSampler::rarityValueMapper)).apply((Applicative<WeirdScaledSampler, ?>)instance, WeirdScaledSampler::new));
        public static final CodecHolder<WeirdScaledSampler> CODEC = DensityFunctionTypes.method_41065(field_37065);

        @Override
        public double method_40518(DensityFunction.NoisePos noisePos, double d) {
            double e = this.rarityValueMapper.scaleFunction.get(d);
            return e * Math.abs(this.noise.method_42356((double)noisePos.blockX() / e, (double)noisePos.blockY() / e, (double)noisePos.blockZ() / e));
        }

        @Override
        public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return visitor.apply(new WeirdScaledSampler(this.input.apply(visitor), visitor.method_42358(this.noise), this.rarityValueMapper));
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

        public static enum RarityValueMapper implements StringIdentifiable
        {
            TYPE1("type_1", DensityFunctions.CaveScaler::scaleTunnels, 2.0),
            TYPE2("type_2", DensityFunctions.CaveScaler::scaleCaves, 3.0);

            public static final Codec<RarityValueMapper> CODEC;
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

            static {
                CODEC = StringIdentifiable.createCodec(RarityValueMapper::values);
            }
        }
    }

    protected record ShiftedNoise(DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ, double xzScale, double yScale, DensityFunction.class_7270 noise) implements DensityFunction
    {
        private static final MapCodec<ShiftedNoise> field_37098 = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)DensityFunction.FUNCTION_CODEC.fieldOf("shift_x")).forGetter(ShiftedNoise::shiftX), ((MapCodec)DensityFunction.FUNCTION_CODEC.fieldOf("shift_y")).forGetter(ShiftedNoise::shiftY), ((MapCodec)DensityFunction.FUNCTION_CODEC.fieldOf("shift_z")).forGetter(ShiftedNoise::shiftZ), ((MapCodec)Codec.DOUBLE.fieldOf("xz_scale")).forGetter(ShiftedNoise::xzScale), ((MapCodec)Codec.DOUBLE.fieldOf("y_scale")).forGetter(ShiftedNoise::yScale), ((MapCodec)DensityFunction.class_7270.field_38248.fieldOf("noise")).forGetter(ShiftedNoise::noise)).apply((Applicative<ShiftedNoise, ?>)instance, ShiftedNoise::new));
        public static final CodecHolder<ShiftedNoise> CODEC = DensityFunctionTypes.method_41065(field_37098);

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
            return visitor.apply(new ShiftedNoise(this.shiftX.apply(visitor), this.shiftY.apply(visitor), this.shiftZ.apply(visitor), this.xzScale, this.yScale, visitor.method_42358(this.noise)));
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

    record RangeChoice(DensityFunction input, double minInclusive, double maxExclusive, DensityFunction whenInRange, DensityFunction whenOutOfRange) implements DensityFunction
    {
        public static final MapCodec<RangeChoice> field_37092 = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)DensityFunction.FUNCTION_CODEC.fieldOf("input")).forGetter(RangeChoice::input), ((MapCodec)CONSTANT_RANGE.fieldOf("min_inclusive")).forGetter(RangeChoice::minInclusive), ((MapCodec)CONSTANT_RANGE.fieldOf("max_exclusive")).forGetter(RangeChoice::maxExclusive), ((MapCodec)DensityFunction.FUNCTION_CODEC.fieldOf("when_in_range")).forGetter(RangeChoice::whenInRange), ((MapCodec)DensityFunction.FUNCTION_CODEC.fieldOf("when_out_of_range")).forGetter(RangeChoice::whenOutOfRange)).apply((Applicative<RangeChoice, ?>)instance, RangeChoice::new));
        public static final CodecHolder<RangeChoice> CODEC = DensityFunctionTypes.method_41065(field_37092);

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            double d = this.input.sample(pos);
            if (d >= this.minInclusive && d < this.maxExclusive) {
                return this.whenInRange.sample(pos);
            }
            return this.whenOutOfRange.sample(pos);
        }

        @Override
        public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            this.input.method_40470(ds, arg);
            for (int i = 0; i < ds.length; ++i) {
                double d = ds[i];
                ds[i] = d >= this.minInclusive && d < this.maxExclusive ? this.whenInRange.sample(arg.method_40477(i)) : this.whenOutOfRange.sample(arg.method_40477(i));
            }
        }

        @Override
        public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return visitor.apply(new RangeChoice(this.input.apply(visitor), this.minInclusive, this.maxExclusive, this.whenInRange.apply(visitor), this.whenOutOfRange.apply(visitor)));
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

    protected record ShiftA(DensityFunction.class_7270 offsetNoise) implements class_6939
    {
        static final CodecHolder<ShiftA> CODEC = DensityFunctionTypes.method_41064(DensityFunction.class_7270.field_38248, ShiftA::new, ShiftA::offsetNoise);

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return this.method_40525(pos.blockX(), 0.0, pos.blockZ());
        }

        @Override
        public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return visitor.apply(new ShiftA(visitor.method_42358(this.offsetNoise)));
        }

        @Override
        public CodecHolder<? extends DensityFunction> getCodec() {
            return CODEC;
        }
    }

    protected record ShiftB(DensityFunction.class_7270 offsetNoise) implements class_6939
    {
        static final CodecHolder<ShiftB> CODEC = DensityFunctionTypes.method_41064(DensityFunction.class_7270.field_38248, ShiftB::new, ShiftB::offsetNoise);

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return this.method_40525(pos.blockZ(), pos.blockX(), 0.0);
        }

        @Override
        public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return visitor.apply(new ShiftB(visitor.method_42358(this.offsetNoise)));
        }

        @Override
        public CodecHolder<? extends DensityFunction> getCodec() {
            return CODEC;
        }
    }

    protected record Shift(DensityFunction.class_7270 offsetNoise) implements class_6939
    {
        static final CodecHolder<Shift> CODEC = DensityFunctionTypes.method_41064(DensityFunction.class_7270.field_38248, Shift::new, Shift::offsetNoise);

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return this.method_40525(pos.blockX(), pos.blockY(), pos.blockZ());
        }

        @Override
        public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return visitor.apply(new Shift(visitor.method_42358(this.offsetNoise)));
        }

        @Override
        public CodecHolder<? extends DensityFunction> getCodec() {
            return CODEC;
        }
    }

    record BlendDensity(DensityFunction input) implements class_6943
    {
        static final CodecHolder<BlendDensity> CODEC = DensityFunctionTypes.method_41069(BlendDensity::new, BlendDensity::input);

        @Override
        public double method_40518(DensityFunction.NoisePos noisePos, double d) {
            return noisePos.getBlender().method_39338(noisePos, d);
        }

        @Override
        public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return visitor.apply(new BlendDensity(this.input.apply(visitor)));
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

    protected record Clamp(DensityFunction input, double minValue, double maxValue) implements class_6932
    {
        private static final MapCodec<Clamp> field_37083 = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)DensityFunction.CODEC.fieldOf("input")).forGetter(Clamp::input), ((MapCodec)CONSTANT_RANGE.fieldOf("min")).forGetter(Clamp::minValue), ((MapCodec)CONSTANT_RANGE.fieldOf("max")).forGetter(Clamp::maxValue)).apply((Applicative<Clamp, ?>)instance, Clamp::new));
        public static final CodecHolder<Clamp> CODEC = DensityFunctionTypes.method_41065(field_37083);

        @Override
        public double apply(double density) {
            return MathHelper.clamp(density, this.minValue, this.maxValue);
        }

        @Override
        public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return new Clamp(this.input.apply(visitor), this.minValue, this.maxValue);
        }

        @Override
        public CodecHolder<? extends DensityFunction> getCodec() {
            return CODEC;
        }
    }

    protected record class_6925(Type type, DensityFunction input, double minValue, double maxValue) implements class_6932
    {
        public static class_6925 method_41079(Type type, DensityFunction densityFunction) {
            double d = densityFunction.minValue();
            double e = class_6925.method_40521(type, d);
            double f = class_6925.method_40521(type, densityFunction.maxValue());
            if (type == Type.ABS || type == Type.SQUARE) {
                return new class_6925(type, densityFunction, Math.max(0.0, d), Math.max(e, f));
            }
            return new class_6925(type, densityFunction, e, f);
        }

        private static double method_40521(Type type, double d) {
            return switch (type) {
                default -> throw new IncompatibleClassChangeError();
                case Type.ABS -> Math.abs(d);
                case Type.SQUARE -> d * d;
                case Type.CUBE -> d * d * d;
                case Type.HALF_NEGATIVE -> {
                    if (d > 0.0) {
                        yield d;
                    }
                    yield d * 0.5;
                }
                case Type.QUARTER_NEGATIVE -> {
                    if (d > 0.0) {
                        yield d;
                    }
                    yield d * 0.25;
                }
                case Type.SQUEEZE -> {
                    double e = MathHelper.clamp(d, -1.0, 1.0);
                    yield e / 2.0 - e * e * e / 24.0;
                }
            };
        }

        @Override
        public double apply(double density) {
            return class_6925.method_40521(this.type, density);
        }

        @Override
        public class_6925 apply(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
            return class_6925.method_41079(this.type, this.input.apply(densityFunctionVisitor));
        }

        @Override
        public CodecHolder<? extends DensityFunction> getCodec() {
            return this.type.codec;
        }

        @Override
        public /* synthetic */ DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return this.apply(visitor);
        }

        static enum Type implements StringIdentifiable
        {
            ABS("abs"),
            SQUARE("square"),
            CUBE("cube"),
            HALF_NEGATIVE("half_negative"),
            QUARTER_NEGATIVE("quarter_negative"),
            SQUEEZE("squeeze");

            private final String name;
            final CodecHolder<class_6925> codec = DensityFunctionTypes.method_41069(densityFunction -> class_6925.method_41079(this, densityFunction), class_6925::input);

            private Type(String name) {
                this.name = name;
            }

            @Override
            public String asString() {
                return this.name;
            }
        }
    }

    static interface Operation
    extends DensityFunction {
        public static final Logger LOGGER = LogUtils.getLogger();

        public static Operation create(Type type, DensityFunction argument1, DensityFunction argument2) {
            double i;
            double d = argument1.minValue();
            double e = argument2.minValue();
            double f = argument1.maxValue();
            double g = argument2.maxValue();
            if (type == Type.MIN || type == Type.MAX) {
                boolean bl2;
                boolean bl = d >= g;
                boolean bl3 = bl2 = e >= f;
                if (bl || bl2) {
                    LOGGER.warn("Creating a " + type + " function between two non-overlapping inputs: " + argument1 + " and " + argument2);
                }
            }
            double h = switch (type) {
                default -> throw new IncompatibleClassChangeError();
                case Type.ADD -> d + e;
                case Type.MAX -> Math.max(d, e);
                case Type.MIN -> Math.min(d, e);
                case Type.MUL -> d > 0.0 && e > 0.0 ? d * e : (f < 0.0 && g < 0.0 ? f * g : Math.min(d * g, f * e));
            };
            switch (type) {
                default: {
                    throw new IncompatibleClassChangeError();
                }
                case ADD: {
                    double d2 = f + g;
                    break;
                }
                case MAX: {
                    double d2 = Math.max(f, g);
                    break;
                }
                case MIN: {
                    double d2 = Math.min(f, g);
                    break;
                }
                case MUL: {
                    double d2 = d > 0.0 && e > 0.0 ? f * g : (i = f < 0.0 && g < 0.0 ? d * e : Math.max(d * e, f * g));
                }
            }
            if (type == Type.MUL || type == Type.ADD) {
                if (argument1 instanceof Constant) {
                    Constant constant = (Constant)argument1;
                    return new class_6929(type == Type.ADD ? class_6929.SpecificType.ADD : class_6929.SpecificType.MUL, argument2, h, i, constant.value);
                }
                if (argument2 instanceof Constant) {
                    Constant constant = (Constant)argument2;
                    return new class_6929(type == Type.ADD ? class_6929.SpecificType.ADD : class_6929.SpecificType.MUL, argument1, h, i, constant.value);
                }
            }
            return new class_6917(type, argument1, argument2, h, i);
        }

        public Type type();

        public DensityFunction argument1();

        public DensityFunction argument2();

        @Override
        default public CodecHolder<? extends DensityFunction> getCodec() {
            return this.type().codec;
        }

        public static enum Type implements StringIdentifiable
        {
            ADD("add"),
            MUL("mul"),
            MIN("min"),
            MAX("max");

            final CodecHolder<Operation> codec = DensityFunctionTypes.method_41068((densityFunction, densityFunction2) -> Operation.create(this, densityFunction, densityFunction2), Operation::argument1, Operation::argument2);
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

    public record Spline(net.minecraft.util.math.Spline<class_7136, class_7135> spline) implements DensityFunction
    {
        private static final Codec<net.minecraft.util.math.Spline<class_7136, class_7135>> field_37678 = net.minecraft.util.math.Spline.createCodec(class_7135.field_37679);
        private static final MapCodec<Spline> field_37256 = ((MapCodec)field_37678.fieldOf("spline")).xmap(Spline::new, Spline::spline);
        public static final CodecHolder<Spline> CODEC = DensityFunctionTypes.method_41065(field_37256);

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return this.spline.apply(new class_7136(pos));
        }

        @Override
        public double minValue() {
            return this.spline.min();
        }

        @Override
        public double maxValue() {
            return this.spline.max();
        }

        @Override
        public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            arg.method_40478(ds, this);
        }

        @Override
        public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return visitor.apply(new Spline(this.spline.method_41187(arg -> arg.method_41530(visitor))));
        }

        @Override
        public CodecHolder<? extends DensityFunction> getCodec() {
            return CODEC;
        }

        public record class_7136(DensityFunction.NoisePos context) {
        }

        public record class_7135(RegistryEntry<DensityFunction> function) implements ToFloatFunction<class_7136>
        {
            public static final Codec<class_7135> field_37679 = DensityFunction.REGISTRY_ENTRY_CODEC.xmap(class_7135::new, class_7135::function);

            @Override
            public String toString() {
                Optional<RegistryKey<DensityFunction>> optional = this.function.getKey();
                if (optional.isPresent()) {
                    RegistryKey<DensityFunction> registryKey = optional.get();
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

            @Override
            public float apply(class_7136 arg) {
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

            public class_7135 method_41530(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
                return new class_7135(new RegistryEntry.Direct<DensityFunction>(this.function.value().apply(densityFunctionVisitor)));
            }
        }
    }

    record Constant(double value) implements DensityFunction.class_6913
    {
        static final CodecHolder<Constant> CODEC = DensityFunctionTypes.method_41064(CONSTANT_RANGE, Constant::new, Constant::value);
        static final Constant ZERO = new Constant(0.0);

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

    record YClampedGradient(int fromY, int toY, double fromValue, double toValue) implements DensityFunction.class_6913
    {
        private static final MapCodec<YClampedGradient> field_37075 = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Codec.intRange(DimensionType.MIN_HEIGHT * 2, DimensionType.MAX_COLUMN_HEIGHT * 2).fieldOf("from_y")).forGetter(YClampedGradient::fromY), ((MapCodec)Codec.intRange(DimensionType.MIN_HEIGHT * 2, DimensionType.MAX_COLUMN_HEIGHT * 2).fieldOf("to_y")).forGetter(YClampedGradient::toY), ((MapCodec)CONSTANT_RANGE.fieldOf("from_value")).forGetter(YClampedGradient::fromValue), ((MapCodec)CONSTANT_RANGE.fieldOf("to_value")).forGetter(YClampedGradient::toValue)).apply((Applicative<YClampedGradient, ?>)instance, YClampedGradient::new));
        public static final CodecHolder<YClampedGradient> CODEC = DensityFunctionTypes.method_41065(field_37075);

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

    record class_6917(Operation.Type type, DensityFunction argument1, DensityFunction argument2, double minValue, double maxValue) implements Operation
    {
        @Override
        public double sample(DensityFunction.NoisePos pos) {
            double d = this.argument1.sample(pos);
            return switch (this.type) {
                default -> throw new IncompatibleClassChangeError();
                case Operation.Type.ADD -> d + this.argument2.sample(pos);
                case Operation.Type.MUL -> {
                    if (d == 0.0) {
                        yield 0.0;
                    }
                    yield d * this.argument2.sample(pos);
                }
                case Operation.Type.MIN -> {
                    if (d < this.argument2.minValue()) {
                        yield d;
                    }
                    yield Math.min(d, this.argument2.sample(pos));
                }
                case Operation.Type.MAX -> d > this.argument2.maxValue() ? d : Math.max(d, this.argument2.sample(pos));
            };
        }

        @Override
        public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            this.argument1.method_40470(ds, arg);
            switch (this.type) {
                case ADD: {
                    double[] es = new double[ds.length];
                    this.argument2.method_40470(es, arg);
                    for (int i = 0; i < ds.length; ++i) {
                        ds[i] = ds[i] + es[i];
                    }
                    break;
                }
                case MUL: {
                    for (int j = 0; j < ds.length; ++j) {
                        double d = ds[j];
                        ds[j] = d == 0.0 ? 0.0 : d * this.argument2.sample(arg.method_40477(j));
                    }
                    break;
                }
                case MIN: {
                    double e = this.argument2.minValue();
                    for (int k = 0; k < ds.length; ++k) {
                        double f = ds[k];
                        ds[k] = f < e ? f : Math.min(f, this.argument2.sample(arg.method_40477(k)));
                    }
                    break;
                }
                case MAX: {
                    double e = this.argument2.maxValue();
                    for (int k = 0; k < ds.length; ++k) {
                        double f = ds[k];
                        ds[k] = f > e ? f : Math.max(f, this.argument2.sample(arg.method_40477(k)));
                    }
                    break;
                }
            }
        }

        @Override
        public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return visitor.apply(Operation.create(this.type, this.argument1.apply(visitor), this.argument2.apply(visitor)));
        }
    }

    record class_6929(SpecificType specificType, DensityFunction input, double minValue, double maxValue, double argument) implements class_6932,
    Operation
    {
        @Override
        public Operation.Type type() {
            return this.specificType == SpecificType.MUL ? Operation.Type.MUL : Operation.Type.ADD;
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
                default -> throw new IncompatibleClassChangeError();
                case SpecificType.MUL -> density * this.argument;
                case SpecificType.ADD -> density + this.argument;
            };
        }

        @Override
        public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            double g;
            double f;
            DensityFunction densityFunction = this.input.apply(visitor);
            double d = densityFunction.minValue();
            double e = densityFunction.maxValue();
            if (this.specificType == SpecificType.ADD) {
                f = d + this.argument;
                g = e + this.argument;
            } else if (this.argument >= 0.0) {
                f = d * this.argument;
                g = e * this.argument;
            } else {
                f = e * this.argument;
                g = d * this.argument;
            }
            return new class_6929(this.specificType, densityFunction, f, g, this.argument);
        }

        static enum SpecificType {
            MUL,
            ADD;

        }
    }

    static interface class_6939
    extends DensityFunction {
        public DensityFunction.class_7270 offsetNoise();

        @Override
        default public double minValue() {
            return -this.maxValue();
        }

        @Override
        default public double maxValue() {
            return this.offsetNoise().method_42355() * 4.0;
        }

        default public double method_40525(double d, double e, double f) {
            return this.offsetNoise().method_42356(d * 0.25, e * 0.25, f * 0.25) * 4.0;
        }

        @Override
        default public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            arg.method_40478(ds, this);
        }
    }

    public static interface Wrapper
    extends DensityFunction {
        public class_6927.Type type();

        public DensityFunction wrapped();

        @Override
        default public CodecHolder<? extends DensityFunction> getCodec() {
            return this.type().codec;
        }

        @Override
        default public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return visitor.apply(new class_6927(this.type(), this.wrapped().apply(visitor)));
        }
    }

    @Debug
    public record RegistryEntryHolder(RegistryEntry<DensityFunction> function) implements DensityFunction
    {
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
            return visitor.apply(new RegistryEntryHolder(new RegistryEntry.Direct<DensityFunction>(this.function.value().apply(visitor))));
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

    public static interface class_7050
    extends DensityFunction.class_6913 {
        public static final CodecHolder<DensityFunction> CODEC = CodecHolder.of(MapCodec.unit(Beardifier.INSTANCE));

        @Override
        default public CodecHolder<? extends DensityFunction> getCodec() {
            return CODEC;
        }
    }

    static interface class_6932
    extends DensityFunction {
        public DensityFunction input();

        @Override
        default public double sample(DensityFunction.NoisePos pos) {
            return this.apply(this.input().sample(pos));
        }

        @Override
        default public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            this.input().method_40470(ds, arg);
            for (int i = 0; i < ds.length; ++i) {
                ds[i] = this.apply(ds[i]);
            }
        }

        public double apply(double var1);
    }

    static interface class_6943
    extends DensityFunction {
        public DensityFunction input();

        @Override
        default public double sample(DensityFunction.NoisePos pos) {
            return this.method_40518(pos, this.input().sample(pos));
        }

        @Override
        default public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            this.input().method_40470(ds, arg);
            for (int i = 0; i < ds.length; ++i) {
                ds[i] = this.method_40518(arg.method_40477(i), ds[i]);
            }
        }

        public double method_40518(DensityFunction.NoisePos var1, double var2);
    }
}

