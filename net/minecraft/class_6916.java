/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import java.util.Arrays;
import net.minecraft.class_6910;
import net.minecraft.class_6954;
import net.minecraft.util.function.ToFloatFunction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.util.VanillaTerrainParameters;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import org.slf4j.Logger;

public final class class_6916 {
    private class_6916() {
    }

    public static class_6910 method_40483(class_6910 arg) {
        return new class_6927(class_6927.class_6928.INTERPOLATED, arg);
    }

    public static class_6910 method_40499(class_6910 arg) {
        return new class_6927(class_6927.class_6928.FLAT_CACHE, arg);
    }

    public static class_6910 method_40504(class_6910 arg) {
        return new class_6927(class_6927.class_6928.CACHE2D, arg);
    }

    public static class_6910 method_40507(class_6910 arg) {
        return new class_6927(class_6927.class_6928.CACHE_ONCE, arg);
    }

    public static class_6910 method_40510(class_6910 arg) {
        return new class_6927(class_6927.class_6928.CACHE_ALL_IN_CELL, arg);
    }

    public static class_6910 method_40497(DoublePerlinNoiseSampler doublePerlinNoiseSampler, @Deprecated double d, double e, double f, double g) {
        return class_6916.method_40484(new class_6931(doublePerlinNoiseSampler, d, e), f, g);
    }

    public static class_6910 method_40496(DoublePerlinNoiseSampler doublePerlinNoiseSampler, double d, double e, double f) {
        return class_6916.method_40484(new class_6931(doublePerlinNoiseSampler, 1.0, d), e, f);
    }

    public static class_6910 method_40495(DoublePerlinNoiseSampler doublePerlinNoiseSampler, double d, double e) {
        return class_6916.method_40484(new class_6931(doublePerlinNoiseSampler, 1.0, 1.0), d, e);
    }

    public static class_6910 method_40487(class_6910 arg, class_6910 arg2, double d, DoublePerlinNoiseSampler doublePerlinNoiseSampler) {
        return new class_6940(arg, class_6916.method_40479(), arg2, d, 0.0, doublePerlinNoiseSampler);
    }

    public static class_6910 method_40493(DoublePerlinNoiseSampler doublePerlinNoiseSampler) {
        return new class_6931(doublePerlinNoiseSampler);
    }

    public static class_6910 method_40502(DoublePerlinNoiseSampler doublePerlinNoiseSampler, double d, double e) {
        return new class_6931(doublePerlinNoiseSampler, d, e);
    }

    public static class_6910 method_40494(DoublePerlinNoiseSampler doublePerlinNoiseSampler, double d) {
        return new class_6931(doublePerlinNoiseSampler, d);
    }

    public static class_6910 method_40485(class_6910 arg, double d, double e, class_6910 arg2, class_6910 arg3) {
        return new class_6933(arg, d, e, arg2, arg3);
    }

    public static class_6910 method_40501(DoublePerlinNoiseSampler doublePerlinNoiseSampler) {
        return new class_6937(doublePerlinNoiseSampler);
    }

    public static class_6910 method_40506(DoublePerlinNoiseSampler doublePerlinNoiseSampler) {
        return new class_6938(doublePerlinNoiseSampler);
    }

    public static class_6910 method_40509(DoublePerlinNoiseSampler doublePerlinNoiseSampler) {
        return new class_6934(doublePerlinNoiseSampler);
    }

    public static class_6910 method_40511(DoublePerlinNoiseSampler doublePerlinNoiseSampler) {
        return new class_6935(doublePerlinNoiseSampler);
    }

    public static class_6910 method_40513(DoublePerlinNoiseSampler doublePerlinNoiseSampler) {
        return new class_6936(doublePerlinNoiseSampler);
    }

    public static class_6910 method_40512(class_6910 arg) {
        return new class_6920(arg);
    }

    public static class_6910 method_40482(long l) {
        return new class_6924(l);
    }

    public static class_6910 method_40491(class_6910 arg, DoublePerlinNoiseSampler doublePerlinNoiseSampler, Double2DoubleFunction double2DoubleFunction, double d) {
        return new class_6944(arg, doublePerlinNoiseSampler, double2DoubleFunction, d);
    }

    public static class_6910 method_40492(GenerationShapeConfig generationShapeConfig, class_6910 arg) {
        return new class_6941(generationShapeConfig, arg);
    }

