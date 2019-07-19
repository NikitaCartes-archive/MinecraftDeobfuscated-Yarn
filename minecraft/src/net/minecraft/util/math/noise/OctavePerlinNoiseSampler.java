package net.minecraft.util.math.noise;

import java.util.Random;
import net.minecraft.util.math.MathHelper;

public class OctavePerlinNoiseSampler implements NoiseSampler {
	private final PerlinNoiseSampler[] octaveSamplers;

	public OctavePerlinNoiseSampler(Random random, int octaveCount) {
		this.octaveSamplers = new PerlinNoiseSampler[octaveCount];

		for (int i = 0; i < octaveCount; i++) {
			this.octaveSamplers[i] = new PerlinNoiseSampler(random);
		}
	}

	public double sample(double x, double y, double z) {
		return this.sample(x, y, z, 0.0, 0.0, false);
	}

	public double sample(double x, double y, double z, double d, double e, boolean bl) {
		double f = 0.0;
		double g = 1.0;

		for (PerlinNoiseSampler perlinNoiseSampler : this.octaveSamplers) {
			f += perlinNoiseSampler.sample(maintainPrecision(x * g), bl ? -perlinNoiseSampler.originY : maintainPrecision(y * g), maintainPrecision(z * g), d * g, e * g)
				/ g;
			g /= 2.0;
		}

		return f;
	}

	public PerlinNoiseSampler getOctave(int octave) {
		return this.octaveSamplers[octave];
	}

	public static double maintainPrecision(double d) {
		return d - (double)MathHelper.lfloor(d / 3.3554432E7 + 0.5) * 3.3554432E7;
	}

	@Override
	public double sample(double x, double y, double d, double e) {
		return this.sample(x, y, 0.0, d, e, false);
	}
}
