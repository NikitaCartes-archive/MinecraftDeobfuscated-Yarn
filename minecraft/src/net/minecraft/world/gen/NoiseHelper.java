package net.minecraft.world.gen;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;

public class NoiseHelper {
	public static double lerpFromProgress(DoublePerlinNoiseSampler sampler, double x, double y, double z, double start, double end) {
		double d = sampler.sample(x, y, z);
		return MathHelper.lerpFromProgress(d, -1.0, 1.0, start, end);
	}

	public static double method_35479(double d, double e) {
		return d + Math.sin(Math.PI * d) * e / Math.PI;
	}

	public static void method_39119(StringBuilder stringBuilder, double d, double e, double f, byte[] bs) {
		stringBuilder.append(String.format("xo=%.3f, yo=%.3f, zo=%.3f, p0=%d, p255=%d", (float)d, (float)e, (float)f, bs[0], bs[255]));
	}

	public static void method_39120(StringBuilder stringBuilder, double d, double e, double f, int[] is) {
		stringBuilder.append(String.format("xo=%.3f, yo=%.3f, zo=%.3f, p0=%d, p255=%d", (float)d, (float)e, (float)f, is[0], is[255]));
	}
}
