package net.minecraft.world.gen;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;

public class NoiseHelper {
	public static double lerpFromProgress(DoublePerlinNoiseSampler sampler, double x, double y, double z, double start, double end) {
		double d = sampler.sample(x, y, z);
		return MathHelper.lerpFromProgress(d, -1.0, 1.0, start, end);
	}
}
