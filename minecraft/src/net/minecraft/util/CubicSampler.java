package net.minecraft.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CubicSampler {
	private static final int FIRST_SEGMENT_OFFSET = 2;
	private static final int NUM_SEGMENTS = 6;
	private static final double[] DENSITY_CURVE = new double[]{0.0, 1.0, 4.0, 6.0, 4.0, 1.0, 0.0};

	private CubicSampler() {
	}

	public static Vec3d sampleColor(Vec3d pos, CubicSampler.RgbFetcher rgbFetcher) {
		int i = MathHelper.floor(pos.getX());
		int j = MathHelper.floor(pos.getY());
		int k = MathHelper.floor(pos.getZ());
		double d = pos.getX() - (double)i;
		double e = pos.getY() - (double)j;
		double f = pos.getZ() - (double)k;
		double g = 0.0;
		Vec3d vec3d = Vec3d.ZERO;

		for (int l = 0; l < 6; l++) {
			double h = MathHelper.lerp(d, DENSITY_CURVE[l + 1], DENSITY_CURVE[l]);
			int m = i - 2 + l;

			for (int n = 0; n < 6; n++) {
				double o = MathHelper.lerp(e, DENSITY_CURVE[n + 1], DENSITY_CURVE[n]);
				int p = j - 2 + n;

				for (int q = 0; q < 6; q++) {
					double r = MathHelper.lerp(f, DENSITY_CURVE[q + 1], DENSITY_CURVE[q]);
					int s = k - 2 + q;
					double t = h * o * r;
					g += t;
					vec3d = vec3d.add(rgbFetcher.fetch(m, p, s).multiply(t));
				}
			}
		}

		return vec3d.multiply(1.0 / g);
	}

	@FunctionalInterface
	public interface RgbFetcher {
		Vec3d fetch(int x, int y, int z);
	}
}
