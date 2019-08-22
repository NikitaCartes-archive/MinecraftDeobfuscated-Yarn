package net.minecraft.util.math.noise;

import java.util.Random;
import net.minecraft.util.math.MathHelper;

public class OctavePerlinNoiseSampler implements NoiseSampler {
	private final PerlinNoiseSampler[] octaveSamplers;

	public OctavePerlinNoiseSampler(Random random, int i) {
		this.octaveSamplers = new PerlinNoiseSampler[i];

		for (int j = 0; j < i; j++) {
			this.octaveSamplers[j] = new PerlinNoiseSampler(random);
		}
	}

	public double sample(double d, double e, double f) {
		return this.sample(d, e, f, 0.0, 0.0, false);
	}

	public double sample(double d, double e, double f, double g, double h, boolean bl) {
		double i = 0.0;
		double j = 1.0;

		for (PerlinNoiseSampler perlinNoiseSampler : this.octaveSamplers) {
			i += perlinNoiseSampler.sample(maintainPrecision(d * j), bl ? -perlinNoiseSampler.originY : maintainPrecision(e * j), maintainPrecision(f * j), g * j, h * j)
				/ j;
			j /= 2.0;
		}

		return i;
	}

	public PerlinNoiseSampler getOctave(int i) {
		return this.octaveSamplers[i];
	}

	public static double maintainPrecision(double d) {
		return d - (double)MathHelper.lfloor(d / 3.3554432E7 + 0.5) * 3.3554432E7;
	}

	@Override
	public double sample(double d, double e, double f, double g) {
		return this.sample(d, e, 0.0, f, g, false);
	}
}
