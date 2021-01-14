package net.minecraft.util.math.noise;

import java.util.Random;
import net.minecraft.util.math.MathHelper;

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
	private final int[] permutations = new int[512];
	public final double originX;
	public final double originY;
	public final double originZ;

	public SimplexNoiseSampler(Random random) {
		this.originX = random.nextDouble() * 256.0;
		this.originY = random.nextDouble() * 256.0;
		this.originZ = random.nextDouble() * 256.0;
		int i = 0;

		while (i < 256) {
			this.permutations[i] = i++;
		}

		for (int ix = 0; ix < 256; ix++) {
			int j = random.nextInt(256 - ix);
			int k = this.permutations[ix];
			this.permutations[ix] = this.permutations[j + ix];
			this.permutations[j + ix] = k;
		}
	}

	private int getGradient(int hash) {
		return this.permutations[hash & 0xFF];
	}

	protected static double dot(int[] gArr, double x, double y, double z) {
		return (double)gArr[0] * x + (double)gArr[1] * y + (double)gArr[2] * z;
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
		int t = this.getGradient(r + this.getGradient(s)) % 12;
		int u = this.getGradient(r + l + this.getGradient(s + m)) % 12;
		int v = this.getGradient(r + 1 + this.getGradient(s + 1)) % 12;
		double w = this.grad(t, h, k, 0.0, 0.5);
		double z = this.grad(u, n, o, 0.0, 0.5);
		double aa = this.grad(v, p, q, 0.0, 0.5);
		return 70.0 * (w + z + aa);
	}

	public double method_22416(double d, double e, double f) {
		double g = 0.3333333333333333;
		double h = (d + e + f) * 0.3333333333333333;
		int i = MathHelper.floor(d + h);
		int j = MathHelper.floor(e + h);
		int k = MathHelper.floor(f + h);
		double l = 0.16666666666666666;
		double m = (double)(i + j + k) * 0.16666666666666666;
		double n = (double)i - m;
		double o = (double)j - m;
		double p = (double)k - m;
		double q = d - n;
		double r = e - o;
		double s = f - p;
		int t;
		int u;
		int v;
		int w;
		int x;
		int y;
		if (q >= r) {
			if (r >= s) {
				t = 1;
				u = 0;
				v = 0;
				w = 1;
				x = 1;
				y = 0;
			} else if (q >= s) {
				t = 1;
				u = 0;
				v = 0;
				w = 1;
				x = 0;
				y = 1;
			} else {
				t = 0;
				u = 0;
				v = 1;
				w = 1;
				x = 0;
				y = 1;
			}
		} else if (r < s) {
			t = 0;
			u = 0;
			v = 1;
			w = 0;
			x = 1;
			y = 1;
		} else if (q < s) {
			t = 0;
			u = 1;
			v = 0;
			w = 0;
			x = 1;
			y = 1;
		} else {
			t = 0;
			u = 1;
			v = 0;
			w = 1;
			x = 1;
			y = 0;
		}

		double z = q - (double)t + 0.16666666666666666;
		double aa = r - (double)u + 0.16666666666666666;
		double ab = s - (double)v + 0.16666666666666666;
		double ac = q - (double)w + 0.3333333333333333;
		double ad = r - (double)x + 0.3333333333333333;
		double ae = s - (double)y + 0.3333333333333333;
		double af = q - 1.0 + 0.5;
		double ag = r - 1.0 + 0.5;
		double ah = s - 1.0 + 0.5;
		int ai = i & 0xFF;
		int aj = j & 0xFF;
		int ak = k & 0xFF;
		int al = this.getGradient(ai + this.getGradient(aj + this.getGradient(ak))) % 12;
		int am = this.getGradient(ai + t + this.getGradient(aj + u + this.getGradient(ak + v))) % 12;
		int an = this.getGradient(ai + w + this.getGradient(aj + x + this.getGradient(ak + y))) % 12;
		int ao = this.getGradient(ai + 1 + this.getGradient(aj + 1 + this.getGradient(ak + 1))) % 12;
		double ap = this.grad(al, q, r, s, 0.6);
		double aq = this.grad(am, z, aa, ab, 0.6);
		double ar = this.grad(an, ac, ad, ae, 0.6);
		double as = this.grad(ao, af, ag, ah, 0.6);
		return 32.0 * (ap + aq + ar + as);
	}
}
