/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source.util;

import net.minecraft.util.function.ToFloatFunction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Spline;
import net.minecraft.world.gen.densityfunction.DensityFunctions;

public class VanillaTerrainParametersCreator {
    private static final float field_38024 = -0.51f;
    private static final float field_38025 = -0.4f;
    private static final float field_38026 = 0.1f;
    private static final float field_38027 = -0.15f;
    private static final ToFloatFunction<Float> IDENTITY = ToFloatFunction.IDENTITY;
    private static final ToFloatFunction<Float> OFFSET_AMPLIFIER = ToFloatFunction.fromFloat(value -> value < 0.0f ? value : value * 2.0f);
    private static final ToFloatFunction<Float> FACTOR_AMPLIFIER = ToFloatFunction.fromFloat(value -> 1.25f - 6.25f / (value + 5.0f));
    private static final ToFloatFunction<Float> JAGGEDNESS_AMPLIFIER = ToFloatFunction.fromFloat(value -> value * 2.0f);

    /**
     * Creates the spline for terrain offset.
     * 
     * Offset roughly correlates to surface height.
     */
    public static <C, I extends ToFloatFunction<C>> Spline<C, I> createOffsetSpline(I continents, I erosion, I ridgesFolded, boolean amplified) {
        ToFloatFunction<Float> toFloatFunction = amplified ? OFFSET_AMPLIFIER : IDENTITY;
        Spline<C, I> spline = VanillaTerrainParametersCreator.createContinentalOffsetSpline(erosion, ridgesFolded, -0.15f, 0.0f, 0.0f, 0.1f, 0.0f, -0.03f, false, false, toFloatFunction);
        Spline<C, I> spline2 = VanillaTerrainParametersCreator.createContinentalOffsetSpline(erosion, ridgesFolded, -0.1f, 0.03f, 0.1f, 0.1f, 0.01f, -0.03f, false, false, toFloatFunction);
        Spline<C, I> spline3 = VanillaTerrainParametersCreator.createContinentalOffsetSpline(erosion, ridgesFolded, -0.1f, 0.03f, 0.1f, 0.7f, 0.01f, -0.03f, true, true, toFloatFunction);
        Spline<C, I> spline4 = VanillaTerrainParametersCreator.createContinentalOffsetSpline(erosion, ridgesFolded, -0.05f, 0.03f, 0.1f, 1.0f, 0.01f, 0.01f, true, true, toFloatFunction);
        return Spline.builder(continents, toFloatFunction).add(-1.1f, 0.044f).add(-1.02f, -0.2222f).add(-0.51f, -0.2222f).add(-0.44f, -0.12f).add(-0.18f, -0.12f).add(-0.16f, spline).add(-0.15f, spline).add(-0.1f, spline2).add(0.25f, spline3).add(1.0f, spline4).build();
    }

    /**
     * Creates the spline for terrain factor.
     * 
     * Higher factor values generally result in flatter terrain,
     * while lower values generally result in more shattered terrain.
     */
    public static <C, I extends ToFloatFunction<C>> Spline<C, I> createFactorSpline(I continents, I erosion, I ridges, I ridgesFolded, boolean amplified) {
        ToFloatFunction<Float> toFloatFunction = amplified ? FACTOR_AMPLIFIER : IDENTITY;
        return Spline.builder(continents, IDENTITY).add(-0.19f, 3.95f).add(-0.15f, VanillaTerrainParametersCreator.method_42054(erosion, ridges, ridgesFolded, 6.25f, true, IDENTITY)).add(-0.1f, VanillaTerrainParametersCreator.method_42054(erosion, ridges, ridgesFolded, 5.47f, true, toFloatFunction)).add(0.03f, VanillaTerrainParametersCreator.method_42054(erosion, ridges, ridgesFolded, 5.08f, true, toFloatFunction)).add(0.06f, VanillaTerrainParametersCreator.method_42054(erosion, ridges, ridgesFolded, 4.69f, false, toFloatFunction)).build();
    }

    /**
     * Creates the spline for terrain jaggedness.
     * 
     * This is used for the peaks in the jagged peaks biome, for example.
     */
    public static <C, I extends ToFloatFunction<C>> Spline<C, I> createJaggednessSpline(I continents, I erosion, I ridges, I ridgesFolded, boolean amplified) {
        ToFloatFunction<Float> toFloatFunction = amplified ? JAGGEDNESS_AMPLIFIER : IDENTITY;
        float f = 0.65f;
        return Spline.builder(continents, toFloatFunction).add(-0.11f, 0.0f).add(0.03f, VanillaTerrainParametersCreator.method_42053(erosion, ridges, ridgesFolded, 1.0f, 0.5f, 0.0f, 0.0f, toFloatFunction)).add(0.65f, VanillaTerrainParametersCreator.method_42053(erosion, ridges, ridgesFolded, 1.0f, 1.0f, 1.0f, 0.0f, toFloatFunction)).build();
    }

