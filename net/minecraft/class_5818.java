/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.minecraft.class_5822;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import org.jetbrains.annotations.Nullable;

public class class_5818 {
    private static final float[] field_28749 = Util.make(new float[25], fs -> {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                float f;
                fs[i + 2 + (j + 2) * 5] = f = 10.0f / MathHelper.sqrt((float)(i * i + j * j) + 0.2f);
            }
        }
    });
    private final BiomeSource field_28750;
    private final int field_28751;
    private final int field_28752;
    private final int field_28753;
    private final GenerationShapeConfig field_28754;
    private final class_5822 field_28755;
    @Nullable
    private final SimplexNoiseSampler field_28756;
    private final OctavePerlinNoiseSampler field_28757;
    private final double field_28758;
    private final double field_28759;
    private final double field_28760;
    private final double field_28761;
    private final double field_28762;
    private final double field_28763;
    private final double field_28764;
    private final double field_28765;

    public class_5818(BiomeSource biomeSource, int i, int j, int k, GenerationShapeConfig generationShapeConfig, class_5822 arg, @Nullable SimplexNoiseSampler simplexNoiseSampler, OctavePerlinNoiseSampler octavePerlinNoiseSampler) {
        this.field_28751 = i;
        this.field_28752 = j;
        this.field_28750 = biomeSource;
        this.field_28753 = k;
        this.field_28754 = generationShapeConfig;
        this.field_28755 = arg;
        this.field_28756 = simplexNoiseSampler;
        this.field_28757 = octavePerlinNoiseSampler;
        this.field_28758 = generationShapeConfig.getTopSlide().getTarget();
        this.field_28759 = generationShapeConfig.getTopSlide().getSize();
        this.field_28760 = generationShapeConfig.getTopSlide().getOffset();
        this.field_28761 = generationShapeConfig.getBottomSlide().getTarget();
        this.field_28762 = generationShapeConfig.getBottomSlide().getSize();
        this.field_28763 = generationShapeConfig.getBottomSlide().getOffset();
        this.field_28764 = generationShapeConfig.getDensityFactor();
        this.field_28765 = generationShapeConfig.getDensityOffset();
    }

    public void method_33648(double[] ds, int i, int j, GenerationShapeConfig generationShapeConfig, int k, int l, int m) {
        double aa;
        double e;
        double d;
        if (this.field_28756 != null) {
            d = TheEndBiomeSource.getNoiseAt(this.field_28756, i, j) - 8.0f;
            e = d > 0.0 ? 0.25 : 1.0;
        } else {
            float f = 0.0f;
            float g = 0.0f;
            float h = 0.0f;
            int n = 2;
            int o = k;
            float p = this.field_28750.getBiomeForNoiseGen(i, o, j).getDepth();
            for (int q = -2; q <= 2; ++q) {
                for (int r = -2; r <= 2; ++r) {
                    float v;
                    float u;
                    Biome biome = this.field_28750.getBiomeForNoiseGen(i + q, o, j + r);
                    float s = biome.getDepth();
                    float t = biome.getScale();
                    if (generationShapeConfig.isAmplified() && s > 0.0f) {
                        u = 1.0f + s * 2.0f;
                        v = 1.0f + t * 4.0f;
                    } else {
                        u = s;
                        v = t;
                    }
                    float w = s > p ? 0.5f : 1.0f;
                    float x = w * field_28749[q + 2 + (r + 2) * 5] / (u + 2.0f);
                    f += v * x;
                    g += u * x;
                    h += x;
                }
            }
            float y = g / h;
            float z = f / h;
            aa = y * 0.5f - 0.125f;
            double ab = z * 0.9f + 0.1f;
            d = aa * 0.265625;
            e = 96.0 / ab;
        }
        double ac = 684.412 * generationShapeConfig.getSampling().getXZScale();
        double ad = 684.412 * generationShapeConfig.getSampling().getYScale();
        double ae = ac / generationShapeConfig.getSampling().getXZFactor();
        double af = ad / generationShapeConfig.getSampling().getYFactor();
        aa = generationShapeConfig.hasRandomDensityOffset() ? this.method_33647(i, j) : 0.0;
        for (int ag = 0; ag <= m; ++ag) {
            int ah = ag + l;
            double ai = this.field_28755.method_33657(i, ah, j, ac, ad, ae, af);
            double aj = this.method_33646(ah, d, e, aa) + ai;
            ds[ag] = aj = this.method_33645(aj, ah);
        }
    }

    private double method_33646(int i, double d, double e, double f) {
        double j;
        double g = 1.0 - (double)i * 2.0 / 32.0 + f;
        double h = g * this.field_28764 + this.field_28765;
        return j * (double)((j = (h + d) * e) > 0.0 ? 4 : 1);
    }

    private double method_33645(double d, int i) {
        double e;
        int j = MathHelper.floorDiv(this.field_28754.getMinimumY(), this.field_28752);
        int k = i - j;
        if (this.field_28759 > 0.0) {
            e = ((double)(this.field_28753 - k) - this.field_28760) / this.field_28759;
            d = MathHelper.clampedLerp(this.field_28758, d, e);
        }
        if (this.field_28762 > 0.0) {
            e = ((double)k - this.field_28763) / this.field_28762;
            d = MathHelper.clampedLerp(this.field_28761, d, e);
        }
        return d;
    }

    private double method_33647(int i, int j) {
        double d = this.field_28757.sample(i * 200, 10.0, j * 200, 1.0, 0.0, true);
        double e = d < 0.0 ? -d * 0.3 : d;
        double f = e * 24.575625 - 2.0;
        if (f < 0.0) {
            return f * 0.009486607142857142;
        }
        return Math.min(f, 1.0) * 0.006640625;
    }
}

