package net.minecraft.util.math.noise;

import java.util.Random;
import net.minecraft.util.math.MathHelper;

public final class PerlinNoiseSampler {
	private final byte[] permutations;
	public final double originX;
	public final double originY;
	public final double originZ;

	public PerlinNoiseSampler(Random random) {
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

	public double sample(double x, double y, double z, double d, double e) {
		double f = x + this.originX;
		double g = y + this.originY;
		double h = z + this.originZ;
		int i = MathHelper.floor(f);
		int j = MathHelper.floor(g);
		int k = MathHelper.floor(h);
		double l = f - (double)i;
		double m = g - (double)j;
		double n = h - (double)k;
		double o = MathHelper.perlinFade(l);
		double p = MathHelper.perlinFade(m);
		double q = MathHelper.perlinFade(n);
		double s;
		if (d != 0.0) {
			double r = Math.min(e, m);
			s = (double)MathHelper.floor(r / d) * d;
		} else {
			s = 0.0;
		}

		return this.sample(i, j, k, l, m - s, n, o, p, q);
	}

	private static double grad(int hash, double x, double y, double z) {
		int i = hash & 15;
		return SimplexNoiseSampler.dot(SimplexNoiseSampler.gradients[i], x, y, z);
	}

	private int getGradient(int hash) {
		return this.permutations[hash & 0xFF] & 0xFF;
	}

	public double sample(
		int sectionX, int sectionY, int sectionZ, double localX, double localY, double localZ, double fadeLocalX, double fadeLocalY, double fadeLocalZ
	) {
		int i = this.getGradient(sectionX) + sectionY;
		int j = this.getGradient(i) + sectionZ;
		int k = this.getGradient(i + 1) + sectionZ;
		int l = this.getGradient(sectionX + 1) + sectionY;
		int m = this.getGradient(l) + sectionZ;
		int n = this.getGradient(l + 1) + sectionZ;
		double d = grad(this.getGradient(j), localX, localY, localZ);
		double e = grad(this.getGradient(m), localX - 1.0, localY, localZ);
		double f = grad(this.getGradient(k), localX, localY - 1.0, localZ);
		double g = grad(this.getGradient(n), localX - 1.0, localY - 1.0, localZ);
		double h = grad(this.getGradient(j + 1), localX, localY, localZ - 1.0);
		double o = grad(this.getGradient(m + 1), localX - 1.0, localY, localZ - 1.0);
		double p = grad(this.getGradient(k + 1), localX, localY - 1.0, localZ - 1.0);
		double q = grad(this.getGradient(n + 1), localX - 1.0, localY - 1.0, localZ - 1.0);
		return MathHelper.lerp3(fadeLocalX, fadeLocalY, fadeLocalZ, d, e, f, g, h, o, p, q);
	}
}
