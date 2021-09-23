/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source.util;

import com.mojang.datafixers.util.Pair;
import java.util.function.Consumer;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.function.ToFloatFunction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Spline;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

public final class VanillaTerrainParameters {
    private static final float OFFSET_VALUE_OFFSET = 0.015f;
    static final ToFloatFunction<NoisePoint> CONTINENTALNESS = new ToFloatFunction<NoisePoint>(){

        @Override
        public float apply(NoisePoint noisePoint) {
            return noisePoint.continentalnessNoise;
        }

        public String toString() {
            return "continents";
        }

        @Override
        public /* synthetic */ float apply(Object object) {
            return this.apply((NoisePoint)object);
        }
    };
    static final ToFloatFunction<NoisePoint> EROSION = new ToFloatFunction<NoisePoint>(){

        @Override
        public float apply(NoisePoint noisePoint) {
            return noisePoint.erosionNoise;
        }

        public String toString() {
            return "erosion";
        }

        @Override
        public /* synthetic */ float apply(Object object) {
            return this.apply((NoisePoint)object);
        }
    };
    static final ToFloatFunction<NoisePoint> WEIRDNESS = new ToFloatFunction<NoisePoint>(){

        @Override
        public float apply(NoisePoint noisePoint) {
            return noisePoint.weirdnessNoise;
        }

        public String toString() {
            return "weirdness";
        }

        @Override
        public /* synthetic */ float apply(Object object) {
            return this.apply((NoisePoint)object);
        }
    };
    static final ToFloatFunction<NoisePoint> NORMALIZED_WEIRDNESS = new ToFloatFunction<NoisePoint>(){

        @Override
        public float apply(NoisePoint noisePoint) {
            return noisePoint.normalizedWeirdness;
        }

        public String toString() {
            return "ridges";
        }

        @Override
        public /* synthetic */ float apply(Object object) {
            return this.apply((NoisePoint)object);
        }
    };
    @Debug
    public Spline<NoisePoint> offsetSpline;
    @Debug
    public Spline<NoisePoint> factorSpline;
    @Debug
    public Spline<NoisePoint> peakSpline;

    public VanillaTerrainParameters() {
        Spline<NoisePoint> spline = VanillaTerrainParameters.createLandSpline(-0.15f, 0.0f, 0.0f, 0.1f, 0.0f, -0.03f, false, false);
        Spline<NoisePoint> spline2 = VanillaTerrainParameters.createLandSpline(-0.1f, 0.03f, 0.1f, 0.1f, 0.01f, -0.03f, false, false);
        Spline<NoisePoint> spline3 = VanillaTerrainParameters.createLandSpline(-0.1f, 0.03f, 0.1f, 0.7f, 0.01f, -0.03f, true, true);
        Spline<NoisePoint> spline4 = VanillaTerrainParameters.createLandSpline(-0.05f, 0.03f, 0.1f, 1.0f, 0.01f, 0.01f, true, true);
        float f = -0.51f;
        float g = -0.4f;
        float h = 0.1f;
        float i = -0.15f;
        this.offsetSpline = Spline.builder(CONTINENTALNESS).add(-1.1f, 0.044f, 0.0f).add(-1.02f, -0.2222f, 0.0f).add(-0.51f, -0.2222f, 0.0f).add(-0.44f, -0.12f, 0.0f).add(-0.18f, -0.12f, 0.0f).add(-0.16f, spline, 0.0f).add(-0.15f, spline, 0.0f).add(-0.1f, spline2, 0.0f).add(0.25f, spline3, 0.0f).add(1.0f, spline4, 0.0f).build();
        this.factorSpline = Spline.builder(CONTINENTALNESS).add(-0.19f, 3.95f, 0.0f).add(-0.15f, VanillaTerrainParameters.buildErosionFactorSpline(6.25f, true), 0.0f).add(-0.1f, VanillaTerrainParameters.buildErosionFactorSpline(5.47f, true), 0.0f).add(0.03f, VanillaTerrainParameters.buildErosionFactorSpline(5.08f, true), 0.0f).add(0.06f, VanillaTerrainParameters.buildErosionFactorSpline(4.69f, false), 0.0f).build();
        this.peakSpline = Spline.builder(CONTINENTALNESS).add(0.1f, 0.0f, 0.0f).add(0.2f, Spline.builder(EROSION).add(-0.8f, Spline.builder(NORMALIZED_WEIRDNESS).add(-1.0f, 0.0f, 0.0f).add(0.2f, 0.0f, 0.0f).add(1.0f, Spline.builder(WEIRDNESS).add(-0.01f, 0.625f, 0.0f).add(0.01f, 0.15625f, 0.0f).build(), 0.0f).build(), 0.0f).add(-0.4f, 0.0f, 0.0f).build(), 0.0f).build();
    }

