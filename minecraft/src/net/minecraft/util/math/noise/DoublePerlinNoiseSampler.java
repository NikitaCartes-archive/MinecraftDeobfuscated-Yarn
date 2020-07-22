package net.minecraft.util.math.noise;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import net.minecraft.world.gen.ChunkRandom;

public class DoublePerlinNoiseSampler {
	private final double amplitude;
	private final OctavePerlinNoiseSampler firstSampler;
	private final OctavePerlinNoiseSampler secondSampler;

	public static DoublePerlinNoiseSampler method_30846(ChunkRandom chunkRandom, int i, DoubleList doubleList) {
		return new DoublePerlinNoiseSampler(chunkRandom, i, doubleList);
	}

	private DoublePerlinNoiseSampler(ChunkRandom chunkRandom, int i, DoubleList doubleList) {
		this.firstSampler = OctavePerlinNoiseSampler.method_30847(chunkRandom, i, doubleList);
		this.secondSampler = OctavePerlinNoiseSampler.method_30847(chunkRandom, i, doubleList);
		int j = Integer.MAX_VALUE;
		int k = Integer.MIN_VALUE;
		DoubleListIterator doubleListIterator = doubleList.iterator();

		while (doubleListIterator.hasNext()) {
			int l = doubleListIterator.nextIndex();
			double d = doubleListIterator.nextDouble();
			if (d != 0.0) {
				j = Math.min(j, l);
				k = Math.max(k, l);
			}
		}

		this.amplitude = 0.16666666666666666 / createAmplitude(k - j);
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
