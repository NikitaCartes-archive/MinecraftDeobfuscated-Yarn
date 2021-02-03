package net.minecraft.util.math.noise;

import java.util.stream.IntStream;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.WorldGenRandom;

public class InterpolatedNoise {
	private OctavePerlinNoiseSampler lowerInterpolatedNoise;
	private OctavePerlinNoiseSampler upperInterpolatedNoise;
	private OctavePerlinNoiseSampler interpolationNoise;

	public InterpolatedNoise(
		OctavePerlinNoiseSampler lowerInterpolatedNoise, OctavePerlinNoiseSampler upperInterpolatedNoise, OctavePerlinNoiseSampler interpolationNoise
	) {
		this.lowerInterpolatedNoise = lowerInterpolatedNoise;
		this.upperInterpolatedNoise = upperInterpolatedNoise;
		this.interpolationNoise = interpolationNoise;
	}

	public InterpolatedNoise(WorldGenRandom random) {
		this(
			new OctavePerlinNoiseSampler(random, IntStream.rangeClosed(-15, 0)),
			new OctavePerlinNoiseSampler(random, IntStream.rangeClosed(-15, 0)),
			new OctavePerlinNoiseSampler(random, IntStream.rangeClosed(-7, 0))
		);
	}

	public double sample(int x, int y, int z, double horizontalScale, double verticalScale, double horizontalStretch, double verticalStretch) {
		double d = 0.0;
		double e = 0.0;
		double f = 0.0;
		boolean bl = true;
		double g = 1.0;

		for (int i = 0; i < 16; i++) {
			double h = OctavePerlinNoiseSampler.maintainPrecision((double)x * horizontalScale * g);
			double j = OctavePerlinNoiseSampler.maintainPrecision((double)y * verticalScale * g);
			double k = OctavePerlinNoiseSampler.maintainPrecision((double)z * horizontalScale * g);
			double l = verticalScale * g;
			PerlinNoiseSampler perlinNoiseSampler = this.lowerInterpolatedNoise.getOctave(i);
			if (perlinNoiseSampler != null) {
				d += perlinNoiseSampler.sample(h, j, k, l, (double)y * l) / g;
			}

			PerlinNoiseSampler perlinNoiseSampler2 = this.upperInterpolatedNoise.getOctave(i);
			if (perlinNoiseSampler2 != null) {
				e += perlinNoiseSampler2.sample(h, j, k, l, (double)y * l) / g;
			}

			if (i < 8) {
				PerlinNoiseSampler perlinNoiseSampler3 = this.interpolationNoise.getOctave(i);
				if (perlinNoiseSampler3 != null) {
					f += perlinNoiseSampler3.sample(
							OctavePerlinNoiseSampler.maintainPrecision((double)x * horizontalStretch * g),
							OctavePerlinNoiseSampler.maintainPrecision((double)y * verticalStretch * g),
							OctavePerlinNoiseSampler.maintainPrecision((double)z * horizontalStretch * g),
							verticalStretch * g,
							(double)y * verticalStretch * g
						)
						/ g;
				}
			}

			g /= 2.0;
		}

		return MathHelper.clampedLerp(d / 512.0, e / 512.0, (f / 10.0 + 1.0) / 2.0);
	}
}
