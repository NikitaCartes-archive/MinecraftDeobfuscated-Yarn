package net.minecraft.world.gen;

import net.minecraft.class_6357;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;

public class NoiseCaveSampler implements class_6357 {
	private final int minY;
	private final DoublePerlinNoiseSampler terrainAdditionNoise;
	private final DoublePerlinNoiseSampler pillarNoise;
	private final DoublePerlinNoiseSampler pillarFalloffNoise;
	private final DoublePerlinNoiseSampler pillarScaleNoise;
	private final DoublePerlinNoiseSampler scaledCaveScaleNoise;
	private final DoublePerlinNoiseSampler horizontalCaveNoise;
	private final DoublePerlinNoiseSampler caveScaleNoise;
	private final DoublePerlinNoiseSampler caveFalloffNoise;
	private final DoublePerlinNoiseSampler tunnelNoise1;
	private final DoublePerlinNoiseSampler tunnelNoise2;
	private final DoublePerlinNoiseSampler tunnelScaleNoise;
	private final DoublePerlinNoiseSampler tunnelFalloffNoise;
	private final DoublePerlinNoiseSampler offsetNoise;
	private final DoublePerlinNoiseSampler offsetScaleNoise;
	private final DoublePerlinNoiseSampler field_28842;
	private final DoublePerlinNoiseSampler caveDensityNoise;
	private static final int field_31463 = 128;
	private static final int field_31464 = 170;

	public NoiseCaveSampler(WorldGenRandom random, int minY) {
		this.minY = minY;
		this.pillarNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -7, 1.0, 1.0);
		this.pillarFalloffNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, 1.0);
		this.pillarScaleNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, 1.0);
		this.scaledCaveScaleNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -7, 1.0);
		this.horizontalCaveNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, 1.0);
		this.caveScaleNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -11, 1.0);
		this.caveFalloffNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -11, 1.0);
		this.tunnelNoise1 = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -7, 1.0);
		this.tunnelNoise2 = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -7, 1.0);
		this.tunnelScaleNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -11, 1.0);
		this.tunnelFalloffNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, 1.0);
		this.offsetNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -5, 1.0);
		this.offsetScaleNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, 1.0);
		this.field_28842 = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, 1.0, 1.0, 1.0);
		this.terrainAdditionNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, 1.0);
		this.caveDensityNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, 0.5, 1.0, 2.0, 1.0, 2.0, 1.0, 0.0, 2.0, 0.0);
	}

	@Override
	public double sample(double d, int i, int j, int k) {
		boolean bl = d < 170.0;
		double e = this.getTunnelOffsetNoise(k, i, j);
		double f = this.getTunnelNoise(k, i, j);
		if (bl) {
			return Math.min(d, (f + e) * 128.0 * 5.0);
		} else {
			double g = this.caveDensityNoise.sample((double)k, (double)i / 1.5, (double)j);
			double h = MathHelper.clamp(g + 0.25, -1.0, 1.0);
			double l = (double)((float)(30 - i) / 8.0F);
			double m = h + MathHelper.clampedLerp(0.5, 0.0, l);
			double n = this.getTerrainAdditionNoise(k, i, j);
			double o = this.getCaveNoise(k, i, j);
			double p = m + n;
			double q = Math.min(p, Math.min(f, o) + e);
			double r = Math.max(q, this.getPillarNoise(k, i, j));
			return 128.0 * MathHelper.clamp(r, -1.0, 1.0);
		}
	}

	private double method_35325(double d, int i, int j, int k) {
		double e = this.field_28842.sample((double)(i * 2), (double)j, (double)(k * 2));
		e = NoiseHelper.method_35479(e, 1.0);
		int l = 0;
		double f = (double)(j - 0) / 40.0;
		e += MathHelper.clampedLerp(0.5, d, f);
		double g = 3.0;
		e = 4.0 * e + 3.0;
		return Math.min(d, e);
	}

	private double getPillarNoise(int x, int y, int z) {
		double d = 0.0;
		double e = 2.0;
		double f = NoiseHelper.lerpFromProgress(this.pillarFalloffNoise, (double)x, (double)y, (double)z, 0.0, 2.0);
		double g = 0.0;
		double h = 1.1;
		double i = NoiseHelper.lerpFromProgress(this.pillarScaleNoise, (double)x, (double)y, (double)z, 0.0, 1.1);
		i = Math.pow(i, 3.0);
		double j = 25.0;
		double k = 0.3;
		double l = this.pillarNoise.sample((double)x * 25.0, (double)y * 0.3, (double)z * 25.0);
		l = i * (l * 2.0 - f);
		return l > 0.03 ? l : Double.NEGATIVE_INFINITY;
	}

	private double getTerrainAdditionNoise(int x, int y, int z) {
		double d = this.terrainAdditionNoise.sample((double)x, (double)(y * 8), (double)z);
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
		double h = NoiseHelper.lerpFromProgress(this.caveFalloffNoise, (double)(x * 2), (double)y, (double)(z * 2), 0.6, 1.3);
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

	private double getTunnelOffsetNoise(int x, int y, int z) {
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
		private CaveScaler() {
		}

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
