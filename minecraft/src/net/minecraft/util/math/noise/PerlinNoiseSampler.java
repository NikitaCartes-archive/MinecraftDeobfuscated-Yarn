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
		double m = MathHelper.perlinFade(g);
		double n = MathHelper.perlinFade(h);
		double o = MathHelper.perlinFade(l);
		double q;
		if (yScale != 0.0) {
			double p = Math.min(yMax, h);
			q = (double)MathHelper.floor(p / yScale) * yScale;
		} else {
			q = 0.0;
		}

		return this.sample(i, j, k, g, h - q, l, m, n, o);
	}

	private static double grad(int hash, double x, double y, double z) {
		int i = hash & 15;
		return SimplexNoiseSampler.dot(SimplexNoiseSampler.GRADIENTS[i], x, y, z);
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
