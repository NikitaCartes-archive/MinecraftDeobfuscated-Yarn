/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math.noise;

import java.util.stream.IntStream;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.gen.WorldGenRandom;

public class InterpolatedNoiseSampler {
    private final OctavePerlinNoiseSampler lowerInterpolatedNoise;
    private final OctavePerlinNoiseSampler upperInterpolatedNoise;
    private final OctavePerlinNoiseSampler interpolationNoise;

    public InterpolatedNoiseSampler(OctavePerlinNoiseSampler lowerInterpolatedNoise, OctavePerlinNoiseSampler upperInterpolatedNoise, OctavePerlinNoiseSampler interpolationNoise) {
        this.lowerInterpolatedNoise = lowerInterpolatedNoise;
        this.upperInterpolatedNoise = upperInterpolatedNoise;
        this.interpolationNoise = interpolationNoise;
    }

    public InterpolatedNoiseSampler(WorldGenRandom random) {
        this(new OctavePerlinNoiseSampler(random, IntStream.rangeClosed(-15, 0)), new OctavePerlinNoiseSampler(random, IntStream.rangeClosed(-15, 0)), new OctavePerlinNoiseSampler(random, IntStream.rangeClosed(-7, 0)));
    }

    public double sample(int i, int j, int k, double horizontalScale, double verticalScale, double horizontalStretch, double verticalStretch) {
        double d = 0.0;
        double e = 0.0;
        double f = 0.0;
        boolean bl = true;
        double g = 1.0;
        for (int l = 0; l < 8; ++l) {
            PerlinNoiseSampler perlinNoiseSampler = this.interpolationNoise.getOctave(l);
            if (perlinNoiseSampler != null) {
                f += perlinNoiseSampler.sample(OctavePerlinNoiseSampler.maintainPrecision((double)i * horizontalStretch * g), OctavePerlinNoiseSampler.maintainPrecision((double)j * verticalStretch * g), OctavePerlinNoiseSampler.maintainPrecision((double)k * horizontalStretch * g), verticalStretch * g, (double)j * verticalStretch * g) / g;
            }
            g /= 2.0;
        }
        double h = (f / 10.0 + 1.0) / 2.0;
        boolean bl2 = h >= 1.0;
        boolean bl3 = h <= 0.0;
        g = 1.0;
        for (int m = 0; m < 16; ++m) {
            PerlinNoiseSampler perlinNoiseSampler2;
            double n = OctavePerlinNoiseSampler.maintainPrecision((double)i * horizontalScale * g);
            double o = OctavePerlinNoiseSampler.maintainPrecision((double)j * verticalScale * g);
            double p = OctavePerlinNoiseSampler.maintainPrecision((double)k * horizontalScale * g);
            double q = verticalScale * g;
            if (!bl2 && (perlinNoiseSampler2 = this.lowerInterpolatedNoise.getOctave(m)) != null) {
                d += perlinNoiseSampler2.sample(n, o, p, q, (double)j * q) / g;
            }
            if (!bl3 && (perlinNoiseSampler2 = this.upperInterpolatedNoise.getOctave(m)) != null) {
                e += perlinNoiseSampler2.sample(n, o, p, q, (double)j * q) / g;
            }
            g /= 2.0;
        }
        return MathHelper.clampedLerp(d / 512.0, e / 512.0, h);
    }
}

