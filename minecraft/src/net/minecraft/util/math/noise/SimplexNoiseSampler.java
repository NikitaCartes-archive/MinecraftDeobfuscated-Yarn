package net.minecraft.util.math.noise;

import java.util.Random;
import net.minecraft.util.math.MathHelper;

public class SimplexNoiseSampler {
	protected static final int[][] gradients = new int[][]{
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
	private static final double sqrt3 = Math.sqrt(3.0);
	private static final double SKEW_FACTOR_2D = 0.5 * (sqrt3 - 1.0);
	private static final double UNSKEW_FACTOR_2D = (3.0 - sqrt3) / 6.0;
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

	private int getGradient(int i) {
		return this.permutations[i & 0xFF];
	}

	protected static double dot(int[] is, double d, double e, double f) {
		return (double)is[0] * d + (double)is[1] * e + (double)is[2] * f;
	}

	private double grad(int i, double d, double e, double f, double g) {
		double h = g - d * d - e * e - f * f;
		double j;
		if (h < 0.0) {
			j = 0.0;
		} else {
			h *= h;
			j = h * h * dot(gradients[i], d, e, f);
		}

		return j;
	}

	public double sample(double d, double e) {
		double f = (d + e) * SKEW_FACTOR_2D;
		int i = MathHelper.floor(d + f);
		int j = MathHelper.floor(e + f);
		double g = (double)(i + j) * UNSKEW_FACTOR_2D;
		double h = (double)i - g;
		double k = (double)j - g;
		double l = d - h;
		double m = e - k;
		int n;
		int o;
		if (l > m) {
			n = 1;
			o = 0;
		} else {
			n = 0;
			o = 1;
		}

		double p = l - (double)n + UNSKEW_FACTOR_2D;
		double q = m - (double)o + UNSKEW_FACTOR_2D;
		double r = l - 1.0 + 2.0 * UNSKEW_FACTOR_2D;
		double s = m - 1.0 + 2.0 * UNSKEW_FACTOR_2D;
		int t = i & 0xFF;
		int u = j & 0xFF;
		int v = this.getGradient(t + this.getGradient(u)) % 12;
		int w = this.getGradient(t + n + this.getGradient(u + o)) % 12;
		int x = this.getGradient(t + 1 + this.getGradient(u + 1)) % 12;
		double y = this.grad(v, l, m, 0.0, 0.5);
		double z = this.grad(w, p, q, 0.0, 0.5);
		double aa = this.grad(x, r, s, 0.0, 0.5);
		return 70.0 * (y + z + aa);
	}
}
