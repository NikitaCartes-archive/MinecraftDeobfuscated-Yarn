package net.minecraft.world.gen.chunk;

@FunctionalInterface
public interface WeightSampler {
	WeightSampler DEFAULT = (d, i, j, k) -> d;

	double sample(double weight, int x, int y, int z);
}