    public static class_6910 method_40486(class_6910 arg, class_6910 arg2) {
        return class_6917.method_40515(class_6917.class_6918.ADD, arg, arg2);
    }

    public static class_6910 method_40500(class_6910 arg, class_6910 arg2) {
        return class_6917.method_40515(class_6917.class_6918.MUL, arg, arg2);
    }

    public static class_6910 method_40505(class_6910 arg, class_6910 arg2) {
        return class_6917.method_40515(class_6917.class_6918.MIN, arg, arg2);
    }

    public static class_6910 method_40508(class_6910 arg, class_6910 arg2) {
        return class_6917.method_40515(class_6917.class_6918.MAX, arg, arg2);
    }

    public static class_6910 method_40489(class_6910 arg, class_6910 arg2, class_6910 arg3, ToFloatFunction<VanillaTerrainParameters.NoisePoint> toFloatFunction, double d, double e) {
        return new class_6942(arg, arg2, arg3, toFloatFunction, d, e);
    }

    public static class_6910 method_40479() {
        return class_6923.field_36553;
    }

    public static class_6910 method_40480(double d) {
        return new class_6923(d);
    }

    public static class_6910 method_40481(int i, int j, double d, double e) {
        return new class_6945(i, j, d, e);
    }

    protected static class_6910 method_40490(class_6910 arg2, class_6925.class_6926 arg22) {
        return new class_6925(arg22, arg2, 0.0, 0.0).method_40469(arg -> arg);
    }

    private static class_6910 method_40484(class_6910 arg, double d, double e) {
        double f = (d + e) * 0.5;
        double g = (e - d) * 0.5;
        return class_6916.method_40486(class_6916.method_40480(f), class_6916.method_40500(class_6916.method_40480(g), arg));
    }

    public static class_6910 method_40498() {
        return class_6919.INSTANCE;
    }

    public static class_6910 method_40503() {
        return class_6921.INSTANCE;
    }

    public static class_6910 method_40488(class_6910 arg, class_6910 arg2, class_6910 arg3) {
        class_6910 lv = class_6916.method_40507(arg);
        class_6910 lv2 = class_6916.method_40486(class_6916.method_40500(lv, class_6916.method_40480(-1.0)), class_6916.method_40480(1.0));
        return class_6916.method_40486(class_6916.method_40500(arg2, lv2), class_6916.method_40500(arg3, lv));
    }

    protected record class_6927(class_6928 type, class_6910 function) implements class_6910
    {
        @Override
        public double method_40464(class_6910.class_6912 arg) {
            return this.function.method_40464(arg);
        }

        @Override
        public void method_40470(double[] ds, class_6910.class_6911 arg) {
            this.function.method_40470(ds, arg);
        }

        @Override
        public class_6910 method_40469(class_6910.class_6915 arg) {
            return (class_6910)arg.apply(new class_6927(this.type, this.function.method_40469(arg)));
        }

        @Override
        public double minValue() {
            return this.function.minValue();
        }

        @Override
        public double maxValue() {
            return this.function.maxValue();
        }

        static enum class_6928 {
            INTERPOLATED,
            FLAT_CACHE,
            CACHE2D,
            CACHE_ONCE,
            CACHE_ALL_IN_CELL;

        }
    }

    record class_6931(DoublePerlinNoiseSampler noise, @Deprecated double xzScale, double yScale) implements class_6910.class_6913
    {
        public class_6931(DoublePerlinNoiseSampler doublePerlinNoiseSampler, double d) {
            this(doublePerlinNoiseSampler, 1.0, d);
        }

        public class_6931(DoublePerlinNoiseSampler doublePerlinNoiseSampler) {
            this(doublePerlinNoiseSampler, 1.0, 1.0);
        }

        @Override
        public double method_40464(class_6910.class_6912 arg) {
            return this.noise.sample((double)arg.blockX() * this.xzScale, (double)arg.blockY() * this.yScale, (double)arg.blockZ() * this.xzScale);
        }

        @Override
        public double minValue() {
            return -this.maxValue();
        }

        @Override
        public double maxValue() {
            return this.noise.method_40554();
        }

        @Deprecated
        public double xzScale() {
            return this.xzScale;
        }
    }

