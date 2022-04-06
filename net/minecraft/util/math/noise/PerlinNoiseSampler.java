/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math.noise;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.gen.noise.NoiseHelper;

public final class PerlinNoiseSampler {
    private static final float field_31701 = 1.0E-7f;
    private final byte[] permutations;
    public final double originX;
    public final double originY;
    public final double originZ;

    public PerlinNoiseSampler(AbstractRandom random) {
        int i;
        this.originX = random.nextDouble() * 256.0;
        this.originY = random.nextDouble() * 256.0;
        this.originZ = random.nextDouble() * 256.0;
        this.permutations = new byte[256];
        for (i = 0; i < 256; ++i) {
            this.permutations[i] = (byte)i;
        }
        for (i = 0; i < 256; ++i) {
            int j = random.nextInt(256 - i);
            byte b = this.permutations[i];
            this.permutations[i] = this.permutations[i + j];
            this.permutations[i + j] = b;
        }
    }

    public double sample(double x, double y, double z) {
        return this.sample(x, y, z, 0.0, 0.0);
    }

    @Deprecated
    public double sample(double x, double y, double z, double yScale, double yMax) {
        double n;
        double d = x + this.originX;
        double e = y + this.originY;
        double f = z + this.originZ;
        int i = MathHelper.floor(d);
        int j = MathHelper.floor(e);
        int k = MathHelper.floor(f);
        double g = d - (double)i;
        double h = e - (double)j;
        double l = f - (double)k;
        if (yScale != 0.0) {
            double m = yMax >= 0.0 && yMax < h ? yMax : h;
            n = (double)MathHelper.floor(m / yScale + (double)1.0E-7f) * yScale;
        } else {
            n = 0.0;
        }
        return this.sample(i, j, k, g, h - n, l, h);
    }

    public double sampleDerivative(double x, double y, double z, double[] ds) {
        double d = x + this.originX;
        double e = y + this.originY;
        double f = z + this.originZ;
        int i = MathHelper.floor(d);
        int j = MathHelper.floor(e);
        int k = MathHelper.floor(f);
        double g = d - (double)i;
        double h = e - (double)j;
        double l = f - (double)k;
        return this.sampleDerivative(i, j, k, g, h, l, ds);
    }

    private static double grad(int hash, double x, double y, double z) {
        return SimplexNoiseSampler.dot(SimplexNoiseSampler.GRADIENTS[hash & 0xF], x, y, z);
    }

    private int getGradient(int hash) {
        return this.permutations[hash & 0xFF] & 0xFF;
    }

    private double sample(int sectionX, int sectionY, int sectionZ, double localX, double localY, double localZ, double fadeLocalX) {
        int i = this.getGradient(sectionX);
        int j = this.getGradient(sectionX + 1);
        int k = this.getGradient(i + sectionY);
        int l = this.getGradient(i + sectionY + 1);
        int m = this.getGradient(j + sectionY);
        int n = this.getGradient(j + sectionY + 1);
        double d = PerlinNoiseSampler.grad(this.getGradient(k + sectionZ), localX, localY, localZ);
        double e = PerlinNoiseSampler.grad(this.getGradient(m + sectionZ), localX - 1.0, localY, localZ);
        double f = PerlinNoiseSampler.grad(this.getGradient(l + sectionZ), localX, localY - 1.0, localZ);
        double g = PerlinNoiseSampler.grad(this.getGradient(n + sectionZ), localX - 1.0, localY - 1.0, localZ);
        double h = PerlinNoiseSampler.grad(this.getGradient(k + sectionZ + 1), localX, localY, localZ - 1.0);
        double o = PerlinNoiseSampler.grad(this.getGradient(m + sectionZ + 1), localX - 1.0, localY, localZ - 1.0);
        double p = PerlinNoiseSampler.grad(this.getGradient(l + sectionZ + 1), localX, localY - 1.0, localZ - 1.0);
        double q = PerlinNoiseSampler.grad(this.getGradient(n + sectionZ + 1), localX - 1.0, localY - 1.0, localZ - 1.0);
        double r = MathHelper.perlinFade(localX);
        double s = MathHelper.perlinFade(fadeLocalX);
        double t = MathHelper.perlinFade(localZ);
        return MathHelper.lerp3(r, s, t, d, e, f, g, h, o, p, q);
    }

