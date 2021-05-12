/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import java.util.Random;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.NoiseHelper;
import net.minecraft.world.gen.SimpleRandom;
import net.minecraft.world.gen.WorldGenRandom;

public class NoodleCavesGenerator {
    private static final int field_33654 = 30;
    private static final double field_33655 = 1.5;
    private static final double field_33656 = 2.6666666666666665;
    private static final double field_33657 = 2.6666666666666665;
    private final DoublePerlinNoiseSampler field_33658;
    private final DoublePerlinNoiseSampler field_33659;
    private final DoublePerlinNoiseSampler field_33660;
    private final DoublePerlinNoiseSampler field_33661;

    public NoodleCavesGenerator(long seed) {
        Random random = new Random(seed);
        this.field_33658 = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -8, 1.0);
        this.field_33659 = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -8, 1.0);
        this.field_33660 = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -7, 1.0);
        this.field_33661 = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -7, 1.0);
    }

    public void method_36471(double[] ds, int i, int j, int k, int l) {
        this.method_36472(ds, i, j, k, l, this.field_33658, 1.0);
    }

    public void method_36474(double[] ds, int i, int j, int k, int l) {
        this.method_36472(ds, i, j, k, l, this.field_33659, 1.0);
    }

    public void method_36475(double[] ds, int i, int j, int k, int l) {
        this.method_36473(ds, i, j, k, l, this.field_33660, 2.6666666666666665, 2.6666666666666665);
    }

    public void method_36476(double[] ds, int i, int j, int k, int l) {
        this.method_36473(ds, i, j, k, l, this.field_33661, 2.6666666666666665, 2.6666666666666665);
    }

    public void method_36472(double[] ds, int i, int j, int k, int l, DoublePerlinNoiseSampler doublePerlinNoiseSampler, double d) {
        this.method_36473(ds, i, j, k, l, doublePerlinNoiseSampler, d, d);
    }

    public void method_36473(double[] ds, int i, int j, int k, int l, DoublePerlinNoiseSampler doublePerlinNoiseSampler, double d, double e) {
        int m = 8;
        int n = 4;
        for (int o = 0; o < l; ++o) {
            int p = o + k;
            int q = i * 4;
            int r = p * 8;
            int s = j * 4;
            double f = r < 38 ? NoiseHelper.lerpFromProgress(doublePerlinNoiseSampler, (double)q * d, (double)r * e, (double)s * d, -1.0, 1.0) : 1.0;
            ds[o] = f;
        }
    }

    public double method_36470(double d, int i, int j, int k, double e, double f, double g, double h, int l) {
        if (j > 30 || j < l + 4) {
            return d;
        }
        if (d < 0.0) {
            return d;
        }
        if (e < 0.0) {
            return d;
        }
        double m = 0.05;
        double n = 0.1;
        double o = MathHelper.clampedLerpFromProgress(f, -1.0, 1.0, 0.05, 0.1);
        double p = Math.abs(1.5 * g) - o;
        double q = Math.abs(1.5 * h) - o;
        double r = Math.max(p, q);
        return Math.min(d, r);
    }
}

