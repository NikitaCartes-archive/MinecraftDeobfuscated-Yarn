package net.minecraft.util.math.noise;

import java.util.stream.IntStream;
import net.minecraft.class_6568;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.chunk.NoiseSamplingConfig;
import net.minecraft.world.gen.random.AbstractRandom;

public class InterpolatedNoiseSampler implements class_6568.ColumnSampler {
	private final OctavePerlinNoiseSampler lowerInterpolatedNoise;
	private final OctavePerlinNoiseSampler upperInterpolatedNoise;
	private final OctavePerlinNoiseSampler interpolationNoise;
	private final double field_34752;
	private final double field_34753;
	private final double field_34754;
	private final double field_34755;
	private final int field_34756;
	private final int field_34757;

	public InterpolatedNoiseSampler(
		OctavePerlinNoiseSampler lowerInterpolatedNoise,
		OctavePerlinNoiseSampler upperInterpolatedNoise,
		OctavePerlinNoiseSampler interpolationNoise,
		NoiseSamplingConfig noiseSamplingConfig,
		int i,
		int j
	) {
		this.lowerInterpolatedNoise = lowerInterpolatedNoise;
		this.upperInterpolatedNoise = upperInterpolatedNoise;
		this.interpolationNoise = interpolationNoise;
		this.field_34752 = 684.412 * noiseSamplingConfig.getXZScale();
		this.field_34753 = 684.412 * noiseSamplingConfig.getYScale();
		this.field_34754 = this.field_34752 / noiseSamplingConfig.getXZFactor();
		this.field_34755 = this.field_34753 / noiseSamplingConfig.getYFactor();
		this.field_34756 = i;
		this.field_34757 = j;
	}

	public InterpolatedNoiseSampler(AbstractRandom random, NoiseSamplingConfig noiseSamplingConfig, int i, int j) {
		this(
			new OctavePerlinNoiseSampler(random, IntStream.rangeClosed(-15, 0)),
			new OctavePerlinNoiseSampler(random, IntStream.rangeClosed(-15, 0)),
			new OctavePerlinNoiseSampler(random, IntStream.rangeClosed(-7, 0)),
			noiseSamplingConfig,
			i,
			j
		);
	}

	@Override
	public double calculateNoise(int i, int j, int k) {
		int l = Math.floorDiv(i, this.field_34756);
		int m = Math.floorDiv(j, this.field_34757);
		int n = Math.floorDiv(k, this.field_34756);
		double d = 0.0;
		double e = 0.0;
		double f = 0.0;
		boolean bl = true;
		double g = 1.0;

		for (int o = 0; o < 8; o++) {
			PerlinNoiseSampler perlinNoiseSampler = this.interpolationNoise.getOctave(o);
			if (perlinNoiseSampler != null) {
				f += perlinNoiseSampler.sample(
						OctavePerlinNoiseSampler.maintainPrecision((double)l * this.field_34754 * g),
						OctavePerlinNoiseSampler.maintainPrecision((double)m * this.field_34755 * g),
						OctavePerlinNoiseSampler.maintainPrecision((double)n * this.field_34754 * g),
						this.field_34755 * g,
						(double)m * this.field_34755 * g
					)
					/ g;
			}

			g /= 2.0;
		}

		double h = (f / 10.0 + 1.0) / 2.0;
		boolean bl2 = h >= 1.0;
		boolean bl3 = h <= 0.0;
		g = 1.0;

		for (int p = 0; p < 16; p++) {
			double q = OctavePerlinNoiseSampler.maintainPrecision((double)l * this.field_34752 * g);
			double r = OctavePerlinNoiseSampler.maintainPrecision((double)m * this.field_34753 * g);
			double s = OctavePerlinNoiseSampler.maintainPrecision((double)n * this.field_34752 * g);
			double t = this.field_34753 * g;
			if (!bl2) {
				PerlinNoiseSampler perlinNoiseSampler2 = this.lowerInterpolatedNoise.getOctave(p);
				if (perlinNoiseSampler2 != null) {
					d += perlinNoiseSampler2.sample(q, r, s, t, (double)m * t) / g;
				}
			}

			if (!bl3) {
				PerlinNoiseSampler perlinNoiseSampler2 = this.upperInterpolatedNoise.getOctave(p);
				if (perlinNoiseSampler2 != null) {
					e += perlinNoiseSampler2.sample(q, r, s, t, (double)m * t) / g;
				}
			}

			g /= 2.0;
		}

		return MathHelper.clampedLerp(d / 512.0, e / 512.0, h) / 128.0;
	}
}