    private static Spline<NoisePoint> buildErosionFactorSpline(float value, boolean bl) {
        Spline<NoisePoint> spline = Spline.builder(WEIRDNESS).add(-0.2f, 6.3f, 0.0f).add(0.2f, value, 0.0f).build();
        Spline.Builder<NoisePoint> builder = Spline.builder(EROSION).add(-0.6f, spline, 0.0f).add(-0.5f, Spline.builder(WEIRDNESS).add(-0.05f, 6.3f, 0.0f).add(0.05f, 2.67f, 0.0f).build(), 0.0f).add(-0.35f, spline, 0.0f).add(-0.25f, spline, 0.0f).add(-0.1f, Spline.builder(WEIRDNESS).add(-0.05f, 2.67f, 0.0f).add(0.05f, 6.3f, 0.0f).build(), 0.0f).add(0.03f, spline, 0.0f);
        if (bl) {
            Spline<NoisePoint> spline2 = Spline.builder(WEIRDNESS).add(0.0f, value, 0.0f).add(0.1f, 0.625f, 0.0f).build();
            Spline<NoisePoint> spline3 = Spline.builder(NORMALIZED_WEIRDNESS).add(-0.9f, value, 0.0f).add(-0.69f, spline2, 0.0f).build();
            builder.add(0.35f, value, 0.0f).add(0.45f, spline3, 0.0f).add(0.55f, spline3, 0.0f).add(0.62f, value, 0.0f);
        } else {
            Spline<NoisePoint> spline2 = Spline.builder(NORMALIZED_WEIRDNESS).add(-0.7f, spline, 0.0f).add(-0.15f, 1.37f, 0.0f).build();
            Spline<NoisePoint> spline3 = Spline.builder(NORMALIZED_WEIRDNESS).add(0.45f, spline, 0.0f).add(0.7f, 1.56f, 0.0f).build();
            builder.add(0.05f, spline3, 0.0f).add(0.4f, spline3, 0.0f).add(0.45f, spline2, 0.0f).add(0.55f, spline2, 0.0f).add(0.58f, value, 0.0f);
        }
        return builder.build();
    }

    private static float method_38210(float f, float g, float h, float i) {
        return (g - f) / (i - h);
    }