    private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42053(I erosion, I ridges, I ridgesFolded, float f, float g, float h, float i, ToFloatFunction<Float> amplifier) {
        float j = -0.5775f;
        Spline<C, I> spline = VanillaTerrainParametersCreator.method_42052(ridges, ridgesFolded, f, h, amplifier);
        Spline<C, I> spline2 = VanillaTerrainParametersCreator.method_42052(ridges, ridgesFolded, g, i, amplifier);
        return Spline.builder(erosion, amplifier).add(-1.0f, spline).add(-0.78f, spline2).add(-0.5775f, spline2).add(-0.375f, 0.0f).build();
    }

    private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42052(I ridges, I ridgesFolded, float f, float g, ToFloatFunction<Float> amplifier) {
        float h = DensityFunctions.getPeaksValleysNoise(0.4f);
        float i = DensityFunctions.getPeaksValleysNoise(0.56666666f);
        float j = (h + i) / 2.0f;
        Spline.Builder<C, I> builder = Spline.builder(ridgesFolded, amplifier);
        builder.add(h, 0.0f);
        if (g > 0.0f) {
            builder.add(j, VanillaTerrainParametersCreator.method_42049(ridges, g, amplifier));
        } else {
            builder.add(j, 0.0f);
        }
        if (f > 0.0f) {
            builder.add(1.0f, VanillaTerrainParametersCreator.method_42049(ridges, f, amplifier));
        } else {
            builder.add(1.0f, 0.0f);
        }
        return builder.build();
    }

    private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42049(I ridges, float f, ToFloatFunction<Float> amplifier) {
        float g = 0.63f * f;
        float h = 0.3f * f;
        return Spline.builder(ridges, amplifier).add(-0.01f, g).add(0.01f, h).build();
    }

    private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42054(I erosion, I ridges, I ridgesFolded, float f, boolean bl, ToFloatFunction<Float> amplifier) {
        Spline spline = Spline.builder(ridges, amplifier).add(-0.2f, 6.3f).add(0.2f, f).build();
        Spline.Builder builder = Spline.builder(erosion, amplifier).add(-0.6f, spline).add(-0.5f, Spline.builder(ridges, amplifier).add(-0.05f, 6.3f).add(0.05f, 2.67f).build()).add(-0.35f, spline).add(-0.25f, spline).add(-0.1f, Spline.builder(ridges, amplifier).add(-0.05f, 2.67f).add(0.05f, 6.3f).build()).add(0.03f, spline);
        if (bl) {
            Spline spline2 = Spline.builder(ridges, amplifier).add(0.0f, f).add(0.1f, 0.625f).build();
            Spline spline3 = Spline.builder(ridgesFolded, amplifier).add(-0.9f, f).add(-0.69f, spline2).build();
            builder.add(0.35f, f).add(0.45f, spline3).add(0.55f, spline3).add(0.62f, f);
        } else {
            Spline spline2 = Spline.builder(ridgesFolded, amplifier).add(-0.7f, spline).add(-0.15f, 1.37f).build();
            Spline spline3 = Spline.builder(ridgesFolded, amplifier).add(0.45f, spline).add(0.7f, 1.56f).build();
            builder.add(0.05f, spline3).add(0.4f, spline3).add(0.45f, spline2).add(0.55f, spline2).add(0.58f, f);
        }
        return builder.build();
    }

    private static float method_42047(float f, float g, float h, float i) {
        return (g - f) / (i - h);
    }

    private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42050(I ridgesFolded, float f, boolean bl, ToFloatFunction<Float> amplifier) {
        Spline.Builder builder = Spline.builder(ridgesFolded, amplifier);
        float g = -0.7f;
        float h = -1.0f;
        float i = VanillaTerrainParametersCreator.getOffsetValue(-1.0f, f, -0.7f);
        float j = 1.0f;
        float k = VanillaTerrainParametersCreator.getOffsetValue(1.0f, f, -0.7f);
        float l = VanillaTerrainParametersCreator.method_42045(f);
        float m = -0.65f;
        if (-0.65f < l && l < 1.0f) {
            float n = VanillaTerrainParametersCreator.getOffsetValue(-0.65f, f, -0.7f);
            float o = -0.75f;
            float p = VanillaTerrainParametersCreator.getOffsetValue(-0.75f, f, -0.7f);
            float q = VanillaTerrainParametersCreator.method_42047(i, p, -1.0f, -0.75f);
            builder.add(-1.0f, i, q);
            builder.add(-0.75f, p);
            builder.add(-0.65f, n);
            float r = VanillaTerrainParametersCreator.getOffsetValue(l, f, -0.7f);
            float s = VanillaTerrainParametersCreator.method_42047(r, k, l, 1.0f);
            float t = 0.01f;
            builder.add(l - 0.01f, r);
            builder.add(l, r, s);
            builder.add(1.0f, k, s);
        } else {
            float n = VanillaTerrainParametersCreator.method_42047(i, k, -1.0f, 1.0f);
            if (bl) {
                builder.add(-1.0f, Math.max(0.2f, i));
                builder.add(0.0f, MathHelper.lerp(0.5f, i, k), n);
            } else {
                builder.add(-1.0f, i, n);
            }
            builder.add(1.0f, k, n);
        }
        return builder.build();
    }