    record class_6940(class_6910 shiftX, class_6910 shiftY, class_6910 shiftZ, double xzScale, double yScale, DoublePerlinNoiseSampler noise) implements class_6910
    {
        @Override
        public double method_40464(class_6910.class_6912 arg) {
            double d = (double)arg.blockX() * this.xzScale + this.shiftX.method_40464(arg);
            double e = (double)arg.blockY() * this.yScale + this.shiftY.method_40464(arg);
            double f = (double)arg.blockZ() * this.xzScale + this.shiftZ.method_40464(arg);
            return this.noise.sample(d, e, f);
        }

        @Override
        public void method_40470(double[] ds, class_6910.class_6911 arg) {
            arg.method_40478(ds, this);
        }

        @Override
        public class_6910 method_40469(class_6910.class_6915 arg) {
            return (class_6910)arg.apply(new class_6940(this.shiftX.method_40469(arg), this.shiftY.method_40469(arg), this.shiftZ.method_40469(arg), this.xzScale, this.yScale, this.noise));
        }

        @Override
        public double minValue() {
            return -this.maxValue();
        }

        @Override
        public double maxValue() {
            return this.noise.method_40554();
        }
    }

    record class_6933(class_6910 input, double minInclusive, double maxExclusive, class_6910 whenInRange, class_6910 whenOutOfRange) implements class_6910
    {
        @Override
        public double method_40464(class_6910.class_6912 arg) {
            double d = this.input.method_40464(arg);
            if (d >= this.minInclusive && d < this.maxExclusive) {
                return this.whenInRange.method_40464(arg);
            }
            return this.whenOutOfRange.method_40464(arg);
        }

        @Override
        public void method_40470(double[] ds, class_6910.class_6911 arg) {
            this.input.method_40470(ds, arg);
            for (int i = 0; i < ds.length; ++i) {
                double d = ds[i];
                ds[i] = d >= this.minInclusive && d < this.maxExclusive ? this.whenInRange.method_40464(arg.method_40477(i)) : this.whenOutOfRange.method_40464(arg.method_40477(i));
            }
        }

        @Override
        public class_6910 method_40469(class_6910.class_6915 arg) {
            return (class_6910)arg.apply(new class_6933(this.input.method_40469(arg), this.minInclusive, this.maxExclusive, this.whenInRange.method_40469(arg), this.whenOutOfRange.method_40469(arg)));
        }

        @Override
        public double minValue() {
            return Math.min(this.whenInRange.minValue(), this.whenOutOfRange.minValue());
        }

        @Override
        public double maxValue() {
            return Math.max(this.whenInRange.maxValue(), this.whenOutOfRange.maxValue());
        }
    }

    record class_6937(DoublePerlinNoiseSampler offsetNoise) implements class_6939
    {
        @Override
        public double method_40464(class_6910.class_6912 arg) {
            return this.method_40525(arg.blockX(), 0.0, arg.blockZ());
        }
    }

    record class_6938(DoublePerlinNoiseSampler offsetNoise) implements class_6939
    {
        @Override
        public double method_40464(class_6910.class_6912 arg) {
            return this.method_40525(arg.blockZ(), arg.blockX(), 0.0);
        }
    }

    record class_6934(DoublePerlinNoiseSampler offsetNoise) implements class_6939
    {
        @Override
        public double method_40464(class_6910.class_6912 arg) {
            return this.method_40525(arg.blockX(), arg.blockY(), arg.blockZ());
        }
    }

    record class_6935(DoublePerlinNoiseSampler offsetNoise) implements class_6939
    {
        @Override
        public double method_40464(class_6910.class_6912 arg) {
            return this.method_40525(arg.blockY(), arg.blockZ(), arg.blockX());
        }
    }

    record class_6936(DoublePerlinNoiseSampler offsetNoise) implements class_6939
    {
        @Override
        public double method_40464(class_6910.class_6912 arg) {
            return this.method_40525(arg.blockZ(), arg.blockX(), arg.blockY());
        }
    }

    record class_6920(class_6910 input) implements class_6943
    {
        @Override
        public double method_40518(class_6910.class_6912 arg, double d) {
            return arg.getBlender().method_39338(arg, d);
        }

        @Override
        public class_6910 method_40469(class_6910.class_6915 arg) {
            return (class_6910)arg.apply(new class_6920(this.input.method_40469(arg)));
        }