    private static Spline<NoisePoint> method_38219(float f, boolean bl) {
        Spline.Builder<NoisePoint> builder = Spline.builder(NORMALIZED_WEIRDNESS);
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

    private static Spline<NoisePoint> createLandSpline(float f, float g, float h, float i, float j, float k, boolean bl, boolean bl2) {
        float l = 0.6f;
        float m = 0.5f;
        float n = 0.5f;
        Spline<NoisePoint> spline = VanillaTerrainParameters.method_38219(MathHelper.lerp(i, 0.6f, 1.5f), bl2);
        Spline<NoisePoint> spline2 = VanillaTerrainParameters.method_38219(MathHelper.lerp(i, 0.6f, 1.0f), bl2);
        Spline<NoisePoint> spline3 = VanillaTerrainParameters.method_38219(i, bl2);
        Spline<NoisePoint> spline4 = VanillaTerrainParameters.createFlatOffsetSpline(f - 0.15f, 0.5f * i, MathHelper.lerp(0.5f, 0.5f, 0.5f) * i, 0.5f * i, 0.6f * i, 0.5f);
        Spline<NoisePoint> spline5 = VanillaTerrainParameters.createFlatOffsetSpline(f, j * i, g * i, 0.5f * i, 0.6f * i, 0.5f);
        Spline<NoisePoint> spline6 = VanillaTerrainParameters.createFlatOffsetSpline(f, j, j, g, h, 0.5f);
        Spline<NoisePoint> spline7 = VanillaTerrainParameters.createFlatOffsetSpline(f, j, j, g, h, 0.5f);
        Spline<NoisePoint> spline8 = Spline.builder(NORMALIZED_WEIRDNESS).add(-1.0f, f, 0.0f).add(-0.4f, spline6, 0.0f).add(0.0f, h + 0.07f, 0.0f).build();
        Spline<NoisePoint> spline9 = VanillaTerrainParameters.createFlatOffsetSpline(-0.02f, k, k, g, h, 0.0f);
        Spline.Builder<NoisePoint> builder = Spline.builder(EROSION).add(-0.85f, spline, 0.0f).add(-0.7f, spline2, 0.0f).add(-0.4f, spline3, 0.0f).add(-0.35f, spline4, 0.0f).add(-0.1f, spline5, 0.0f).add(0.2f, spline6, 0.0f);
        if (bl) {
            builder.add(0.4f, spline7, 0.0f).add(0.45f, spline8, 0.0f).add(0.55f, spline8, 0.0f).add(0.58f, spline7, 0.0f);
        }
        builder.add(0.7f, spline9, 0.0f);
        return builder.build();
    }

    private static Spline<NoisePoint> createFlatOffsetSpline(float f, float g, float h, float i, float j, float k) {
        float l = Math.max(0.5f * (g - f), k);
        float m = 5.0f * (h - g);
        return Spline.builder(NORMALIZED_WEIRDNESS).add(-1.0f, f, l).add(-0.4f, g, Math.min(l, m)).add(0.0f, h, m).add(0.4f, i, 2.0f * (i - h)).add(1.0f, j, 0.7f * (j - i)).build();
    }

    public void writeDebugBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters) {
        MultiNoiseUtil.ParameterRange parameterRange = MultiNoiseUtil.ParameterRange.of(-1.0f, 1.0f);
        parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(parameterRange, parameterRange, parameterRange, parameterRange, MultiNoiseUtil.ParameterRange.of(0.0f), parameterRange, 0.01f), BiomeKeys.PLAINS));
        Spline<NoisePoint> spline = VanillaTerrainParameters.createLandSpline(-0.15f, 0.0f, 0.0f, 0.1f, 0.0f, -0.03f, false, false);
        RegistryKey<Biome> registryKey = BiomeKeys.DESERT;
        for (Float float_ : spline.getLocations()) {
            parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(parameterRange, parameterRange, parameterRange, MultiNoiseUtil.ParameterRange.of(float_.floatValue()), MultiNoiseUtil.ParameterRange.of(0.0f), parameterRange, 0.0f), registryKey));
            registryKey = registryKey == BiomeKeys.DESERT ? BiomeKeys.BADLANDS : BiomeKeys.DESERT;
        }
        for (Float float_ : this.offsetSpline.getLocations()) {
            parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(parameterRange, parameterRange, MultiNoiseUtil.ParameterRange.of(float_.floatValue()), parameterRange, MultiNoiseUtil.ParameterRange.of(0.0f), parameterRange, 0.0f), BiomeKeys.SNOWY_TAIGA));
        }
    }

    @Debug
    public Spline<NoisePoint> getOffsetSpline() {
        return this.offsetSpline;
    }

    @Debug
    public Spline<NoisePoint> getFactorSpline() {
        return this.factorSpline;
    }

    public float getOffset(NoisePoint point) {
        return this.offsetSpline.apply(point) + 0.015f;
    }

    public float getFactor(NoisePoint point) {
        return this.factorSpline.apply(point);
    }

    public float getPeak(NoisePoint point) {
        return this.peakSpline.apply(point);
    }

    public NoisePoint createNoisePoint(float continentalnessNoise, float erosionNoise, float weirdnessNoise) {
        return new NoisePoint(continentalnessNoise, erosionNoise, VanillaTerrainParameters.getNormalizedWeirdness(weirdnessNoise), weirdnessNoise);
    }

    public static float getNormalizedWeirdness(float weirdness) {
        return -(Math.abs(Math.abs(weirdness) - 0.6666667f) - 0.33333334f) * 3.0f;
    }

    public static final class NoisePoint {
        final float continentalnessNoise;
        final float erosionNoise;
        final float normalizedWeirdness;
        final float weirdnessNoise;

        public NoisePoint(float continentalnessNoise, float erosionNoise, float normalizedWeirdness, float weirdnessNoise) {
            this.continentalnessNoise = continentalnessNoise;
            this.erosionNoise = erosionNoise;
            this.normalizedWeirdness = normalizedWeirdness;
            this.weirdnessNoise = weirdnessNoise;
        }

        public float getContinentalnessNoise() {
            return this.continentalnessNoise;
        }

        public float getErosionNoise() {
            return this.erosionNoise;
        }

        public float getNormalizedWeirdness() {
            return this.normalizedWeirdness;
        }

        public float getWeirdnessNoise() {
            return this.weirdnessNoise;
        }
    }
}

