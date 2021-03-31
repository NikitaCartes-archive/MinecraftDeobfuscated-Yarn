package net.minecraft.util.math.noise;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.WorldGenRandom;

public final class PerlinNoiseSampler {
	private static final float field_31701 = 1.0E-7F;
	private final byte[] permutations;
	public final double originX;
	public final double originY;
	public final double originZ;

	public PerlinNoiseSampler(WorldGenRandom random) {
		this.originX = random.nextDouble() * 256.0;
		this.originY = random.nextDouble() * 256.0;
		this.originZ = random.nextDouble() * 256.0;
		this.permutations = new byte[256];

		for (int i = 0; i < 256; i++) {
			this.permutations[i] = (byte)i;
		}

		for (int i = 0; i < 256; i++) {
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
		double d = x + this.originX;
		double e = y + this.originY;
		double f = z + this.originZ;
		int i = MathHelper.floor(d);
		int j = MathHelper.floor(e);
		int k = MathHelper.floor(f);
		double g = d - (double)i;
		double h = e - (double)j;
		double l = f - (double)k;
		double n;
		if (yScale != 0.0) {
			double m;
			if (yMax >= 0.0 && yMax < h) {
				m = yMax;
			} else {
				m = h;
			}

			n = (double)MathHelper.floor(m / yScale + 1.0E-7F) * yScale;
		} else {
			n = 0.0;
		}

		return this.sample(i, j, k, g, h - n, l, h);
	}

	public double method_35477(double d, double e, double f, double[] ds) {
		double g = d + this.originX;
		double h = e + this.originY;
		double i = f + this.originZ;
		int j = MathHelper.floor(g);
		int k = MathHelper.floor(h);
		int l = MathHelper.floor(i);
		double m = g - (double)j;
		double n = h - (double)k;
		double o = i - (double)l;
		return this.method_35478(j, k, l, m, n, o, ds);
	}

	private static double grad(int hash, double x, double y, double z) {
		return SimplexNoiseSampler.dot(SimplexNoiseSampler.GRADIENTS[hash & 15], x, y, z);
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
		double d = grad(this.getGradient(k + sectionZ), localX, localY, localZ);
		double e = grad(this.getGradient(m + sectionZ), localX - 1.0, localY, localZ);
		double f = grad(this.getGradient(l + sectionZ), localX, localY - 1.0, localZ);
		double g = grad(this.getGradient(n + sectionZ), localX - 1.0, localY - 1.0, localZ);
		double h = grad(this.getGradient(k + sectionZ + 1), localX, localY, localZ - 1.0);
		double o = grad(this.getGradient(m + sectionZ + 1), localX - 1.0, localY, localZ - 1.0);
		double p = grad(this.getGradient(l + sectionZ + 1), localX, localY - 1.0, localZ - 1.0);
		double q = grad(this.getGradient(n + sectionZ + 1), localX - 1.0, localY - 1.0, localZ - 1.0);
		double r = MathHelper.perlinFade(localX);
		double s = MathHelper.perlinFade(fadeLocalX);
		double t = MathHelper.perlinFade(localZ);
		return MathHelper.lerp3(r, s, t, d, e, f, g, h, o, p, q);
	}

	private double method_35478(int i, int j, int k, double d, double e, double f, double[] ds) {
		int l = this.getGradient(i);
		int m = this.getGradient(i + 1);
		int n = this.getGradient(l + j);
		int o = this.getGradient(l + j + 1);
		int p = this.getGradient(m + j);
		int q = this.getGradient(m + j + 1);
		int r = this.getGradient(n + k);
		int s = this.getGradient(p + k);
		int t = this.getGradient(o + k);
		int u = this.getGradient(q + k);
		int v = this.getGradient(n + k + 1);
		int w = this.getGradient(p + k + 1);
		int x = this.getGradient(o + k + 1);
		int y = this.getGradient(q + k + 1);
		int[] is = SimplexNoiseSampler.GRADIENTS[r & 15];
		int[] js = SimplexNoiseSampler.GRADIENTS[s & 15];
		int[] ks = SimplexNoiseSampler.GRADIENTS[t & 15];
		int[] ls = SimplexNoiseSampler.GRADIENTS[u & 15];
		int[] ms = SimplexNoiseSampler.GRADIENTS[v & 15];
		int[] ns = SimplexNoiseSampler.GRADIENTS[w & 15];
		int[] os = SimplexNoiseSampler.GRADIENTS[x & 15];
		int[] ps = SimplexNoiseSampler.GRADIENTS[y & 15];
		double g = SimplexNoiseSampler.dot(is, d, e, f);
		double h = SimplexNoiseSampler.dot(js, d - 1.0, e, f);
		double z = SimplexNoiseSampler.dot(ks, d, e - 1.0, f);
		double aa = SimplexNoiseSampler.dot(ls, d - 1.0, e - 1.0, f);
		double ab = SimplexNoiseSampler.dot(ms, d, e, f - 1.0);
		double ac = SimplexNoiseSampler.dot(ns, d - 1.0, e, f - 1.0);
		double ad = SimplexNoiseSampler.dot(os, d, e - 1.0, f - 1.0);
		double ae = SimplexNoiseSampler.dot(ps, d - 1.0, e - 1.0, f - 1.0);
		double af = MathHelper.perlinFade(d);
		double ag = MathHelper.perlinFade(e);
		double ah = MathHelper.perlinFade(f);
		double ai = MathHelper.lerp3(
			af, ag, ah, (double)is[0], (double)js[0], (double)ks[0], (double)ls[0], (double)ms[0], (double)ns[0], (double)os[0], (double)ps[0]
		);
		double aj = MathHelper.lerp3(
			af, ag, ah, (double)is[1], (double)js[1], (double)ks[1], (double)ls[1], (double)ms[1], (double)ns[1], (double)os[1], (double)ps[1]
		);
		double ak = MathHelper.lerp3(
			af, ag, ah, (double)is[2], (double)js[2], (double)ks[2], (double)ls[2], (double)ms[2], (double)ns[2], (double)os[2], (double)ps[2]
		);
		double al = MathHelper.lerp2(ag, ah, h - g, aa - z, ac - ab, ae - ad);
		double am = MathHelper.lerp2(ah, af, z - g, ad - ab, aa - h, ae - ac);
		double an = MathHelper.lerp2(af, ag, ab - g, ac - h, ad - z, ae - aa);
		double ao = MathHelper.method_34956(d);
		double ap = MathHelper.method_34956(e);
		double aq = MathHelper.method_34956(f);
		double ar = ai + ao * al;
		double as = aj + ap * am;
		double at = ak + aq * an;
		ds[0] += ar;
		ds[1] += as;
		ds[2] += at;
		return MathHelper.lerp3(af, ag, ah, g, h, z, aa, ab, ac, ad, ae);
	}
}
