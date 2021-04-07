/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.gen.NoiseCaveSampler;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import org.jetbrains.annotations.Nullable;

/**
 * Samples noise values for use in chunk generation.
 */
public class NoiseColumnSampler {
    private static final int field_31470 = 32;
    /**
     * Table of weights used to weight faraway biomes less than nearby biomes.
     */
    private static final float[] BIOME_WEIGHT_TABLE = Util.make(new float[25], array -> {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                float f;
                array[i + 2 + (j + 2) * 5] = f = 10.0f / MathHelper.sqrt((float)(i * i + j * j) + 0.2f);
            }
        }
    });
    private final BiomeSource biomeSource;
    private final int horizontalNoiseResolution;
    private final int verticalNoiseResolution;
    private final int noiseSizeY;
    private final GenerationShapeConfig config;
    private final InterpolatedNoiseSampler noise;
    @Nullable
    private final SimplexNoiseSampler islandNoise;
    private final OctavePerlinNoiseSampler densityNoise;
    private final double topSlideTarget;
    private final double topSlideSize;
    private final double topSlideOffset;
    private final double bottomSlideTarget;
    private final double bottomSlideSize;
    private final double bottomSlideOffset;
    private final double densityFactor;
    private final double densityOffset;
    @Nullable
    private final NoiseCaveSampler noiseCaveSampler;

    public NoiseColumnSampler(BiomeSource biomeSource, int horizontalNoiseResolution, int verticalNoiseResolution, int noiseSizeY, GenerationShapeConfig config, InterpolatedNoiseSampler noise, @Nullable SimplexNoiseSampler islandNoise, OctavePerlinNoiseSampler densityNoise, @Nullable NoiseCaveSampler noiseCaveSampler) {
        this.horizontalNoiseResolution = horizontalNoiseResolution;
        this.verticalNoiseResolution = verticalNoiseResolution;
        this.biomeSource = biomeSource;
        this.noiseSizeY = noiseSizeY;
        this.config = config;
        this.noise = noise;
        this.islandNoise = islandNoise;
        this.densityNoise = densityNoise;
        this.topSlideTarget = config.getTopSlide().getTarget();
        this.topSlideSize = config.getTopSlide().getSize();
        this.topSlideOffset = config.getTopSlide().getOffset();
        this.bottomSlideTarget = config.getBottomSlide().getTarget();
        this.bottomSlideSize = config.getBottomSlide().getSize();
        this.bottomSlideOffset = config.getBottomSlide().getOffset();
        this.densityFactor = config.getDensityFactor();
        this.densityOffset = config.getDensityOffset();
        this.noiseCaveSampler = noiseCaveSampler;
    }

    /**
     * Samples the noise for the given column and stores it in the buffer parameter.
     */
    public void sampleNoiseColumn(double[] buffer, int x, int z, GenerationShapeConfig config, int seaLevel, int minY, int noiseSizeY) {
        double v;
        double e;
        double d;
        if (this.islandNoise != null) {
            d = TheEndBiomeSource.getNoiseAt(this.islandNoise, x, z) - 8.0f;
            e = d > 0.0 ? 0.25 : 1.0;
        } else {
            float f = 0.0f;
            float g = 0.0f;
            float h = 0.0f;
            int i = 2;
            int j = seaLevel;
            float k = this.biomeSource.getBiomeForNoiseGen(x, j, z).getDepth();
            for (int l = -2; l <= 2; ++l) {
                for (int m = -2; m <= 2; ++m) {
                    float q;
                    float p;
                    Biome biome = this.biomeSource.getBiomeForNoiseGen(x + l, j, z + m);
                    float n = biome.getDepth();
                    float o = biome.getScale();
                    if (config.isAmplified() && n > 0.0f) {
                        p = 1.0f + n * 2.0f;
                        q = 1.0f + o * 4.0f;
                    } else {
                        p = n;
                        q = o;
                    }
                    float r = n > k ? 0.5f : 1.0f;
                    float s = r * BIOME_WEIGHT_TABLE[l + 2 + (m + 2) * 5] / (p + 2.0f);
                    f += q * s;
                    g += p * s;
                    h += s;
                }
            }
            float t = g / h;
            float u = f / h;
            v = t * 0.5f - 0.125f;
            double w = u * 0.9f + 0.1f;
            d = v * 0.265625;
            e = 96.0 / w;
        }
        double y = 684.412 * config.getSampling().getXZScale();
        double aa = 684.412 * config.getSampling().getYScale();
        double ab = y / config.getSampling().getXZFactor();
        double ac = aa / config.getSampling().getYFactor();
        v = config.hasRandomDensityOffset() ? this.getDensityNoise(x, z) : 0.0;
        for (int ad = 0; ad <= noiseSizeY; ++ad) {
            int ae = ad + minY;
            double af = this.noise.sample(x, ae, z, y, aa, ab, ac);
            double ag = this.getOffset(ae, d, e, v) + af;
            ag = this.sampleNoiseCaves(x * this.horizontalNoiseResolution, ae * this.verticalNoiseResolution, z * this.horizontalNoiseResolution, ag);
            buffer[ad] = ag = this.applySlides(ag, ae);
        }
    }

    private double sampleNoiseCaves(int x, int y, int z, double noise) {
        if (this.noiseCaveSampler == null) {
            return noise;
        }
        return this.noiseCaveSampler.sample(x, y, z, noise);
    }

    /**
     * Calculates an offset for the noise.
     * <p>For example in the overworld, this makes lower y values solid while making higher y values air.
     */
    private double getOffset(int y, double depth, double scale, double randomDensityOffset) {
        double f;
        double d = 1.0 - (double)y * 2.0 / 32.0 + randomDensityOffset;
        double e = d * this.densityFactor + this.densityOffset;
        return f * (double)((f = (e + depth) * scale) > 0.0 ? 4 : 1);
    }

    /**
     * Interpolates the noise at the top and bottom of the world.
     */
    private double applySlides(double noise, int y) {
        double d;
        int i = MathHelper.floorDiv(this.config.getMinimumY(), this.verticalNoiseResolution);
        int j = y - i;
        if (this.topSlideSize > 0.0) {
            d = ((double)(this.noiseSizeY - j) - this.topSlideOffset) / this.topSlideSize;
            noise = MathHelper.clampedLerp(this.topSlideTarget, noise, d);
        }
        if (this.bottomSlideSize > 0.0) {
            d = ((double)j - this.bottomSlideOffset) / this.bottomSlideSize;
            noise = MathHelper.clampedLerp(this.bottomSlideTarget, noise, d);
        }
        return noise;
    }

    /**
     * Applies a random change to the density to subtly vary the height of the terrain.
     */
    private double getDensityNoise(int x, int z) {
        double d = this.densityNoise.sample(x * 200, 10.0, z * 200, 1.0, 0.0, true);
        double e = d < 0.0 ? -d * 0.3 : d;
        double f = e * 24.575625 - 2.0;
        if (f < 0.0) {
            return f * 0.009486607142857142;
        }
        return Math.min(f, 1.0) * 0.006640625;
    }
}