        @Override
        public double minValue() {
            return Double.NEGATIVE_INFINITY;
        }

        @Override
        public double maxValue() {
            return Double.POSITIVE_INFINITY;
        }
    }

    static final class class_6924
    implements class_6910.class_6913 {
        final SimplexNoiseSampler field_36554;

        public class_6924(long l) {
            AtomicSimpleRandom abstractRandom = new AtomicSimpleRandom(l);
            abstractRandom.skip(17292);
            this.field_36554 = new SimplexNoiseSampler(abstractRandom);
        }

        @Override
        public double method_40464(class_6910.class_6912 arg) {
            return ((double)TheEndBiomeSource.getNoiseAt(this.field_36554, arg.blockX() / 8, arg.blockZ() / 8) - 8.0) / 128.0;
        }

        @Override
        public double minValue() {
            return -0.84375;
        }

        @Override
        public double maxValue() {
            return 0.5625;
        }
    }

    record class_6944(class_6910 input, DoublePerlinNoiseSampler noise, Double2DoubleFunction rarityValueMapper, double maxRarity) implements class_6943
    {
        @Override
        public double method_40518(class_6910.class_6912 arg, double d) {
            double e = this.rarityValueMapper.get(d);
            return e * Math.abs(this.noise.sample((double)arg.blockX() / e, (double)arg.blockY() / e, (double)arg.blockZ() / e));
        }

        @Override
        public class_6910 method_40469(class_6910.class_6915 arg) {
            this.input.method_40469(arg);
            return (class_6910)arg.apply(new class_6944(this.input.method_40469(arg), this.noise, this.rarityValueMapper, this.maxRarity));
        }

        @Override
        public double minValue() {
            return 0.0;
        }

        @Override
        public double maxValue() {
            return this.maxRarity * this.noise.method_40554();
        }
    }

    record class_6941(GenerationShapeConfig settings, class_6910 input) implements class_6943
    {
        @Override
        public double method_40518(class_6910.class_6912 arg, double d) {
            return class_6954.method_40542(this.settings, d, arg.blockY());
        }

        @Override
        public class_6910 method_40469(class_6910.class_6915 arg) {
            return (class_6910)arg.apply(new class_6941(this.settings, this.input.method_40469(arg)));
        }

        @Override
        public double minValue() {
            return Math.min(this.input.minValue(), Math.min(this.settings.bottomSlide().target(), this.settings.topSlide().target()));
        }

        @Override
        public double maxValue() {
            return Math.max(this.input.maxValue(), Math.max(this.settings.bottomSlide().target(), this.settings.topSlide().target()));
        }
    }

    record class_6917(class_6918 type, class_6910 f1, class_6910 f2, double minValue, double maxValue) implements class_6910
    {
        private static final Logger field_36543 = LogUtils.getLogger();

