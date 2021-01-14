package net.minecraft.util.math.noise;

public interface NoiseSampler {
	double sample(double x, double y, double yScale, double yMax);
}
