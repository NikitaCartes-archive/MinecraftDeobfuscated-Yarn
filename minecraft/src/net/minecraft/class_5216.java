package net.minecraft;

import java.util.List;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.gen.ChunkRandom;

public class class_5216 {
	private final double field_24175;
	private final OctavePerlinNoiseSampler field_24176;
	private final OctavePerlinNoiseSampler field_24177;

	public class_5216(ChunkRandom chunkRandom, List<Integer> list) {
		this.field_24176 = new OctavePerlinNoiseSampler(chunkRandom, list);
		this.field_24177 = new OctavePerlinNoiseSampler(chunkRandom, list);
		int i = (Integer)list.stream().min(Integer::compareTo).orElse(0);
		int j = (Integer)list.stream().max(Integer::compareTo).orElse(0);
		this.field_24175 = 0.16666666666666666 / method_27407(j - i);
	}

	private static double method_27407(int i) {
		return 0.1 * (1.0 + 1.0 / (double)(i + 1));
	}

	public double method_27406(double d, double e, double f) {
		double g = d * 1.0181268882175227;
		double h = e * 1.0181268882175227;
		double i = f * 1.0181268882175227;
		return (this.field_24176.sample(d, e, f) + this.field_24177.sample(g, h, i)) * this.field_24175;
	}
}