        public static class_6910 method_40515(class_6918 arg, class_6910 arg2, class_6910 arg3) {
            double i;
            double d = arg2.minValue();
            double e = arg3.minValue();
            double f = arg2.maxValue();
            double g = arg3.maxValue();
            if (arg == class_6918.MIN || arg == class_6918.MAX) {
                boolean bl2;
                boolean bl = d >= g;
                boolean bl3 = bl2 = e >= f;
                if (bl || bl2) {
                    field_36543.warn("Creating a " + arg + " function between two non-overlapping inputs: " + arg2 + " and " + arg3);
                    if (arg == class_6918.MIN) {
                        return bl2 ? arg2 : arg3;
                    }
                    return bl2 ? arg3 : arg2;
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
                if (arg2 instanceof class_6923) {
                    class_6923 lv = (class_6923)arg2;
                    return new class_6929(arg == class_6918.ADD ? class_6929.class_6930.ADD : class_6929.class_6930.MUL, arg3, h, i, lv.value);
                }
                if (arg3 instanceof class_6923) {
                    class_6923 lv = (class_6923)arg3;
                    return new class_6929(arg == class_6918.ADD ? class_6929.class_6930.ADD : class_6929.class_6930.MUL, arg2, h, i, lv.value);
                }
            }
            return new class_6917(arg, arg2, arg3, h, i);
        }

        @Override
        public double method_40464(class_6910.class_6912 arg) {
            double d = this.f1.method_40464(arg);
            return switch (this.type) {
                default -> throw new IncompatibleClassChangeError();
                case class_6918.ADD -> d + this.f2.method_40464(arg);
                case class_6918.MUL -> {
                    if (d == 0.0) {
                        yield 0.0;
                    }
                    yield d * this.f2.method_40464(arg);
                }
                case class_6918.MIN -> {
                    if (d < this.f2.minValue()) {
                        yield d;
                    }
                    yield Math.min(d, this.f2.method_40464(arg));
                }
                case class_6918.MAX -> d > this.f2.maxValue() ? d : Math.max(d, this.f2.method_40464(arg));
            };
        }

        @Override
        public void method_40470(double[] ds, class_6910.class_6911 arg) {
            this.f1.method_40470(ds, arg);
            switch (this.type) {
                case ADD: {
                    double[] es = new double[ds.length];
                    this.f2.method_40470(es, arg);
                    for (int i = 0; i < ds.length; ++i) {
                        ds[i] = ds[i] + es[i];
                    }
                    break;
                }
                case MUL: {
                    for (int j = 0; j < ds.length; ++j) {
                        double d = ds[j];
                        ds[j] = d == 0.0 ? 0.0 : d * this.f2.method_40464(arg.method_40477(j));
                    }
                    break;
                }
                case MIN: {
                    double e = this.f2.minValue();
                    for (int k = 0; k < ds.length; ++k) {
                        double f = ds[k];
                        ds[k] = f < e ? f : Math.min(f, this.f2.method_40464(arg.method_40477(k)));
                    }
                    break;
                }
                case MAX: {
                    double e = this.f2.maxValue();
                    for (int k = 0; k < ds.length; ++k) {
                        double f = ds[k];
                        ds[k] = f > e ? f : Math.max(f, this.f2.method_40464(arg.method_40477(k)));
                    }
                    break;
                }
            }
        }

        @Override
        public class_6910 method_40469(class_6910.class_6915 arg) {
            return (class_6910)arg.apply(class_6917.method_40515(this.type, this.f1.method_40469(arg), this.f2.method_40469(arg)));
        }

        static enum class_6918 {
            ADD,
            MUL,
            MIN,
            MAX;

        }
    }

    record class_6942(class_6910 continentalness, class_6910 erosion, class_6910 weirdness, ToFloatFunction<VanillaTerrainParameters.NoisePoint> spline, double minValue, double maxValue) implements class_6910
    {
        @Override
        public double method_40464(class_6910.class_6912 arg) {
            return MathHelper.clamp((double)this.spline.apply(VanillaTerrainParameters.createNoisePoint((float)this.continentalness.method_40464(arg), (float)this.erosion.method_40464(arg), (float)this.weirdness.method_40464(arg))), this.minValue, this.maxValue);
        }

        @Override
        public void method_40470(double[] ds, class_6910.class_6911 arg) {
            for (int i = 0; i < ds.length; ++i) {
                ds[i] = this.method_40464(arg.method_40477(i));
            }
        }

        @Override
        public class_6910 method_40469(class_6910.class_6915 arg) {
            return (class_6910)arg.apply(new class_6942(this.continentalness.method_40469(arg), this.erosion.method_40469(arg), this.weirdness.method_40469(arg), this.spline, this.minValue, this.maxValue));
        }
    }

    record class_6923(double value) implements class_6910.class_6913
    {
        static final class_6923 field_36553 = new class_6923(0.0);

        @Override
        public double method_40464(class_6910.class_6912 arg) {
            return this.value;
        }

        @Override
        public void method_40470(double[] ds, class_6910.class_6911 arg) {
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
    }

    record class_6945(int fromY, int toY, double fromValue, double toValue) implements class_6910.class_6913
    {
        @Override
        public double method_40464(class_6910.class_6912 arg) {
            return MathHelper.clampedLerpFromProgress((double)arg.blockY(), (double)this.fromY, (double)this.toY, this.fromValue, this.toValue);
        }

        @Override
        public double minValue() {
            return Math.min(this.fromValue, this.toValue);
        }

        @Override
        public double maxValue() {
            return Math.max(this.fromValue, this.toValue);
        }
    }

    protected record class_6925(class_6926 type, class_6910 input, double minValue, double maxValue) implements class_6932
    {
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
        public double method_40520(double d) {
            return class_6925.method_40521(this.type, d);
        }

