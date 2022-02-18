/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source.util;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.function.ToFloatFunction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Spline;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

public record VanillaTerrainParameters(Spline<NoisePoint> offsetSpline, Spline<NoisePoint> factorSpline, Spline<NoisePoint> peakSpline) {
    private static final Codec<Spline<NoisePoint>> field_35457 = Spline.createCodec(LocationFunction.field_35464);
    public static final Codec<VanillaTerrainParameters> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)field_35457.fieldOf("offset")).forGetter(VanillaTerrainParameters::offsetSpline), ((MapCodec)field_35457.fieldOf("factor")).forGetter(VanillaTerrainParameters::factorSpline), ((MapCodec)field_35457.fieldOf("jaggedness")).forGetter(vanillaTerrainParameters -> vanillaTerrainParameters.peakSpline)).apply((Applicative<VanillaTerrainParameters, ?>)instance, VanillaTerrainParameters::new));
    private static final float OFFSET_VALUE_OFFSET = -0.50375f;
    private static final ToFloatFunction<Float> field_35673 = float_ -> float_.floatValue();

    private static float method_39534(float f) {
        return f < 0.0f ? f : f * 2.0f;
    }

    private static float method_39535(float f) {
        return 1.25f - 6.25f / (f + 5.0f);
    }

    private static float method_39536(float f) {
        return f * 2.0f;
    }

    public static VanillaTerrainParameters createSurfaceParameters(boolean amplified) {
        ToFloatFunction<Float> toFloatFunction = amplified ? VanillaTerrainParameters::method_39534 : field_35673;
        ToFloatFunction<Float> toFloatFunction2 = amplified ? VanillaTerrainParameters::method_39535 : field_35673;
        ToFloatFunction<Float> toFloatFunction3 = amplified ? VanillaTerrainParameters::method_39536 : field_35673;
        Spline<NoisePoint> spline = VanillaTerrainParameters.createLandSpline(-0.15f, 0.0f, 0.0f, 0.1f, 0.0f, -0.03f, false, false, toFloatFunction);
        Spline<NoisePoint> spline2 = VanillaTerrainParameters.createLandSpline(-0.1f, 0.03f, 0.1f, 0.1f, 0.01f, -0.03f, false, false, toFloatFunction);
        Spline<NoisePoint> spline3 = VanillaTerrainParameters.createLandSpline(-0.1f, 0.03f, 0.1f, 0.7f, 0.01f, -0.03f, true, true, toFloatFunction);
        Spline<NoisePoint> spline4 = VanillaTerrainParameters.createLandSpline(-0.05f, 0.03f, 0.1f, 1.0f, 0.01f, 0.01f, true, true, toFloatFunction);
        float f = -0.51f;
        float g = -0.4f;
        float h = 0.1f;
        float i = -0.15f;
        Spline<NoisePoint> spline5 = Spline.builder(LocationFunction.CONTINENTS, toFloatFunction).add(-1.1f, 0.044f, 0.0f).add(-1.02f, -0.2222f, 0.0f).add(-0.51f, -0.2222f, 0.0f).add(-0.44f, -0.12f, 0.0f).add(-0.18f, -0.12f, 0.0f).add(-0.16f, spline, 0.0f).add(-0.15f, spline, 0.0f).add(-0.1f, spline2, 0.0f).add(0.25f, spline3, 0.0f).add(1.0f, spline4, 0.0f).build();
        Spline<NoisePoint> spline6 = Spline.builder(LocationFunction.CONTINENTS, field_35673).add(-0.19f, 3.95f, 0.0f).add(-0.15f, VanillaTerrainParameters.buildErosionFactorSpline(6.25f, true, field_35673), 0.0f).add(-0.1f, VanillaTerrainParameters.buildErosionFactorSpline(5.47f, true, toFloatFunction2), 0.0f).add(0.03f, VanillaTerrainParameters.buildErosionFactorSpline(5.08f, true, toFloatFunction2), 0.0f).add(0.06f, VanillaTerrainParameters.buildErosionFactorSpline(4.69f, false, toFloatFunction2), 0.0f).build();
        float j = 0.65f;
        Spline<NoisePoint> spline7 = Spline.builder(LocationFunction.CONTINENTS, toFloatFunction3).add(-0.11f, 0.0f, 0.0f).add(0.03f, VanillaTerrainParameters.method_38856(1.0f, 0.5f, 0.0f, 0.0f, toFloatFunction3), 0.0f).add(0.65f, VanillaTerrainParameters.method_38856(1.0f, 1.0f, 1.0f, 0.0f, toFloatFunction3), 0.0f).build();
        return new VanillaTerrainParameters(spline5, spline6, spline7);
    }

    private static Spline<NoisePoint> method_38856(float f, float g, float h, float i, ToFloatFunction<Float> toFloatFunction) {
        float j = -0.5775f;
        Spline<NoisePoint> spline = VanillaTerrainParameters.method_38855(f, h, toFloatFunction);
        Spline<NoisePoint> spline2 = VanillaTerrainParameters.method_38855(g, i, toFloatFunction);
        return Spline.builder(LocationFunction.EROSION, toFloatFunction).add(-1.0f, spline, 0.0f).add(-0.78f, spline2, 0.0f).add(-0.5775f, spline2, 0.0f).add(-0.375f, 0.0f, 0.0f).build();
    }

    private static Spline<NoisePoint> method_38855(float f, float g, ToFloatFunction<Float> toFloatFunction) {
        float h = VanillaTerrainParameters.getNormalizedWeirdness(0.4f);
        float i = VanillaTerrainParameters.getNormalizedWeirdness(0.56666666f);
        float j = (h + i) / 2.0f;
        Spline.Builder<NoisePoint> builder = Spline.builder(LocationFunction.RIDGES, toFloatFunction);
        builder.add(h, 0.0f, 0.0f);
        if (g > 0.0f) {
            builder.add(j, VanillaTerrainParameters.method_38857(g, toFloatFunction), 0.0f);
        } else {
            builder.add(j, 0.0f, 0.0f);
        }
        if (f > 0.0f) {
            builder.add(1.0f, VanillaTerrainParameters.method_38857(f, toFloatFunction), 0.0f);
        } else {
            builder.add(1.0f, 0.0f, 0.0f);
        }
        return builder.build();
    }

    private static Spline<NoisePoint> method_38857(float f, ToFloatFunction<Float> toFloatFunction) {
        float g = 0.63f * f;
        float h = 0.3f * f;
        return Spline.builder(LocationFunction.WEIRDNESS, toFloatFunction).add(-0.01f, g, 0.0f).add(0.01f, h, 0.0f).build();
    }

    private static Spline<NoisePoint> buildErosionFactorSpline(float value, boolean bl, ToFloatFunction<Float> toFloatFunction) {
        Spline<NoisePoint> spline = Spline.builder(LocationFunction.WEIRDNESS, toFloatFunction).add(-0.2f, 6.3f, 0.0f).add(0.2f, value, 0.0f).build();
        Spline.Builder<NoisePoint> builder = Spline.builder(LocationFunction.EROSION, toFloatFunction).add(-0.6f, spline, 0.0f).add(-0.5f, Spline.builder(LocationFunction.WEIRDNESS, toFloatFunction).add(-0.05f, 6.3f, 0.0f).add(0.05f, 2.67f, 0.0f).build(), 0.0f).add(-0.35f, spline, 0.0f).add(-0.25f, spline, 0.0f).add(-0.1f, Spline.builder(LocationFunction.WEIRDNESS, toFloatFunction).add(-0.05f, 2.67f, 0.0f).add(0.05f, 6.3f, 0.0f).build(), 0.0f).add(0.03f, spline, 0.0f);
        if (bl) {
            Spline<NoisePoint> spline2 = Spline.builder(LocationFunction.WEIRDNESS, toFloatFunction).add(0.0f, value, 0.0f).add(0.1f, 0.625f, 0.0f).build();
            Spline<NoisePoint> spline3 = Spline.builder(LocationFunction.RIDGES, toFloatFunction).add(-0.9f, value, 0.0f).add(-0.69f, spline2, 0.0f).build();
            builder.add(0.35f, value, 0.0f).add(0.45f, spline3, 0.0f).add(0.55f, spline3, 0.0f).add(0.62f, value, 0.0f);
        } else {
            Spline<NoisePoint> spline2 = Spline.builder(LocationFunction.RIDGES, toFloatFunction).add(-0.7f, spline, 0.0f).add(-0.15f, 1.37f, 0.0f).build();
            Spline<NoisePoint> spline3 = Spline.builder(LocationFunction.RIDGES, toFloatFunction).add(0.45f, spline, 0.0f).add(0.7f, 1.56f, 0.0f).build();
            builder.add(0.05f, spline3, 0.0f).add(0.4f, spline3, 0.0f).add(0.45f, spline2, 0.0f).add(0.55f, spline2, 0.0f).add(0.58f, value, 0.0f);
        }
        return builder.build();
    }

    private static float method_38210(float f, float g, float h, float i) {
        return (g - f) / (i - h);
    }

    private static Spline<NoisePoint> method_38219(float f, boolean bl, ToFloatFunction<Float> toFloatFunction) {
        Spline.Builder<NoisePoint> builder = Spline.builder(LocationFunction.RIDGES, toFloatFunction);
        float g = -0.7f;
        float h = -1.0f;
        float i = VanillaTerrainParameters.getOffsetValue(-1.0f, f, -0.7f);
        float j = 1.0f;
        float k = VanillaTerrainParameters.getOffsetValue(1.0f, f, -0.7f);
        float l = VanillaTerrainParameters.method_38217(f);
        float m = -0.65f;
        if (-0.65f < l && l < 1.0f) {
            float n = VanillaTerrainParameters.getOffsetValue(-0.65f, f, -0.7f);
            float o = -0.75f;
            float p = VanillaTerrainParameters.getOffsetValue(-0.75f, f, -0.7f);
            float q = VanillaTerrainParameters.method_38210(i, p, -1.0f, -0.75f);
            builder.add(-1.0f, i, q);
            builder.add(-0.75f, p, 0.0f);
            builder.add(-0.65f, n, 0.0f);
            float r = VanillaTerrainParameters.getOffsetValue(l, f, -0.7f);
            float s = VanillaTerrainParameters.method_38210(r, k, l, 1.0f);
            float t = 0.01f;
            builder.add(l - 0.01f, r, 0.0f);
            builder.add(l, r, s);
            builder.add(1.0f, k, s);
        } else {
            float n = VanillaTerrainParameters.method_38210(i, k, -1.0f, 1.0f);
            if (bl) {
                builder.add(-1.0f, Math.max(0.2f, i), 0.0f);
                builder.add(0.0f, MathHelper.lerp(0.5f, i, k), n);
            } else {
                builder.add(-1.0f, i, n);
            }
            builder.add(1.0f, k, n);
        }
        return builder.build();
    }

    private static float getOffsetValue(float weirdness, float continentalness, float weirdnessThreshold) {
        float f = 1.17f;
        float g = 0.46082947f;
        float h = 1.0f - (1.0f - continentalness) * 0.5f;
        float i = 0.5f * (1.0f - continentalness);
        float j = (weirdness + 1.17f) * 0.46082947f;
        float k = j * h - i;
        if (weirdness < weirdnessThreshold) {
            return Math.max(k, -0.2222f);
        }
        return Math.max(k, 0.0f);
    }

    private static float method_38217(float continentalness) {
        float f = 1.17f;
        float g = 0.46082947f;
        float h = 1.0f - (1.0f - continentalness) * 0.5f;
        float i = 0.5f * (1.0f - continentalness);
        return i / (0.46082947f * h) - 1.17f;
    }

    private static Spline<NoisePoint> createLandSpline(float f, float g, float h, float i, float j, float k, boolean bl, boolean bl2, ToFloatFunction<Float> toFloatFunction) {
        float l = 0.6f;
        float m = 0.5f;
        float n = 0.5f;
        Spline<NoisePoint> spline = VanillaTerrainParameters.method_38219(MathHelper.lerp(i, 0.6f, 1.5f), bl2, toFloatFunction);
        Spline<NoisePoint> spline2 = VanillaTerrainParameters.method_38219(MathHelper.lerp(i, 0.6f, 1.0f), bl2, toFloatFunction);
        Spline<NoisePoint> spline3 = VanillaTerrainParameters.method_38219(i, bl2, toFloatFunction);
        Spline<NoisePoint> spline4 = VanillaTerrainParameters.createFlatOffsetSpline(f - 0.15f, 0.5f * i, MathHelper.lerp(0.5f, 0.5f, 0.5f) * i, 0.5f * i, 0.6f * i, 0.5f, toFloatFunction);
        Spline<NoisePoint> spline5 = VanillaTerrainParameters.createFlatOffsetSpline(f, j * i, g * i, 0.5f * i, 0.6f * i, 0.5f, toFloatFunction);
        Spline<NoisePoint> spline6 = VanillaTerrainParameters.createFlatOffsetSpline(f, j, j, g, h, 0.5f, toFloatFunction);
        Spline<NoisePoint> spline7 = VanillaTerrainParameters.createFlatOffsetSpline(f, j, j, g, h, 0.5f, toFloatFunction);
        Spline<NoisePoint> spline8 = Spline.builder(LocationFunction.RIDGES, toFloatFunction).add(-1.0f, f, 0.0f).add(-0.4f, spline6, 0.0f).add(0.0f, h + 0.07f, 0.0f).build();
        Spline<NoisePoint> spline9 = VanillaTerrainParameters.createFlatOffsetSpline(-0.02f, k, k, g, h, 0.0f, toFloatFunction);
        Spline.Builder<NoisePoint> builder = Spline.builder(LocationFunction.EROSION, toFloatFunction).add(-0.85f, spline, 0.0f).add(-0.7f, spline2, 0.0f).add(-0.4f, spline3, 0.0f).add(-0.35f, spline4, 0.0f).add(-0.1f, spline5, 0.0f).add(0.2f, spline6, 0.0f);
        if (bl) {
            builder.add(0.4f, spline7, 0.0f).add(0.45f, spline8, 0.0f).add(0.55f, spline8, 0.0f).add(0.58f, spline7, 0.0f);
        }
        builder.add(0.7f, spline9, 0.0f);
        return builder.build();
    }

    private static Spline<NoisePoint> createFlatOffsetSpline(float f, float g, float h, float i, float j, float k, ToFloatFunction<Float> toFloatFunction) {
        float l = Math.max(0.5f * (g - f), k);
        float m = 5.0f * (h - g);
        return Spline.builder(LocationFunction.RIDGES, toFloatFunction).add(-1.0f, f, l).add(-0.4f, g, Math.min(l, m)).add(0.0f, h, m).add(0.4f, i, 2.0f * (i - h)).add(1.0f, j, 0.7f * (j - i)).build();
    }

    public void writeDebugBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters) {
        Float float_;
        int n;
        MultiNoiseUtil.ParameterRange parameterRange = MultiNoiseUtil.ParameterRange.of(-1.0f, 1.0f);
        parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(parameterRange, parameterRange, parameterRange, parameterRange, MultiNoiseUtil.ParameterRange.of(0.0f), parameterRange, 0.01f), BiomeKeys.PLAINS));
        Spline.Implementation implementation = (Spline.Implementation)VanillaTerrainParameters.createLandSpline(-0.15f, 0.0f, 0.0f, 0.1f, 0.0f, -0.03f, false, false, field_35673);
        RegistryKey<Biome> registryKey = BiomeKeys.DESERT;
        float[] fArray = implementation.locations();
        int n2 = fArray.length;
        for (n = 0; n < n2; ++n) {
            float_ = Float.valueOf(fArray[n]);
            parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(parameterRange, parameterRange, parameterRange, MultiNoiseUtil.ParameterRange.of(float_.floatValue()), MultiNoiseUtil.ParameterRange.of(0.0f), parameterRange, 0.0f), registryKey));
            registryKey = registryKey == BiomeKeys.DESERT ? BiomeKeys.BADLANDS : BiomeKeys.DESERT;
        }
        fArray = ((Spline.Implementation)this.offsetSpline).locations();
        n2 = fArray.length;
        for (n = 0; n < n2; ++n) {
            float_ = Float.valueOf(fArray[n]);
            parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(parameterRange, parameterRange, MultiNoiseUtil.ParameterRange.of(float_.floatValue()), parameterRange, MultiNoiseUtil.ParameterRange.of(0.0f), parameterRange, 0.0f), BiomeKeys.SNOWY_TAIGA));
        }
    }

    @Debug
    public Spline<NoisePoint> offsetSpline() {
        return this.offsetSpline;
    }

    @Debug
    public Spline<NoisePoint> factorSpline() {
        return this.factorSpline;
    }

    @Debug
    public Spline<NoisePoint> peakSpline() {
        return this.peakSpline;
    }

    public float getOffset(NoisePoint point) {
        return this.offsetSpline.apply(point) + -0.50375f;
    }

    public float getFactor(NoisePoint point) {
        return this.factorSpline.apply(point);
    }

    public float getPeak(NoisePoint point) {
        return this.peakSpline.apply(point);
    }

    public static NoisePoint createNoisePoint(float f, float g, float h) {
        return new NoisePoint(f, g, VanillaTerrainParameters.getNormalizedWeirdness(h), h);
    }

    public static float getNormalizedWeirdness(float weirdness) {
        return -(Math.abs(Math.abs(weirdness) - 0.6666667f) - 0.33333334f) * 3.0f;
    }

    @VisibleForTesting
    protected static enum LocationFunction implements StringIdentifiable,
    ToFloatFunction<NoisePoint>
    {
        CONTINENTS(NoisePoint::continentalnessNoise, "continents"),
        EROSION(NoisePoint::erosionNoise, "erosion"),
        WEIRDNESS(NoisePoint::weirdnessNoise, "weirdness"),
        RIDGES(NoisePoint::normalizedWeirdness, "ridges");

        private static final Map<String, LocationFunction> field_35462;
        private static final Codec<LocationFunction> CODEC;
        static final Codec<ToFloatFunction<NoisePoint>> field_35464;
        private final ToFloatFunction<NoisePoint> noiseFunction;
        private final String id;

        private LocationFunction(ToFloatFunction<NoisePoint> noiseFunction, String id) {
            this.noiseFunction = noiseFunction;
            this.id = id;
        }

        @Override
        public String asString() {
            return this.id;
        }

        public String toString() {
            return this.id;
        }

        @Override
        public float apply(NoisePoint noisePoint) {
            return this.noiseFunction.apply(noisePoint);
        }

        @Override
        public /* synthetic */ float apply(Object object) {
            return this.apply((NoisePoint)object);
        }

        static {
            field_35462 = Arrays.stream(LocationFunction.values()).collect(Collectors.toMap(LocationFunction::asString, locationFunction -> locationFunction));
            CODEC = StringIdentifiable.createCodec(LocationFunction::values, field_35462::get);
            field_35464 = CODEC.flatComapMap(locationFunction -> locationFunction, toFloatFunction -> {
                DataResult<Object> dataResult;
                if (toFloatFunction instanceof LocationFunction) {
                    LocationFunction locationFunction = (LocationFunction)toFloatFunction;
                    dataResult = DataResult.success(locationFunction);
                } else {
                    dataResult = DataResult.error("Not a coordinate resolver: " + toFloatFunction);
                }
                return dataResult;
            });
        }
    }

    public record NoisePoint(float continentalnessNoise, float erosionNoise, float normalizedWeirdness, float weirdnessNoise) {
    }
}

