package net.minecraft.world.gen.chunk;

import java.util.Random;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.NoiseHelper;
import net.minecraft.world.gen.SimpleRandom;

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
		this.field_33658 = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, 1.0);
		this.field_33659 = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, 1.0);
		this.field_33660 = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -7, 1.0);
		this.field_33661 = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -7, 1.0);
	}

	public void method_36471(double[] buffer, int x, int z, int minY, int noiseSizeY) {
		this.sample(buffer, x, z, minY, noiseSizeY, this.field_33658, 1.0);
	}

	public void method_36474(double[] buffer, int x, int z, int minY, int noiseSizeY) {
		this.sample(buffer, x, z, minY, noiseSizeY, this.field_33659, 1.0);
	}

	public void method_36475(double[] buffer, int x, int z, int minY, int noiseSizeY) {
		this.sample(buffer, x, z, minY, noiseSizeY, this.field_33660, 2.6666666666666665, 2.6666666666666665);
	}

	public void method_36476(double[] buffer, int x, int z, int minY, int noiseSizeY) {
		this.sample(buffer, x, z, minY, noiseSizeY, this.field_33661, 2.6666666666666665, 2.6666666666666665);
	}

	public void sample(double[] buffer, int x, int z, int minY, int noiseSizeY, DoublePerlinNoiseSampler sampler, double scale) {
		this.sample(buffer, x, z, minY, noiseSizeY, sampler, scale, scale);
	}

	public void sample(double[] buffer, int x, int z, int minY, int noiseSizeY, DoublePerlinNoiseSampler sampler, double horizontalScale, double verticalScale) {
		int i = 8;
		int j = 4;

		for (int k = 0; k < noiseSizeY; k++) {
			int l = k + minY;
			int m = x * 4;
			int n = l * 8;
			int o = z * 4;
			double d;
			if (n < 38) {
				d = NoiseHelper.lerpFromProgress(sampler, (double)m * horizontalScale, (double)n * verticalScale, (double)o * horizontalScale, -1.0, 1.0);
			} else {
				d = 1.0;
			}

			buffer[k] = d;
		}
	}

	public double method_36470(double weight, int x, int y, int z, double d, double e, double f, double g, int minY) {
		if (y > 30 || y < minY + 4) {
			return weight;
		} else if (weight < 0.0) {
			return weight;
		} else if (d < 0.0) {
			return weight;
		} else {
			double h = 0.05;
			double i = 0.1;
			double j = MathHelper.clampedLerpFromProgress(e, -1.0, 1.0, 0.05, 0.1);
			double k = Math.abs(1.5 * f) - j;
			double l = Math.abs(1.5 * g) - j;
			double m = Math.max(k, l);
			return Math.min(weight, m);
		}
	}
}