    private static float getOffsetValue(float f, float g, float h) {
        float i = 1.17f;
        float j = 0.46082947f;
        float k = 1.0f - (1.0f - g) * 0.5f;
        float l = 0.5f * (1.0f - g);
        float m = (f + 1.17f) * 0.46082947f;
        float n = m * k - l;
        if (f < h) {
            return Math.max(n, -0.2222f);
        }
        return Math.max(n, 0.0f);
    }

    private static float method_42045(float f) {
        float g = 1.17f;
        float h = 0.46082947f;
        float i = 1.0f - (1.0f - f) * 0.5f;
        float j = 0.5f * (1.0f - f);
        return j / (0.46082947f * i) - 1.17f;
    }

    public static <C, I extends ToFloatFunction<C>> Spline<C, I> createContinentalOffsetSpline(I erosion, I ridgesFolded, float continentalness, float f, float g, float h, float i, float j, boolean bl, boolean bl2, ToFloatFunction<Float> amplifier) {
        float k = 0.6f;
        float l = 0.5f;
        float m = 0.5f;
        Spline<C, I> spline = VanillaTerrainParametersCreator.method_42050(ridgesFolded, MathHelper.lerp(h, 0.6f, 1.5f), bl2, amplifier);
        Spline<C, I> spline2 = VanillaTerrainParametersCreator.method_42050(ridgesFolded, MathHelper.lerp(h, 0.6f, 1.0f), bl2, amplifier);
        Spline<C, I> spline3 = VanillaTerrainParametersCreator.method_42050(ridgesFolded, h, bl2, amplifier);
        Spline<C, I> spline4 = VanillaTerrainParametersCreator.method_42048(ridgesFolded, continentalness - 0.15f, 0.5f * h, MathHelper.lerp(0.5f, 0.5f, 0.5f) * h, 0.5f * h, 0.6f * h, 0.5f, amplifier);
        Spline<C, I> spline5 = VanillaTerrainParametersCreator.method_42048(ridgesFolded, continentalness, i * h, f * h, 0.5f * h, 0.6f * h, 0.5f, amplifier);
        Spline<C, I> spline6 = VanillaTerrainParametersCreator.method_42048(ridgesFolded, continentalness, i, i, f, g, 0.5f, amplifier);
        Spline<C, I> spline7 = VanillaTerrainParametersCreator.method_42048(ridgesFolded, continentalness, i, i, f, g, 0.5f, amplifier);
        Spline spline8 = Spline.builder(ridgesFolded, amplifier).add(-1.0f, continentalness).add(-0.4f, spline6).add(0.0f, g + 0.07f).build();
        Spline<C, I> spline9 = VanillaTerrainParametersCreator.method_42048(ridgesFolded, -0.02f, j, j, f, g, 0.0f, amplifier);
        Spline.Builder<C, I> builder = Spline.builder(erosion, amplifier).add(-0.85f, spline).add(-0.7f, spline2).add(-0.4f, spline3).add(-0.35f, spline4).add(-0.1f, spline5).add(0.2f, spline6);
        if (bl) {
            builder.add(0.4f, spline7).add(0.45f, spline8).add(0.55f, spline8).add(0.58f, spline7);
        }
        builder.add(0.7f, spline9);
        return builder.build();
    }

    private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42048(I ridgesFolded, float continentalness, float f, float g, float h, float i, float j, ToFloatFunction<Float> amplifier) {
        float k = Math.max(0.5f * (f - continentalness), j);
        float l = 5.0f * (g - f);
        return Spline.builder(ridgesFolded, amplifier).add(-1.0f, continentalness, k).add(-0.4f, f, Math.min(k, l)).add(0.0f, g, l).add(0.4f, h, 2.0f * (h - g)).add(1.0f, i, 0.7f * (i - h)).build();
    }
}

