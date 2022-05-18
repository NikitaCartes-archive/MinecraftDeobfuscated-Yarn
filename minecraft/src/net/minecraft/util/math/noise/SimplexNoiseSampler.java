package net.minecraft.util.math.noise;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class SimplexNoiseSampler {
	protected static final int[][] GRADIENTS = new int[][]{
		{1, 1, 0},
		{-1, 1, 0},
		{1, -1, 0},
		{-1, -1, 0},
		{1, 0, 1},
		{-1, 0, 1},
		{1, 0, -1},
		{-1, 0, -1},
		{0, 1, 1},
		{0, -1, 1},
		{0, 1, -1},
		{0, -1, -1},
		{1, 1, 0},
		{0, -1, 1},
		{-1, 1, 0},
		{0, -1, -1}
	};
	private static final double SQRT_3 = Math.sqrt(3.0);
	private static final double SKEW_FACTOR_2D = 0.5 * (SQRT_3 - 1.0);
	private static final double UNSKEW_FACTOR_2D = (3.0 - SQRT_3) / 6.0;
	private final int[] permutation = new int[512];
	public final double originX;
	public final double originY;
	public final double originZ;

	public SimplexNoiseSampler(Random random) {
		this.originX = random.nextDouble() * 256.0;
		this.originY = random.nextDouble() * 256.0;
		this.originZ = random.nextDouble() * 256.0;
		int i = 0;

		while (i < 256) {
			this.permutation[i] = i++;
		}

		for (int ix = 0; ix < 256; ix++) {
			int j = random.nextInt(256 - ix);
			int k = this.permutation[ix];
			this.permutation[ix] = this.permutation[j + ix];
			this.permutation[j + ix] = k;
		}
	}

	private int map(int input) {
		return this.permutation[input & 0xFF];
	}

	protected static double dot(int[] gradient, double x, double y, double z) {
		return (double)gradient[0] * x + (double)gradient[1] * y + (double)gradient[2] * z;
	}

	private double grad(int hash, double x, double y, double z, double distance) {
		double d = distance - x * x - y * y - z * z;
		double e;
		if (d < 0.0) {
			e = 0.0;
		} else {
			d *= d;
			e = d * d * dot(GRADIENTS[hash], x, y, z);
		}

		return e;
	}

	public double sample(double x, double y) {
		double d = (x + y) * SKEW_FACTOR_2D;
		int i = MathHelper.floor(x + d);
		int j = MathHelper.floor(y + d);
		double e = (double)(i + j) * UNSKEW_FACTOR_2D;
		double f = (double)i - e;
		double g = (double)j - e;
		double h = x - f;
		double k = y - g;
		int l;
		int m;
		if (h > k) {
			l = 1;
			m = 0;
		} else {
			l = 0;
			m = 1;
		}

		double n = h - (double)l + UNSKEW_FACTOR_2D;
		double o = k - (double)m + UNSKEW_FACTOR_2D;
		double p = h - 1.0 + 2.0 * UNSKEW_FACTOR_2D;
		double q = k - 1.0 + 2.0 * UNSKEW_FACTOR_2D;
		int r = i & 0xFF;
		int s = j & 0xFF;
		int t = this.map(r + this.map(s)) % 12;
		int u = this.map(r + l + this.map(s + m)) % 12;
		int v = this.map(r + 1 + this.map(s + 1)) % 12;
		double w = this.grad(t, h, k, 0.0, 0.5);
		double z = this.grad(u, n, o, 0.0, 0.5);
		double aa = this.grad(v, p, q, 0.0, 0.5);
		return 70.0 * (w + z + aa);
	}

	public double sample(double x, double y, double z) {
		double d = 0.3333333333333333;
		double e = (x + y + z) * 0.3333333333333333;
		int i = MathHelper.floor(x + e);
		int j = MathHelper.floor(y + e);
		int k = MathHelper.floor(z + e);
		double f = 0.16666666666666666;
		double g = (double)(i + j + k) * 0.16666666666666666;
		double h = (double)i - g;
		double l = (double)j - g;
		double m = (double)k - g;
		double n = x - h;
		double o = y - l;
		double p = z - m;
		int q;
		int r;
		int s;
		int t;
		int u;
		int v;
		if (n >= o) {
			if (o >= p) {
				q = 1;
				r = 0;
				s = 0;
				t = 1;
				u = 1;
				v = 0;
			} else if (n >= p) {
				q = 1;
				r = 0;
				s = 0;
				t = 1;
				u = 0;
				v = 1;
			} else {
				q = 0;
				r = 0;
				s = 1;
				t = 1;
				u = 0;
				v = 1;
			}
		} else if (o < p) {
			q = 0;
			r = 0;
			s = 1;
			t = 0;
			u = 1;
			v = 1;
		} else if (n < p) {
			q = 0;
			r = 1;
			s = 0;
			t = 0;
			u = 1;
			v = 1;
		} else {
			q = 0;
			r = 1;
			s = 0;
			t = 1;
			u = 1;
			v = 0;
		}

		double w = n - (double)q + 0.16666666666666666;
		double aa = o - (double)r + 0.16666666666666666;
		double ab = p - (double)s + 0.16666666666666666;
		double ac = n - (double)t + 0.3333333333333333;
		double ad = o - (double)u + 0.3333333333333333;
		double ae = p - (double)v + 0.3333333333333333;
		double af = n - 1.0 + 0.5;
		double ag = o - 1.0 + 0.5;
		double ah = p - 1.0 + 0.5;
		int ai = i & 0xFF;
		int aj = j & 0xFF;
		int ak = k & 0xFF;
		int al = this.map(ai + this.map(aj + this.map(ak))) % 12;
		int am = this.map(ai + q + this.map(aj + r + this.map(ak + s))) % 12;
		int an = this.map(ai + t + this.map(aj + u + this.map(ak + v))) % 12;
		int ao = this.map(ai + 1 + this.map(aj + 1 + this.map(ak + 1))) % 12;
		double ap = this.grad(al, n, o, p, 0.6);
		double aq = this.grad(am, w, aa, ab, 0.6);
		double ar = this.grad(an, ac, ad, ae, 0.6);
		double as = this.grad(ao, af, ag, ah, 0.6);
		return 32.0 * (ap + aq + ar + as);
	}
}
