package net.minecraft.util.math.noise;

import java.util.Random;

public class OctaveSimplexNoiseSampler implements NoiseSampler {
	private final SimplexNoiseSampler[] octaveSamplers;
	private final int octaveCount;

	public OctaveSimplexNoiseSampler(Random random, int i) {
		this.octaveCount = i;
		this.octaveSamplers = new SimplexNoiseSampler[i];

		for (int j = 0; j < i; j++) {
			this.octaveSamplers[j] = new SimplexNoiseSampler(random);
		}
	}

	public double sample(double d, double e) {
		return this.sample(d, e, false);
	}

	public double sample(double d, double e, boolean bl) {
		double f = 0.0;
		double g = 1.0;

		for (int i = 0; i < this.octaveCount; i++) {
			f += this.octaveSamplers[i].sample(d * g + (bl ? this.octaveSamplers[i].field_15763 : 0.0), e * g + (bl ? this.octaveSamplers[i].field_15762 : 0.0)) / g;
			g /= 2.0;
		}

		return f;
	}

	@Override
	public double sample(double d, double e, double f, double g) {
		return this.sample(d, e, true) * 0.55;
	}
}
