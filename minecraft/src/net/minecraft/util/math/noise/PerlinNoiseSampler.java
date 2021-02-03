package net.minecraft.util.math.noise;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.WorldGenRandom;

public final class PerlinNoiseSampler {
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
}
