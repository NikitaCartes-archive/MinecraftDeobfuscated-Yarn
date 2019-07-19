package net.minecraft.util.math.noise;

import java.util.Random;

public class OctaveSimplexNoiseSampler implements NoiseSampler {
	private final SimplexNoiseSampler[] octaveSamplers;
	private final int octaveCount;

	public OctaveSimplexNoiseSampler(Random random, int octaveCount) {
		this.octaveCount = octaveCount;
		this.octaveSamplers = new SimplexNoiseSampler[octaveCount];

		for (int i = 0; i < octaveCount; i++) {
			this.octaveSamplers[i] = new SimplexNoiseSampler(random);
		}
	}

	public double sample(double x, double y) {
		return this.sample(x, y, false);
	}

	public double sample(double x, double y, boolean bl) {
		double d = 0.0;
		double e = 1.0;

		for (int i = 0; i < this.octaveCount; i++) {
			d += this.octaveSamplers[i].sample(x * e + (bl ? this.octaveSamplers[i].originX : 0.0), y * e + (bl ? this.octaveSamplers[i].originY : 0.0)) / e;
			e /= 2.0;
		}

		return d;
	}

	@Override
	public double sample(double x, double y, double d, double e) {
		return this.sample(x, y, true) * 0.55;
	}
}