    private double sampleDerivative(int sectionX, int sectionY, int sectionZ, double localX, double localY, double localZ, double[] ds) {
        int i = this.getGradient(sectionX);
        int j = this.getGradient(sectionX + 1);
        int k = this.getGradient(i + sectionY);
        int l = this.getGradient(i + sectionY + 1);
        int m = this.getGradient(j + sectionY);
        int n = this.getGradient(j + sectionY + 1);
        int o = this.getGradient(k + sectionZ);
        int p = this.getGradient(m + sectionZ);
        int q = this.getGradient(l + sectionZ);
        int r = this.getGradient(n + sectionZ);
        int s = this.getGradient(k + sectionZ + 1);
        int t = this.getGradient(m + sectionZ + 1);
        int u = this.getGradient(l + sectionZ + 1);
        int v = this.getGradient(n + sectionZ + 1);
        int[] is = SimplexNoiseSampler.GRADIENTS[o & 0xF];
        int[] js = SimplexNoiseSampler.GRADIENTS[p & 0xF];
        int[] ks = SimplexNoiseSampler.GRADIENTS[q & 0xF];
        int[] ls = SimplexNoiseSampler.GRADIENTS[r & 0xF];
        int[] ms = SimplexNoiseSampler.GRADIENTS[s & 0xF];
        int[] ns = SimplexNoiseSampler.GRADIENTS[t & 0xF];
        int[] os = SimplexNoiseSampler.GRADIENTS[u & 0xF];
        int[] ps = SimplexNoiseSampler.GRADIENTS[v & 0xF];
        double d = SimplexNoiseSampler.dot(is, localX, localY, localZ);
        double e = SimplexNoiseSampler.dot(js, localX - 1.0, localY, localZ);
        double f = SimplexNoiseSampler.dot(ks, localX, localY - 1.0, localZ);
        double g = SimplexNoiseSampler.dot(ls, localX - 1.0, localY - 1.0, localZ);
        double h = SimplexNoiseSampler.dot(ms, localX, localY, localZ - 1.0);
        double w = SimplexNoiseSampler.dot(ns, localX - 1.0, localY, localZ - 1.0);
        double x = SimplexNoiseSampler.dot(os, localX, localY - 1.0, localZ - 1.0);
        double y = SimplexNoiseSampler.dot(ps, localX - 1.0, localY - 1.0, localZ - 1.0);
        double z = MathHelper.perlinFade(localX);
        double aa = MathHelper.perlinFade(localY);
        double ab = MathHelper.perlinFade(localZ);
        double ac = MathHelper.lerp3(z, aa, ab, is[0], js[0], ks[0], ls[0], ms[0], ns[0], os[0], ps[0]);
        double ad = MathHelper.lerp3(z, aa, ab, is[1], js[1], ks[1], ls[1], ms[1], ns[1], os[1], ps[1]);
        double ae = MathHelper.lerp3(z, aa, ab, is[2], js[2], ks[2], ls[2], ms[2], ns[2], os[2], ps[2]);
        double af = MathHelper.lerp2(aa, ab, e - d, g - f, w - h, y - x);
        double ag = MathHelper.lerp2(ab, z, f - d, x - h, g - e, y - w);
        double ah = MathHelper.lerp2(z, aa, h - d, w - e, x - f, y - g);
        double ai = MathHelper.perlinFadeDerivative(localX);
        double aj = MathHelper.perlinFadeDerivative(localY);
        double ak = MathHelper.perlinFadeDerivative(localZ);
        double al = ac + ai * af;
        double am = ad + aj * ag;
        double an = ae + ak * ah;
        ds[0] = ds[0] + al;
        ds[1] = ds[1] + am;
        ds[2] = ds[2] + an;
        return MathHelper.lerp3(z, aa, ab, d, e, f, g, h, w, x, y);
    }

    @VisibleForTesting
    public void addDebugInfo(StringBuilder info) {
        NoiseHelper.appendDebugInfo(info, this.originX, this.originY, this.originZ, this.permutations);
    }
}