        @Override
        public class_6910 method_40469(class_6910.class_6915 arg) {
            class_6910 lv = this.input.method_40469(arg);
            double d = lv.minValue();
            double e = class_6925.method_40521(this.type, d);
            double f = class_6925.method_40521(this.type, lv.maxValue());
            if (this.type == class_6926.ABS || this.type == class_6926.SQUARE) {
                return new class_6925(this.type, lv, Math.max(0.0, d), Math.max(e, f));
            }
            return new class_6925(this.type, lv, e, f);
        }

        static enum class_6926 {
            ABS,
            SQUARE,
            CUBE,
            HALF_NEGATIVE,
            QUARTER_NEGATIVE,
            SQUEEZE;

        }
    }

    protected static enum class_6919 implements class_6910.class_6913
    {
        INSTANCE;


        @Override
        public double method_40464(class_6910.class_6912 arg) {
            return 1.0;
        }

        @Override
        public void method_40470(double[] ds, class_6910.class_6911 arg) {
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
    }

    protected static enum class_6921 implements class_6910.class_6913
    {
        INSTANCE;


        @Override
        public double method_40464(class_6910.class_6912 arg) {
            return 0.0;
        }

        @Override
        public void method_40470(double[] ds, class_6910.class_6911 arg) {
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

    record class_6929(class_6930 type, class_6910 input, double minValue, double maxValue, double argument) implements class_6932
    {
        @Override
        public double method_40520(double d) {
            return switch (this.type) {
                default -> throw new IncompatibleClassChangeError();
                case class_6930.MUL -> d * this.argument;
                case class_6930.ADD -> d + this.argument;
            };
        }

        @Override
        public class_6910 method_40469(class_6910.class_6915 arg) {
            double g;
            double f;
            class_6910 lv = this.input.method_40469(arg);
            double d = lv.minValue();
            double e = lv.maxValue();
            if (this.type == class_6930.ADD) {
                f = d + this.argument;
                g = e + this.argument;
            } else if (this.argument >= 0.0) {
                f = d * this.argument;
                g = e * this.argument;
            } else {
                f = e * this.argument;
                g = d * this.argument;
            }
            return new class_6929(this.type, lv, f, g, this.argument);
        }

        static enum class_6930 {
            MUL,
            ADD;

        }
    }

    protected record class_6922(class_6910 input, double minValue, double maxValue) implements class_6932
    {
        @Override
        public double method_40520(double d) {
            return MathHelper.clamp(d, this.minValue, this.maxValue);
        }

        @Override
        public class_6910 method_40469(class_6910.class_6915 arg) {
            return new class_6922(this.input.method_40469(arg), this.minValue, this.maxValue);
        }
    }

    static interface class_6939
    extends class_6910.class_6913 {
        public DoublePerlinNoiseSampler offsetNoise();

        @Override
        default public double minValue() {
            return -this.maxValue();
        }

        @Override
        default public double maxValue() {
            return this.offsetNoise().method_40554() * 4.0;
        }

        default public double method_40525(double d, double e, double f) {
            return this.offsetNoise().sample(d * 0.25, e * 0.25, f * 0.25) * 4.0;
        }
    }

    static interface class_6932
    extends class_6910 {
        public class_6910 input();

        @Override
        default public double method_40464(class_6910.class_6912 arg) {
            return this.method_40520(this.input().method_40464(arg));
        }

        @Override
        default public void method_40470(double[] ds, class_6910.class_6911 arg) {
            this.input().method_40470(ds, arg);
            for (int i = 0; i < ds.length; ++i) {
                ds[i] = this.method_40520(ds[i]);
            }
        }

        public double method_40520(double var1);
    }

    static interface class_6943
    extends class_6910 {
        public class_6910 input();

        @Override
        default public double method_40464(class_6910.class_6912 arg) {
            return this.method_40518(arg, this.input().method_40464(arg));
        }

        @Override
        default public void method_40470(double[] ds, class_6910.class_6911 arg) {
            this.input().method_40470(ds, arg);
            for (int i = 0; i < ds.length; ++i) {
                ds[i] = this.method_40518(arg.method_40477(i), ds[i]);
            }
        }

        public double method_40518(class_6910.class_6912 var1, double var2);
    }
}

