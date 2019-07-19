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

	private int getGradient(int hash) {
		return this.permutations[hash & 0xFF];
	}

	protected static double dot(int[] gArr, double x, double y, double z) {
		return (double)gArr[0] * x + (double)gArr[1] * y + (double)gArr[2] * z;
	}

	private double grad(int hash, double x, double y, double z, double d) {
		double e = d - x * x - y * y - z * z;
		double f;
		if (e < 0.0) {
			f = 0.0;
		} else {
			e *= e;
			f = e * e * dot(gradients[hash], x, y, z);
		}

		return f;
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
}
