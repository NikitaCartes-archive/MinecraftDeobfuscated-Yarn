package net.minecraft.world.gen.chunk;

import java.util.Random;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.NoiseHelper;
import net.minecraft.world.gen.SimpleRandom;

public class NoodleCavesGenerator {
	private static final int MAX_Y = 30;
	private static final double WEIGHT_NOISE_FACTOR = 1.5;
	private static final double HORIZONTAL_WEIGHT_NOISE_SCALE = 2.6666666666666665;
	private static final double VERTICAL_WEIGHT_NOISE_SCALE = 2.6666666666666665;
	private final DoublePerlinNoiseSampler frequencyNoiseSampler;
	private final DoublePerlinNoiseSampler weightReducingNoiseSampler;
	private final DoublePerlinNoiseSampler firstWeightNoiseSampelr;
	private final DoublePerlinNoiseSampler secondWeightNoiseSampler;

	public NoodleCavesGenerator(long seed) {
		Random random = new Random(seed);
		this.frequencyNoiseSampler = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, 1.0);
		this.weightReducingNoiseSampler = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, 1.0);
		this.firstWeightNoiseSampelr = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -7, 1.0);
		this.secondWeightNoiseSampler = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -7, 1.0);
	}

	public void sampleFrequencyNoise(double[] buffer, int x, int z, int minY, int noiseSizeY) {
		this.sample(buffer, x, z, minY, noiseSizeY, this.frequencyNoiseSampler, 1.0);
	}

	public void sampleWeightReducingNoise(double[] buffer, int x, int z, int minY, int noiseSizeY) {
		this.sample(buffer, x, z, minY, noiseSizeY, this.weightReducingNoiseSampler, 1.0);
	}

	public void sampleFirstWeightNoise(double[] buffer, int x, int z, int minY, int noiseSizeY) {
		this.sample(buffer, x, z, minY, noiseSizeY, this.firstWeightNoiseSampelr, 2.6666666666666665, 2.6666666666666665);
	}

	public void sampleSecondWeightNoise(double[] buffer, int x, int z, int minY, int noiseSizeY) {
		this.sample(buffer, x, z, minY, noiseSizeY, this.secondWeightNoiseSampler, 2.6666666666666665, 2.6666666666666665);
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

	public double sampleWeight(
		double weight, int x, int y, int z, double frequencyNoise, double weightReducingNoise, double firstWeightNoise, double secondWeightNoise, int minY
	) {
		if (y > 30 || y < minY + 4) {
			return weight;
		} else if (weight < 0.0) {
			return weight;
		} else if (frequencyNoise < 0.0) {
			return weight;
		} else {
			double d = 0.05;
			double e = 0.1;
			double f = MathHelper.clampedLerpFromProgress(weightReducingNoise, -1.0, 1.0, 0.05, 0.1);
			double g = Math.abs(1.5 * firstWeightNoise) - f;
			double h = Math.abs(1.5 * secondWeightNoise) - f;
			double i = Math.max(g, h);
			return Math.min(weight, i);
		}
	}
}
