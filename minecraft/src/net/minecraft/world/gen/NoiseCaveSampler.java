package net.minecraft.world.gen;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;

public class NoiseCaveSampler {
	private final int minY;
	private final DoublePerlinNoiseSampler verticalOffsetNoise;
	private final DoublePerlinNoiseSampler verticalAdditionNoise;
	private final DoublePerlinNoiseSampler falloffNoise;
	private final DoublePerlinNoiseSampler pow3AdditionNoise;
	private final DoublePerlinNoiseSampler scaledCaveScaleNoise;
	private final DoublePerlinNoiseSampler horizontalCaveNoise;
	private final DoublePerlinNoiseSampler caveScaleNoise;
	private final DoublePerlinNoiseSampler caveExtentNoise;
	private final DoublePerlinNoiseSampler tunnelNoise1;
	private final DoublePerlinNoiseSampler tunnelNoise2;
	private final DoublePerlinNoiseSampler tunnelScaleNoise;
	private final DoublePerlinNoiseSampler tunnelFalloffNoise;
	private final DoublePerlinNoiseSampler offsetNoise;
	private final DoublePerlinNoiseSampler offsetScaleNoise;
	private final DoublePerlinNoiseSampler field_28842;

	public NoiseCaveSampler(WorldGenRandom random, int minY) {
		this.minY = minY;
		this.verticalAdditionNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -7, 1.0, 1.0);
		this.falloffNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, 1.0);
		this.pow3AdditionNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, 1.0);
		this.scaledCaveScaleNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -7, 1.0);
		this.horizontalCaveNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, 1.0);
		this.caveScaleNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -11, 1.0);
		this.caveExtentNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -11, 1.0);
		this.tunnelNoise1 = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -7, 1.0);
		this.tunnelNoise2 = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -7, 1.0);
		this.tunnelScaleNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -11, 1.0);
		this.tunnelFalloffNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, 1.0);
		this.offsetNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -5, 1.0);
		this.offsetScaleNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, 1.0);
		this.field_28842 = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, 1.0, 1.0, 1.0);
		this.verticalOffsetNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, 1.0);
	}

	public double sample(int x, int y, int z, double noise, double offset) {
		boolean bl = offset >= 375.0;
		double d = this.getOffsetNoise(x, y, z);
		double e = this.getTunnelNoise(x, y, z);
		if (bl) {
			double f = noise / 128.0;
			double g = MathHelper.clamp(f + 0.25, -1.0, 1.0);
			double h = this.getVerticalNoise(x, y, z);
			double i = this.getCaveNoise(x, y, z);
			double j = g + h;
			double k = Math.min(j, Math.min(e, i) + d);
			double l = Math.max(k, this.getAdditionNoise(x, y, z));
			return 128.0 * MathHelper.clamp(l, -1.0, 1.0);
		} else {
			return Math.min(offset, (e + d) * 128.0);
		}
	}

	private double getAdditionNoise(int x, int y, int z) {
		double d = 0.0;
		double e = 2.0;
		double f = NoiseHelper.lerpFromProgress(this.falloffNoise, (double)x, (double)y, (double)z, 0.0, 2.0);
		int i = 0;
		int j = 1;
		double g = NoiseHelper.lerpFromProgress(this.pow3AdditionNoise, (double)x, (double)y, (double)z, 0.0, 1.0);
		g = Math.pow(g, 3.0);
		double h = 25.0;
		double k = 0.3;
		double l = this.verticalAdditionNoise.sample((double)x * 25.0, (double)y * 0.3, (double)z * 25.0);
		l = g * (l * 2.0 - f);
		return l > 0.02 ? l : Double.NEGATIVE_INFINITY;
	}

	private double getVerticalNoise(int x, int y, int z) {
		double d = this.verticalOffsetNoise.sample((double)x, (double)(y * 8), (double)z);
		return MathHelper.square(d) * 4.0;
	}

	private double getTunnelNoise(int x, int y, int z) {
		double d = this.tunnelScaleNoise.sample((double)(x * 2), (double)y, (double)(z * 2));
		double e = NoiseCaveSampler.CaveScaler.scaleTunnels(d);
		double f = 0.065;
		double g = 0.088;
		double h = NoiseHelper.lerpFromProgress(this.tunnelFalloffNoise, (double)x, (double)y, (double)z, 0.065, 0.088);
		double i = sample(this.tunnelNoise1, (double)x, (double)y, (double)z, e);
		double j = Math.abs(e * i) - h;
		double k = sample(this.tunnelNoise2, (double)x, (double)y, (double)z, e);
		double l = Math.abs(e * k) - h;
		return clamp(Math.max(j, l));
	}

	private double getCaveNoise(int x, int y, int z) {
		double d = this.caveScaleNoise.sample((double)(x * 2), (double)y, (double)(z * 2));
		double e = NoiseCaveSampler.CaveScaler.scaleCaves(d);
		double f = 0.6;
		double g = 1.3;
		double h = NoiseHelper.lerpFromProgress(this.caveExtentNoise, (double)(x * 2), (double)y, (double)(z * 2), 0.6, 1.3);
		double i = sample(this.scaledCaveScaleNoise, (double)x, (double)y, (double)z, e);
		double j = 0.083;
		double k = Math.abs(e * i) - 0.083 * h;
		int l = this.minY;
		int m = 8;
		double n = NoiseHelper.lerpFromProgress(this.horizontalCaveNoise, (double)x, 0.0, (double)z, (double)l, 8.0);
		double o = Math.abs(n - (double)y / 8.0) - 1.0 * h;
		o = o * o * o;
		return clamp(Math.max(o, k));
	}

	private double getOffsetNoise(int x, int y, int z) {
		double d = NoiseHelper.lerpFromProgress(this.offsetScaleNoise, (double)x, (double)y, (double)z, 0.0, 0.1);
		return (0.4 - Math.abs(this.offsetNoise.sample((double)x, (double)y, (double)z))) * d;
	}

	private static double clamp(double value) {
		return MathHelper.clamp(value, -1.0, 1.0);
	}

	private static double sample(DoublePerlinNoiseSampler sampler, double x, double y, double z, double scale) {
		return sampler.sample(x / scale, y / scale, z / scale);
	}

	static final class CaveScaler {
		private static double scaleCaves(double value) {
			if (value < -0.75) {
				return 0.5;
			} else if (value < -0.5) {
				return 0.75;
			} else if (value < 0.5) {
				return 1.0;
			} else {
				return value < 0.75 ? 2.0 : 3.0;
			}
		}

		private static double scaleTunnels(double value) {
			if (value < -0.5) {
				return 0.75;
			} else if (value < 0.0) {
				return 1.0;
			} else {
				return value < 0.5 ? 1.5 : 2.0;
			}
		}
	}
}
