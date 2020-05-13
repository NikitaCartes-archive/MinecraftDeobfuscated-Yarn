package net.minecraft.util.math.noise;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.world.gen.ChunkRandom;

public class DoublePerlinNoiseSampler {
	private final double amplitude;
	private final OctavePerlinNoiseSampler firstSampler;
	private final OctavePerlinNoiseSampler secondSampler;

	public DoublePerlinNoiseSampler(ChunkRandom chunkRandom, IntStream intStream) {
		this(chunkRandom, (List<Integer>)intStream.boxed().collect(ImmutableList.toImmutableList()));
	}

	public DoublePerlinNoiseSampler(ChunkRandom chunkRandom, List<Integer> octaves) {
		this.firstSampler = new OctavePerlinNoiseSampler(chunkRandom, octaves);
		this.secondSampler = new OctavePerlinNoiseSampler(chunkRandom, octaves);
		int i = (Integer)octaves.stream().min(Integer::compareTo).orElse(0);
		int j = (Integer)octaves.stream().max(Integer::compareTo).orElse(0);
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
