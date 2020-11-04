package net.minecraft.util.math.noise;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import net.minecraft.world.gen.ChunkRandom;

public class DoublePerlinNoiseSampler {
	private final double amplitude;
	private final OctavePerlinNoiseSampler firstSampler;
	private final OctavePerlinNoiseSampler secondSampler;

	public static DoublePerlinNoiseSampler create(ChunkRandom random, int offset, double... octaves) {
		return new DoublePerlinNoiseSampler(random, offset, new DoubleArrayList(octaves));
	}

	public static DoublePerlinNoiseSampler create(ChunkRandom random, int offset, DoubleList octaves) {
		return new DoublePerlinNoiseSampler(random, offset, octaves);
	}

	private DoublePerlinNoiseSampler(ChunkRandom random, int offset, DoubleList octaves) {
		this.firstSampler = OctavePerlinNoiseSampler.create(random, offset, octaves);
		this.secondSampler = OctavePerlinNoiseSampler.create(random, offset, octaves);
		int i = Integer.MAX_VALUE;
		int j = Integer.MIN_VALUE;
		DoubleListIterator doubleListIterator = octaves.iterator();

		while (doubleListIterator.hasNext()) {
			int k = doubleListIterator.nextIndex();
			double d = doubleListIterator.nextDouble();
			if (d != 0.0) {
				i = Math.min(i, k);
				j = Math.max(j, k);
			}
		}

		this.amplitude = 0.16666666666666666 / createAmplitude(j - i);
	}

	private static double createAmplitude(int octaves) {
		return 0.1 * (1.0 + 1.0 / (double)(octaves + 1));
	}

	public double sample(double x, double y, double z) {
		double d = x * 1.0181268882175227;
		double e = y * 1.0181268882175227;
		double f = z * 1.0181268882175227;
		return (this.firstSampler.sample(x, y, z) + this.secondSampler.sample(d, e, f)) * this.amplitude;
	}
}
