/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.NoiseHelper;
import net.minecraft.world.gen.SimpleRandom;
import net.minecraft.world.gen.WorldGenRandom;
import net.minecraft.world.gen.chunk.WeightSampler;

public class NoiseCaveSampler
implements WeightSampler {
    private final int minY;
    private final DoublePerlinNoiseSampler terrainAdditionNoise;
    private final DoublePerlinNoiseSampler pillarNoise;
    private final DoublePerlinNoiseSampler pillarFalloffNoise;
    private final DoublePerlinNoiseSampler pillarScaleNoise;
    private final DoublePerlinNoiseSampler scaledCaveScaleNoise;
    private final DoublePerlinNoiseSampler horizontalCaveNoise;
    private final DoublePerlinNoiseSampler caveScaleNoise;
    private final DoublePerlinNoiseSampler caveFalloffNoise;
    private final DoublePerlinNoiseSampler tunnelNoise1;
    private final DoublePerlinNoiseSampler tunnelNoise2;
    private final DoublePerlinNoiseSampler tunnelScaleNoise;
    private final DoublePerlinNoiseSampler tunnelFalloffNoise;
    private final DoublePerlinNoiseSampler offsetNoise;
    private final DoublePerlinNoiseSampler offsetScaleNoise;
    private final DoublePerlinNoiseSampler field_28842;
    private final DoublePerlinNoiseSampler caveDensityNoise;
    private static final int field_31463 = 128;
    private static final int field_31464 = 170;

    public NoiseCaveSampler(WorldGenRandom random, int minY) {
        this.minY = minY;
        this.pillarNoise = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -7, 1.0, 1.0);
        this.pillarFalloffNoise = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -8, 1.0);
        this.pillarScaleNoise = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -8, 1.0);
        this.scaledCaveScaleNoise = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -7, 1.0);
        this.horizontalCaveNoise = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -8, 1.0);
        this.caveScaleNoise = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -11, 1.0);
        this.caveFalloffNoise = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -11, 1.0);
        this.tunnelNoise1 = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -7, 1.0);
        this.tunnelNoise2 = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -7, 1.0);
        this.tunnelScaleNoise = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -11, 1.0);
        this.tunnelFalloffNoise = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -8, 1.0);
        this.offsetNoise = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -5, 1.0);
        this.offsetScaleNoise = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -8, 1.0);
        this.field_28842 = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -8, 1.0, 1.0, 1.0);
        this.terrainAdditionNoise = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -8, 1.0);
        this.caveDensityNoise = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -8, 0.5, 1.0, 2.0, 1.0, 2.0, 1.0, 0.0, 2.0, 0.0);
    }

    @Override
    public double sample(double weight, int x, int y, int z) {
        boolean bl = weight < 170.0;
        double d = this.getTunnelOffsetNoise(z, x, y);
        double e = this.getTunnelNoise(z, x, y);
        if (bl) {
            return Math.min(weight, (e + d) * 128.0 * 5.0);
        }
        double f = this.caveDensityNoise.sample(z, (double)x / 1.5, y);
        double g = MathHelper.clamp(f + 0.25, -1.0, 1.0);
        double h = (float)(30 - x) / 8.0f;
        double i = g + MathHelper.clampedLerp(0.5, 0.0, h);
        double j = this.getTerrainAdditionNoise(z, x, y);
        double k = this.getCaveNoise(z, x, y);
        double l = i + j;
        double m = Math.min(l, Math.min(e, k) + d);
        double n = Math.max(m, this.getPillarNoise(z, x, y));
        return 128.0 * MathHelper.clamp(n, -1.0, 1.0);
    }

    private double method_35325(double d, int i, int j, int k) {
        double e = this.field_28842.sample(i * 2, j, k * 2);
        e = NoiseHelper.method_35479(e, 1.0);
        boolean l = false;
        double f = (double)(j - 0) / 40.0;
        e += MathHelper.clampedLerp(0.5, d, f);
        double g = 3.0;
        e = 4.0 * e + 3.0;
        return Math.min(d, e);
    }

    private double getPillarNoise(int x, int y, int z) {
        double d = 0.0;
        double e = 2.0;
        double f = NoiseHelper.lerpFromProgress(this.pillarFalloffNoise, x, y, z, 0.0, 2.0);
        double g = 0.0;
        double h = 1.1;
        double i = NoiseHelper.lerpFromProgress(this.pillarScaleNoise, x, y, z, 0.0, 1.1);
        i = Math.pow(i, 3.0);
        double j = 25.0;
        double k = 0.3;
        double l = this.pillarNoise.sample((double)x * 25.0, (double)y * 0.3, (double)z * 25.0);
        if ((l = i * (l * 2.0 - f)) > 0.03) {
            return l;
        }
        return Double.NEGATIVE_INFINITY;
    }

    private double getTerrainAdditionNoise(int x, int y, int z) {
        double d = this.terrainAdditionNoise.sample(x, y * 8, z);
        return MathHelper.square(d) * 4.0;
    }

    private double getTunnelNoise(int x, int y, int z) {
        double d = this.tunnelScaleNoise.sample(x * 2, y, z * 2);
        double e = CaveScaler.scaleTunnels(d);
        double f = 0.065;
        double g = 0.088;
        double h = NoiseHelper.lerpFromProgress(this.tunnelFalloffNoise, x, y, z, 0.065, 0.088);
        double i = NoiseCaveSampler.sample(this.tunnelNoise1, x, y, z, e);
        double j = Math.abs(e * i) - h;
        double k = NoiseCaveSampler.sample(this.tunnelNoise2, x, y, z, e);
        double l = Math.abs(e * k) - h;
        return NoiseCaveSampler.clamp(Math.max(j, l));
    }

    private double getCaveNoise(int x, int y, int z) {
        double d = this.caveScaleNoise.sample(x * 2, y, z * 2);
        double e = CaveScaler.scaleCaves(d);
        double f = 0.6;
        double g = 1.3;
        double h = NoiseHelper.lerpFromProgress(this.caveFalloffNoise, x * 2, y, z * 2, 0.6, 1.3);
        double i = NoiseCaveSampler.sample(this.scaledCaveScaleNoise, x, y, z, e);
        double j = 0.083;
        double k = Math.abs(e * i) - 0.083 * h;
        int l = this.minY;
        int m = 8;
        double n = NoiseHelper.lerpFromProgress(this.horizontalCaveNoise, x, 0.0, z, l, 8.0);
        double o = Math.abs(n - (double)y / 8.0) - 1.0 * h;
        o = o * o * o;
        return NoiseCaveSampler.clamp(Math.max(o, k));
    }

    private double getTunnelOffsetNoise(int x, int y, int z) {
        double d = NoiseHelper.lerpFromProgress(this.offsetScaleNoise, x, y, z, 0.0, 0.1);
        return (0.4 - Math.abs(this.offsetNoise.sample(x, y, z))) * d;
    }

    private static double clamp(double value) {
        return MathHelper.clamp(value, -1.0, 1.0);
    }

    private static double sample(DoublePerlinNoiseSampler sampler, double x, double y, double z, double scale) {
        return sampler.sample(x / scale, y / scale, z / scale);
    }

    static final class CaveScaler {
        private CaveScaler() {
        }

        static double scaleCaves(double value) {
            if (value < -0.75) {
                return 0.5;
            }
            if (value < -0.5) {
                return 0.75;
            }
            if (value < 0.5) {
                return 1.0;
            }
            if (value < 0.75) {
                return 2.0;
            }
            return 3.0;
        }

        static double scaleTunnels(double value) {
            if (value < -0.5) {
                return 0.75;
            }
            if (value < 0.0) {
                return 1.0;
            }
            if (value < 0.5) {
                return 1.5;
            }
            return 2.0;
        }
    }
}

