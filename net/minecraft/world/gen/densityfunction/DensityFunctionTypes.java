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
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
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
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctions;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class DensityFunctionTypes {
    private static final Codec<DensityFunction> field_37062 = Registry.DENSITY_FUNCTION_TYPE.getCodec().dispatch(DensityFunction::getCodec, Function.identity());
    protected static final double field_37060 = 1000000.0;
    static final Codec<Double> field_37063 = Codec.doubleRange(-1000000.0, 1000000.0);
    public static final Codec<DensityFunction> field_37061 = Codec.either(field_37063, field_37062).xmap(either -> either.map(DensityFunctionTypes::constant, Function.identity()), densityFunction -> {
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
        for (Enum enum_ : class_6925.class_6926.values()) {
            DensityFunctionTypes.register(registry, ((class_6925.class_6926)enum_).asString(), ((class_6925.class_6926)enum_).codec);
        }
        DensityFunctionTypes.register(registry, "slide", Slide.CODEC);
        for (Enum enum_ : class_7055.class_6918.values()) {
            DensityFunctionTypes.register(registry, ((class_7055.class_6918)enum_).asString(), ((class_7055.class_6918)enum_).codec);
        }
        DensityFunctionTypes.register(registry, "spline", Spline.CODEC);
        DensityFunctionTypes.register(registry, "terrain_shaper_spline", TerrainShaperSpline.CODEC);
        DensityFunctionTypes.register(registry, "constant", Constant.CODEC);
        return DensityFunctionTypes.register(registry, "y_clamped_gradient", YClampedGradient.CODEC);
    }

    private static Codec<? extends DensityFunction> register(Registry<Codec<? extends DensityFunction>> registry, String id, Codec<? extends DensityFunction> codec) {
        return Registry.register(registry, id, codec);
    }

    static <A, O> Codec<O> method_41064(Codec<A> codec, Function<A, O> function, Function<O, A> function2) {
        return ((MapCodec)codec.fieldOf("argument")).xmap(function, function2).codec();
    }

    static <O> Codec<O> method_41069(Function<DensityFunction, O> function, Function<O, DensityFunction> function2) {
        return DensityFunctionTypes.method_41064(DensityFunction.field_37059, function, function2);
    }

    static <O> Codec<O> method_41068(BiFunction<DensityFunction, DensityFunction, O> biFunction, Function<O, DensityFunction> function, Function<O, DensityFunction> function2) {
        return RecordCodecBuilder.create(instance -> instance.group(((MapCodec)DensityFunction.field_37059.fieldOf("argument1")).forGetter(function), ((MapCodec)DensityFunction.field_37059.fieldOf("argument2")).forGetter(function2)).apply(instance, biFunction));
    }

    static <O> Codec<O> method_41065(MapCodec<O> mapCodec) {
        return mapCodec.codec();
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

    public static DensityFunction noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, @Deprecated double d, double e, double f, double g) {
        return DensityFunctionTypes.method_40484(new Noise(noiseParameters, null, d, e), f, g);
    }

    public static DensityFunction noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, double d, double e, double f) {
        return DensityFunctionTypes.noise(noiseParameters, 1.0, d, e, f);
    }

    public static DensityFunction noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, double d, double e) {
        return DensityFunctionTypes.noise(noiseParameters, 1.0, 1.0, d, e);
    }

    public static DensityFunction shiftedNoise(DensityFunction densityFunction, DensityFunction densityFunction2, double d, RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters) {
        return new ShiftedNoise(densityFunction, DensityFunctionTypes.zero(), densityFunction2, d, 0.0, noiseParameters, null);
    }

    public static DensityFunction noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters) {
        return DensityFunctionTypes.method_40502(noiseParameters, 1.0, 1.0);
    }

    public static DensityFunction method_40502(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, double d, double e) {
        return new Noise(noiseParameters, null, d, e);
    }

    public static DensityFunction noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, double d) {
        return DensityFunctionTypes.method_40502(noiseParameters, 1.0, d);
    }

    public static DensityFunction rangeChoice(DensityFunction densityFunction, double d, double e, DensityFunction densityFunction2, DensityFunction densityFunction3) {
        return new RangeChoice(densityFunction, d, e, densityFunction2, densityFunction3);
    }

    public static DensityFunction shiftA(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters) {
        return new ShiftA(noiseParameters, null);
    }

    public static DensityFunction shiftB(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters) {
        return new ShiftB(noiseParameters, null);
    }

    public static DensityFunction shift(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters) {
        return new Shift(noiseParameters, null);
    }

    public static DensityFunction blendDensity(DensityFunction densityFunction) {
        return new BlendDensity(densityFunction);
    }

    public static DensityFunction endIslands(long seed) {
        return new EndIslands(seed);
    }

    public static DensityFunction weirdScaledSampler(DensityFunction densityFunction, RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry, WeirdScaledSampler.RarityValueMapper rarityValueMapper) {
        return new WeirdScaledSampler(densityFunction, registryEntry, null, rarityValueMapper);
    }

    public static DensityFunction slide(GenerationShapeConfig generationShapeConfig, DensityFunction densityFunction) {
        return new Slide(generationShapeConfig, densityFunction);
    }

    public static DensityFunction method_40486(DensityFunction densityFunction, DensityFunction densityFunction2) {
        return class_7055.method_41097(class_7055.class_6918.ADD, densityFunction, densityFunction2);
    }

    public static DensityFunction method_40500(DensityFunction densityFunction, DensityFunction densityFunction2) {
        return class_7055.method_41097(class_7055.class_6918.MUL, densityFunction, densityFunction2);
    }

    public static DensityFunction method_40505(DensityFunction densityFunction, DensityFunction densityFunction2) {
        return class_7055.method_41097(class_7055.class_6918.MIN, densityFunction, densityFunction2);
    }

    public static DensityFunction method_40508(DensityFunction densityFunction, DensityFunction densityFunction2) {
        return class_7055.method_41097(class_7055.class_6918.MAX, densityFunction, densityFunction2);
    }

    public static DensityFunction method_40489(DensityFunction densityFunction, DensityFunction densityFunction2, DensityFunction densityFunction3, TerrainShaperSpline.class_7054 arg, double d, double e) {
        return new TerrainShaperSpline(densityFunction, densityFunction2, densityFunction3, null, arg, d, e);
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

    public static DensityFunction method_40490(DensityFunction densityFunction, class_6925.class_6926 arg) {
        return class_6925.method_41079(arg, densityFunction);
    }

    private static DensityFunction method_40484(DensityFunction densityFunction, double d, double e) {
        double f = (d + e) * 0.5;
        double g = (e - d) * 0.5;
        return DensityFunctionTypes.method_40486(DensityFunctionTypes.constant(f), DensityFunctionTypes.method_40500(DensityFunctionTypes.constant(g), densityFunction));
    }

    public static DensityFunction blendAlpha() {
        return BlendAlpha.INSTANCE;
    }

    public static DensityFunction blendOffset() {
        return BlendOffset.INSTANCE;
    }

    public static DensityFunction method_40488(DensityFunction densityFunction, DensityFunction densityFunction2, DensityFunction densityFunction3) {
        DensityFunction densityFunction4 = DensityFunctionTypes.cacheOnce(densityFunction);
        DensityFunction densityFunction5 = DensityFunctionTypes.method_40486(DensityFunctionTypes.method_40500(densityFunction4, DensityFunctionTypes.constant(-1.0)), DensityFunctionTypes.constant(1.0));
        return DensityFunctionTypes.method_40486(DensityFunctionTypes.method_40500(densityFunction2, densityFunction5), DensityFunctionTypes.method_40500(densityFunction3, densityFunction4));
    }

    protected static enum BlendAlpha implements DensityFunction.class_6913
    {
        INSTANCE;

        public static final Codec<DensityFunction> CODEC;

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

        static {
            CODEC = Codec.unit(INSTANCE);
        }
    }

    protected static enum BlendOffset implements DensityFunction.class_6913
    {
        INSTANCE;

        public static final Codec<DensityFunction> CODEC;

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

        static {
            CODEC = Codec.unit(INSTANCE);
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
        public DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
            return (DensityFunction)densityFunctionVisitor.apply(new class_6927(this.type, this.wrapped.method_40469(densityFunctionVisitor)));
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
            final Codec<Wrapper> codec = DensityFunctionTypes.method_41069(densityFunction -> new class_6927(this, (DensityFunction)densityFunction), Wrapper::wrapped);

            private Type(String name) {
                this.name = name;
            }

            @Override
            public String asString() {
                return this.name;
            }
        }
    }

    protected record Noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Nullable DoublePerlinNoiseSampler noise, @Deprecated double xzScale, double yScale) implements DensityFunction.class_6913
    {
        public static final MapCodec<Noise> field_37090 = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("noise")).forGetter(Noise::noiseData), ((MapCodec)Codec.DOUBLE.fieldOf("xz_scale")).forGetter(Noise::xzScale), ((MapCodec)Codec.DOUBLE.fieldOf("y_scale")).forGetter(Noise::yScale)).apply((Applicative<Noise, ?>)instance, Noise::method_41084));
        public static final Codec<Noise> CODEC = DensityFunctionTypes.method_41065(field_37090);

        public static Noise method_41084(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry, @Deprecated double d, double e) {
            return new Noise(registryEntry, null, d, e);
        }

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return this.noise == null ? 0.0 : this.noise.sample((double)pos.blockX() * this.xzScale, (double)pos.blockY() * this.yScale, (double)pos.blockZ() * this.xzScale);
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

        @Nullable
        public DoublePerlinNoiseSampler noise() {
            return this.noise;
        }

        @Deprecated
        public double xzScale() {
            return this.xzScale;
        }
    }

    protected static final class EndIslands
    implements DensityFunction.class_6913 {
        public static final Codec<EndIslands> CODEC = Codec.unit(new EndIslands(0L));
        final SimplexNoiseSampler field_36554;

        public EndIslands(long seed) {
            AtomicSimpleRandom abstractRandom = new AtomicSimpleRandom(seed);
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

    protected record WeirdScaledSampler(DensityFunction input, RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Nullable DoublePerlinNoiseSampler noise, RarityValueMapper rarityValueMapper) implements class_6943
    {
        private static final MapCodec<WeirdScaledSampler> field_37065 = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)DensityFunction.field_37059.fieldOf("input")).forGetter(WeirdScaledSampler::input), ((MapCodec)DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("noise")).forGetter(WeirdScaledSampler::noiseData), ((MapCodec)RarityValueMapper.CODEC.fieldOf("rarity_value_mapper")).forGetter(WeirdScaledSampler::rarityValueMapper)).apply((Applicative<WeirdScaledSampler, ?>)instance, WeirdScaledSampler::create));
        public static final Codec<WeirdScaledSampler> CODEC = DensityFunctionTypes.method_41065(field_37065);

        public static WeirdScaledSampler create(DensityFunction input, RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, RarityValueMapper rarityValueMapper) {
            return new WeirdScaledSampler(input, noiseData, null, rarityValueMapper);
        }

        @Override
        public double method_40518(DensityFunction.NoisePos noisePos, double d) {
            if (this.noise == null) {
                return 0.0;
            }
            double e = this.rarityValueMapper.scaleFunction.get(d);
            return e * Math.abs(this.noise.sample((double)noisePos.blockX() / e, (double)noisePos.blockY() / e, (double)noisePos.blockZ() / e));
        }

        @Override
        public DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
            this.input.method_40469(densityFunctionVisitor);
            return (DensityFunction)densityFunctionVisitor.apply(new WeirdScaledSampler(this.input.method_40469(densityFunctionVisitor), this.noiseData, this.noise, this.rarityValueMapper));
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

        @Nullable
        public DoublePerlinNoiseSampler noise() {
            return this.noise;
        }

        public static enum RarityValueMapper implements StringIdentifiable
        {
            TYPE1("type_1", DensityFunctions.CaveScaler::scaleTunnels, 2.0),
            TYPE2("type_2", DensityFunctions.CaveScaler::scaleCaves, 3.0);

            private static final Map<String, RarityValueMapper> TYPES_MAP;
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
                TYPES_MAP = Arrays.stream(RarityValueMapper.values()).collect(Collectors.toMap(RarityValueMapper::asString, rarityValueMapper -> rarityValueMapper));
                CODEC = StringIdentifiable.createCodec(RarityValueMapper::values, TYPES_MAP::get);
            }
        }
    }

    protected record ShiftedNoise(DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ, double xzScale, double yScale, RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Nullable DoublePerlinNoiseSampler noise) implements DensityFunction
    {
        private static final MapCodec<ShiftedNoise> field_37098 = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)DensityFunction.field_37059.fieldOf("shift_x")).forGetter(ShiftedNoise::shiftX), ((MapCodec)DensityFunction.field_37059.fieldOf("shift_y")).forGetter(ShiftedNoise::shiftY), ((MapCodec)DensityFunction.field_37059.fieldOf("shift_z")).forGetter(ShiftedNoise::shiftZ), ((MapCodec)Codec.DOUBLE.fieldOf("xz_scale")).forGetter(ShiftedNoise::xzScale), ((MapCodec)Codec.DOUBLE.fieldOf("y_scale")).forGetter(ShiftedNoise::yScale), ((MapCodec)DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("noise")).forGetter(ShiftedNoise::noiseData)).apply((Applicative<ShiftedNoise, ?>)instance, ShiftedNoise::create));
        public static final Codec<ShiftedNoise> CODEC = DensityFunctionTypes.method_41065(field_37098);

        public static ShiftedNoise create(DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ, double xzScale, double yScale, RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData) {
            return new ShiftedNoise(shiftX, shiftY, shiftZ, xzScale, yScale, noiseData, null);
        }

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            if (this.noise == null) {
                return 0.0;
            }
            double d = (double)pos.blockX() * this.xzScale + this.shiftX.sample(pos);
            double e = (double)pos.blockY() * this.yScale + this.shiftY.sample(pos);
            double f = (double)pos.blockZ() * this.xzScale + this.shiftZ.sample(pos);
            return this.noise.sample(d, e, f);
        }

        @Override
        public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            arg.method_40478(ds, this);
        }

        @Override
        public DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
            return (DensityFunction)densityFunctionVisitor.apply(new ShiftedNoise(this.shiftX.method_40469(densityFunctionVisitor), this.shiftY.method_40469(densityFunctionVisitor), this.shiftZ.method_40469(densityFunctionVisitor), this.xzScale, this.yScale, this.noiseData, this.noise));
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

        @Nullable
        public DoublePerlinNoiseSampler noise() {
            return this.noise;
        }
    }

    record RangeChoice(DensityFunction input, double minInclusive, double maxExclusive, DensityFunction whenInRange, DensityFunction whenOutOfRange) implements DensityFunction
    {
        public static final MapCodec<RangeChoice> field_37092 = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)DensityFunction.field_37059.fieldOf("input")).forGetter(RangeChoice::input), ((MapCodec)field_37063.fieldOf("min_inclusive")).forGetter(RangeChoice::minInclusive), ((MapCodec)field_37063.fieldOf("max_exclusive")).forGetter(RangeChoice::maxExclusive), ((MapCodec)DensityFunction.field_37059.fieldOf("when_in_range")).forGetter(RangeChoice::whenInRange), ((MapCodec)DensityFunction.field_37059.fieldOf("when_out_of_range")).forGetter(RangeChoice::whenOutOfRange)).apply((Applicative<RangeChoice, ?>)instance, RangeChoice::new));
        public static final Codec<RangeChoice> CODEC = DensityFunctionTypes.method_41065(field_37092);

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
        public DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
            return (DensityFunction)densityFunctionVisitor.apply(new RangeChoice(this.input.method_40469(densityFunctionVisitor), this.minInclusive, this.maxExclusive, this.whenInRange.method_40469(densityFunctionVisitor), this.whenOutOfRange.method_40469(densityFunctionVisitor)));
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

    protected record ShiftA(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Nullable DoublePerlinNoiseSampler offsetNoise) implements class_6939
    {
        static final Codec<ShiftA> CODEC = DensityFunctionTypes.method_41064(DoublePerlinNoiseSampler.NoiseParameters.CODEC, registryEntry -> new ShiftA((RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters>)registryEntry, null), ShiftA::noiseData);

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return this.method_40525(pos.blockX(), 0.0, pos.blockZ());
        }

        @Override
        public class_6939 method_41086(DoublePerlinNoiseSampler doublePerlinNoiseSampler) {
            return new ShiftA(this.noiseData, doublePerlinNoiseSampler);
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }

        @Override
        @Nullable
        public DoublePerlinNoiseSampler offsetNoise() {
            return this.offsetNoise;
        }
    }

    protected record ShiftB(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Nullable DoublePerlinNoiseSampler offsetNoise) implements class_6939
    {
        static final Codec<ShiftB> CODEC = DensityFunctionTypes.method_41064(DoublePerlinNoiseSampler.NoiseParameters.CODEC, registryEntry -> new ShiftB((RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters>)registryEntry, null), ShiftB::noiseData);

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return this.method_40525(pos.blockZ(), pos.blockX(), 0.0);
        }

        @Override
        public class_6939 method_41086(DoublePerlinNoiseSampler doublePerlinNoiseSampler) {
            return new ShiftB(this.noiseData, doublePerlinNoiseSampler);
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }

        @Override
        @Nullable
        public DoublePerlinNoiseSampler offsetNoise() {
            return this.offsetNoise;
        }
    }

    record Shift(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Nullable DoublePerlinNoiseSampler offsetNoise) implements class_6939
    {
        static final Codec<Shift> CODEC = DensityFunctionTypes.method_41064(DoublePerlinNoiseSampler.NoiseParameters.CODEC, registryEntry -> new Shift((RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters>)registryEntry, null), Shift::noiseData);

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            return this.method_40525(pos.blockX(), pos.blockY(), pos.blockZ());
        }

        @Override
        public class_6939 method_41086(DoublePerlinNoiseSampler doublePerlinNoiseSampler) {
            return new Shift(this.noiseData, doublePerlinNoiseSampler);
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }

        @Override
        @Nullable
        public DoublePerlinNoiseSampler offsetNoise() {
            return this.offsetNoise;
        }
    }

    record BlendDensity(DensityFunction input) implements class_6943
    {
        static final Codec<BlendDensity> CODEC = DensityFunctionTypes.method_41069(BlendDensity::new, BlendDensity::input);

        @Override
        public double method_40518(DensityFunction.NoisePos noisePos, double d) {
            return noisePos.getBlender().method_39338(noisePos, d);
        }

        @Override
        public DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
            return (DensityFunction)densityFunctionVisitor.apply(new BlendDensity(this.input.method_40469(densityFunctionVisitor)));
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

    protected record Clamp(DensityFunction input, double minValue, double maxValue) implements class_6932
    {
        private static final MapCodec<Clamp> field_37083 = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)DensityFunction.field_37057.fieldOf("input")).forGetter(Clamp::input), ((MapCodec)field_37063.fieldOf("min")).forGetter(Clamp::minValue), ((MapCodec)field_37063.fieldOf("max")).forGetter(Clamp::maxValue)).apply((Applicative<Clamp, ?>)instance, Clamp::new));
        public static final Codec<Clamp> CODEC = DensityFunctionTypes.method_41065(field_37083);

        @Override
        public double apply(double d) {
            return MathHelper.clamp(d, this.minValue, this.maxValue);
        }

        @Override
        public DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
            return new Clamp(this.input.method_40469(densityFunctionVisitor), this.minValue, this.maxValue);
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }
    }

    protected record class_6925(class_6926 type, DensityFunction input, double minValue, double maxValue) implements class_6932
    {
        public static class_6925 method_41079(class_6926 arg, DensityFunction densityFunction) {
            double d = densityFunction.minValue();
            double e = class_6925.method_40521(arg, d);
            double f = class_6925.method_40521(arg, densityFunction.maxValue());
            if (arg == class_6926.ABS || arg == class_6926.SQUARE) {
                return new class_6925(arg, densityFunction, Math.max(0.0, d), Math.max(e, f));
            }
            return new class_6925(arg, densityFunction, e, f);
        }

        private static double method_40521(class_6926 arg, double d) {
            return switch (arg) {
                default -> throw new IncompatibleClassChangeError();
                case class_6926.ABS -> Math.abs(d);
                case class_6926.SQUARE -> d * d;
                case class_6926.CUBE -> d * d * d;
                case class_6926.HALF_NEGATIVE -> {
                    if (d > 0.0) {
                        yield d;
                    }
                    yield d * 0.5;
                }
                case class_6926.QUARTER_NEGATIVE -> {
                    if (d > 0.0) {
                        yield d;
                    }
                    yield d * 0.25;
                }
                case class_6926.SQUEEZE -> {
                    double e = MathHelper.clamp(d, -1.0, 1.0);
                    yield e / 2.0 - e * e * e / 24.0;
                }
            };
        }

        @Override
        public double apply(double d) {
            return class_6925.method_40521(this.type, d);
        }

        @Override
        public class_6925 method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
            return class_6925.method_41079(this.type, this.input.method_40469(densityFunctionVisitor));
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return this.type.codec;
        }

        @Override
        public /* synthetic */ DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
            return this.method_40469(densityFunctionVisitor);
        }

        static enum class_6926 implements StringIdentifiable
        {
            ABS("abs"),
            SQUARE("square"),
            CUBE("cube"),
            HALF_NEGATIVE("half_negative"),
            QUARTER_NEGATIVE("quarter_negative"),
            SQUEEZE("squeeze");

            private final String name;
            final Codec<class_6925> codec = DensityFunctionTypes.method_41069(densityFunction -> class_6925.method_41079(this, densityFunction), class_6925::input);

            private class_6926(String name) {
                this.name = name;
            }

            @Override
            public String asString() {
                return this.name;
            }
        }
    }

    protected record Slide(@Nullable GenerationShapeConfig settings, DensityFunction input) implements class_6943
    {
        public static final Codec<Slide> CODEC = DensityFunctionTypes.method_41069(densityFunction -> new Slide(null, (DensityFunction)densityFunction), Slide::input);

        @Override
        public double method_40518(DensityFunction.NoisePos noisePos, double d) {
            if (this.settings == null) {
                return d;
            }
            return DensityFunctions.method_40542(this.settings, d, noisePos.blockY());
        }

        @Override
        public DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
            return (DensityFunction)densityFunctionVisitor.apply(new Slide(this.settings, this.input.method_40469(densityFunctionVisitor)));
        }

        @Override
        public double minValue() {
            if (this.settings == null) {
                return this.input.minValue();
            }
            return Math.min(this.input.minValue(), Math.min(this.settings.bottomSlide().target(), this.settings.topSlide().target()));
        }

        @Override
        public double maxValue() {
            if (this.settings == null) {
                return this.input.maxValue();
            }
            return Math.max(this.input.maxValue(), Math.max(this.settings.bottomSlide().target(), this.settings.topSlide().target()));
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }

        @Nullable
        public GenerationShapeConfig settings() {
            return this.settings;
        }
    }

    static interface class_7055
    extends DensityFunction {
        public static final Logger LOGGER = LogUtils.getLogger();

        public static class_7055 method_41097(class_6918 arg, DensityFunction densityFunction, DensityFunction densityFunction2) {
            double i;
            double d = densityFunction.minValue();
            double e = densityFunction2.minValue();
            double f = densityFunction.maxValue();
            double g = densityFunction2.maxValue();
            if (arg == class_6918.MIN || arg == class_6918.MAX) {
                boolean bl2;
                boolean bl = d >= g;
                boolean bl3 = bl2 = e >= f;
                if (bl || bl2) {
                    LOGGER.warn("Creating a " + arg + " function between two non-overlapping inputs: " + densityFunction + " and " + densityFunction2);
                }
            }
            double h = switch (arg) {
                default -> throw new IncompatibleClassChangeError();
                case class_6918.ADD -> d + e;
                case class_6918.MAX -> Math.max(d, e);
                case class_6918.MIN -> Math.min(d, e);
                case class_6918.MUL -> d > 0.0 && e > 0.0 ? d * e : (f < 0.0 && g < 0.0 ? f * g : Math.min(d * g, f * e));
            };
            switch (arg) {
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
            if (arg == class_6918.MUL || arg == class_6918.ADD) {
                if (densityFunction instanceof Constant) {
                    Constant constant = (Constant)densityFunction;
                    return new class_6929(arg == class_6918.ADD ? class_6929.class_6930.ADD : class_6929.class_6930.MUL, densityFunction2, h, i, constant.value);
                }
                if (densityFunction2 instanceof Constant) {
                    Constant constant = (Constant)densityFunction2;
                    return new class_6929(arg == class_6918.ADD ? class_6929.class_6930.ADD : class_6929.class_6930.MUL, densityFunction, h, i, constant.value);
                }
            }
            return new class_6917(arg, densityFunction, densityFunction2, h, i);
        }

        public class_6918 type();

        public DensityFunction argument1();

        public DensityFunction argument2();

        @Override
        default public Codec<? extends DensityFunction> getCodec() {
            return this.type().codec;
        }

        public static enum class_6918 implements StringIdentifiable
        {
            ADD("add"),
            MUL("mul"),
            MIN("min"),
            MAX("max");

            final Codec<class_7055> codec = DensityFunctionTypes.method_41068((densityFunction, densityFunction2) -> class_7055.method_41097(this, densityFunction, densityFunction2), class_7055::argument1, class_7055::argument2);
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

    public record Spline(net.minecraft.util.math.Spline<VanillaTerrainParameters.class_7075> spline, double minValue, double maxValue) implements DensityFunction
    {
        private static final MapCodec<Spline> field_37256 = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)VanillaTerrainParameters.field_37252.fieldOf("spline")).forGetter(Spline::spline), ((MapCodec)field_37063.fieldOf("min_value")).forGetter(Spline::minValue), ((MapCodec)field_37063.fieldOf("max_value")).forGetter(Spline::maxValue)).apply((Applicative<Spline, ?>)instance, Spline::new));
        public static final Codec<Spline> CODEC = DensityFunctionTypes.method_41065(field_37256);

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
            return (DensityFunction)densityFunctionVisitor.apply(new Spline(this.spline.method_41187(toFloatFunction -> {
                ToFloatFunction toFloatFunction2;
                if (toFloatFunction instanceof VanillaTerrainParameters.class_7074) {
                    VanillaTerrainParameters.class_7074 lv = (VanillaTerrainParameters.class_7074)toFloatFunction;
                    toFloatFunction2 = lv.method_41194(densityFunctionVisitor);
                } else {
                    toFloatFunction2 = toFloatFunction;
                }
                return toFloatFunction2;
            }), this.minValue, this.maxValue));
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }
    }

    @Deprecated
    public record TerrainShaperSpline(DensityFunction continentalness, DensityFunction erosion, DensityFunction weirdness, @Nullable VanillaTerrainParameters shaper, class_7054 spline, double minValue, double maxValue) implements DensityFunction
    {
        private static final MapCodec<TerrainShaperSpline> field_37101 = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)DensityFunction.field_37059.fieldOf("continentalness")).forGetter(TerrainShaperSpline::continentalness), ((MapCodec)DensityFunction.field_37059.fieldOf("erosion")).forGetter(TerrainShaperSpline::erosion), ((MapCodec)DensityFunction.field_37059.fieldOf("weirdness")).forGetter(TerrainShaperSpline::weirdness), ((MapCodec)class_7054.CODEC.fieldOf("spline")).forGetter(TerrainShaperSpline::spline), ((MapCodec)field_37063.fieldOf("min_value")).forGetter(TerrainShaperSpline::minValue), ((MapCodec)field_37063.fieldOf("max_value")).forGetter(TerrainShaperSpline::maxValue)).apply((Applicative<TerrainShaperSpline, ?>)instance, TerrainShaperSpline::method_41094));
        public static final Codec<TerrainShaperSpline> CODEC = DensityFunctionTypes.method_41065(field_37101);

        public static TerrainShaperSpline method_41094(DensityFunction densityFunction, DensityFunction densityFunction2, DensityFunction densityFunction3, class_7054 arg, double d, double e) {
            return new TerrainShaperSpline(densityFunction, densityFunction2, densityFunction3, null, arg, d, e);
        }

        @Override
        public double sample(DensityFunction.NoisePos pos) {
            if (this.shaper == null) {
                return 0.0;
            }
            return MathHelper.clamp((double)this.spline.field_37108.apply(this.shaper, VanillaTerrainParameters.createNoisePoint((float)this.continentalness.sample(pos), (float)this.erosion.sample(pos), (float)this.weirdness.sample(pos))), this.minValue, this.maxValue);
        }

        @Override
        public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
            for (int i = 0; i < ds.length; ++i) {
                ds[i] = this.sample(arg.method_40477(i));
            }
        }

        @Override
        public DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
            return (DensityFunction)densityFunctionVisitor.apply(new TerrainShaperSpline(this.continentalness.method_40469(densityFunctionVisitor), this.erosion.method_40469(densityFunctionVisitor), this.weirdness.method_40469(densityFunctionVisitor), this.shaper, this.spline, this.minValue, this.maxValue));
        }

        @Override
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }

        @Nullable
        public VanillaTerrainParameters shaper() {
            return this.shaper;
        }

        public static enum class_7054 implements StringIdentifiable
        {
            OFFSET("offset", VanillaTerrainParameters::getOffset),
            FACTOR("factor", VanillaTerrainParameters::getFactor),
            JAGGEDNESS("jaggedness", VanillaTerrainParameters::getPeak);

            private static final Map<String, class_7054> field_37106;
            public static final Codec<class_7054> CODEC;
            private final String name;
            final class_7053 field_37108;

            private class_7054(String name, class_7053 arg) {
                this.name = name;
                this.field_37108 = arg;
            }

            @Override
            public String asString() {
                return this.name;
            }

            static {
                field_37106 = Arrays.stream(class_7054.values()).collect(Collectors.toMap(class_7054::asString, arg -> arg));
                CODEC = StringIdentifiable.createCodec(class_7054::values, field_37106::get);
            }
        }

        static interface class_7053 {
            public float apply(VanillaTerrainParameters var1, VanillaTerrainParameters.NoisePoint var2);
        }
    }

    record Constant(double value) implements DensityFunction.class_6913
    {
        static final Codec<Constant> CODEC = DensityFunctionTypes.method_41064(field_37063, Constant::new, Constant::value);
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
        public Codec<? extends DensityFunction> getCodec() {
            return CODEC;
        }
    }

    record YClampedGradient(int fromY, int toY, double fromValue, double toValue) implements DensityFunction.class_6913
    {
        private static final MapCodec<YClampedGradient> field_37075 = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Codec.intRange(DimensionType.MIN_HEIGHT * 2, DimensionType.MAX_COLUMN_HEIGHT * 2).fieldOf("from_y")).forGetter(YClampedGradient::fromY), ((MapCodec)Codec.intRange(DimensionType.MIN_HEIGHT * 2, DimensionType.MAX_COLUMN_HEIGHT * 2).fieldOf("to_y")).forGetter(YClampedGradient::toY), ((MapCodec)field_37063.fieldOf("from_value")).forGetter(YClampedGradient::fromValue), ((MapCodec)field_37063.fieldOf("to_value")).forGetter(YClampedGradient::toValue)).apply((Applicative<YClampedGradient, ?>)instance, YClampedGradient::new));
        public static final Codec<YClampedGradient> CODEC = DensityFunctionTypes.method_41065(field_37075);

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

    record class_6917(class_7055.class_6918 type, DensityFunction argument1, DensityFunction argument2, double minValue, double maxValue) implements class_7055
    {
        @Override
        public double sample(DensityFunction.NoisePos pos) {
            double d = this.argument1.sample(pos);
            return switch (this.type) {
                default -> throw new IncompatibleClassChangeError();
                case class_7055.class_6918.ADD -> d + this.argument2.sample(pos);
                case class_7055.class_6918.MUL -> {
                    if (d == 0.0) {
                        yield 0.0;
                    }
                    yield d * this.argument2.sample(pos);
                }
                case class_7055.class_6918.MIN -> {
                    if (d < this.argument2.minValue()) {
                        yield d;
                    }
                    yield Math.min(d, this.argument2.sample(pos));
                }
                case class_7055.class_6918.MAX -> d > this.argument2.maxValue() ? d : Math.max(d, this.argument2.sample(pos));
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
        public DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
            return (DensityFunction)densityFunctionVisitor.apply(class_7055.method_41097(this.type, this.argument1.method_40469(densityFunctionVisitor), this.argument2.method_40469(densityFunctionVisitor)));
        }
    }

    record class_6929(class_6930 specificType, DensityFunction input, double minValue, double maxValue, double argument) implements class_7055,
    class_6932
    {
        @Override
        public class_7055.class_6918 type() {
            return this.specificType == class_6930.MUL ? class_7055.class_6918.MUL : class_7055.class_6918.ADD;
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
                default -> throw new IncompatibleClassChangeError();
                case class_6930.MUL -> d * this.argument;
                case class_6930.ADD -> d + this.argument;
            };
        }

        @Override
        public DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
            double g;
            double f;
            DensityFunction densityFunction = this.input.method_40469(densityFunctionVisitor);
            double d = densityFunction.minValue();
            double e = densityFunction.maxValue();
            if (this.specificType == class_6930.ADD) {
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

        static enum class_6930 {
            MUL,
            ADD;

        }
    }

    static interface class_6939
    extends DensityFunction.class_6913 {
        public RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData();

        @Nullable
        public DoublePerlinNoiseSampler offsetNoise();

        @Override
        default public double minValue() {
            return -this.maxValue();
        }

        @Override
        default public double maxValue() {
            DoublePerlinNoiseSampler doublePerlinNoiseSampler = this.offsetNoise();
            return (doublePerlinNoiseSampler == null ? 2.0 : doublePerlinNoiseSampler.method_40554()) * 4.0;
        }

        default public double method_40525(double d, double e, double f) {
            DoublePerlinNoiseSampler doublePerlinNoiseSampler = this.offsetNoise();
            return doublePerlinNoiseSampler == null ? 0.0 : doublePerlinNoiseSampler.sample(d * 0.25, e * 0.25, f * 0.25) * 4.0;
        }

        public class_6939 method_41086(DoublePerlinNoiseSampler var1);
    }

    public static interface Wrapper
    extends DensityFunction {
        public class_6927.Type type();

        public DensityFunction wrapped();

        @Override
        default public Codec<? extends DensityFunction> getCodec() {
            return this.type().codec;
        }
    }

    protected record class_7051(RegistryEntry<DensityFunction> function) implements DensityFunction
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
        public DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
            return (DensityFunction)densityFunctionVisitor.apply(new class_7051(new RegistryEntry.Direct<DensityFunction>(this.function.value().method_40469(densityFunctionVisitor))));
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

    public static interface class_7050
    extends DensityFunction.class_6913 {
        public static final Codec<DensityFunction> CODEC = Codec.unit(Beardifier.INSTANCE);

        @Override
        default public Codec<? extends DensityFunction> getCodec() {
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

