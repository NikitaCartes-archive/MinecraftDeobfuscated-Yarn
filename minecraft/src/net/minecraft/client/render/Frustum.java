package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Frustum {
	public final float[][] sides = new float[6][4];
	public final float[] field_4498 = new float[16];
	public final float[] field_4500 = new float[16];
	public final float[] field_4499 = new float[16];

	private double getDistanceFromPlane(float[] fs, double d, double e, double f) {
		return (double)fs[0] * d + (double)fs[1] * e + (double)fs[2] * f + (double)fs[3];
	}

	public boolean intersects(double d, double e, double f, double g, double h, double i) {
		for (int j = 0; j < 6; j++) {
			float[] fs = this.sides[j];
			if (!(this.getDistanceFromPlane(fs, d, e, f) > 0.0)
				&& !(this.getDistanceFromPlane(fs, g, e, f) > 0.0)
				&& !(this.getDistanceFromPlane(fs, d, h, f) > 0.0)
				&& !(this.getDistanceFromPlane(fs, g, h, f) > 0.0)
				&& !(this.getDistanceFromPlane(fs, d, e, i) > 0.0)
				&& !(this.getDistanceFromPlane(fs, g, e, i) > 0.0)
				&& !(this.getDistanceFromPlane(fs, d, h, i) > 0.0)
				&& !(this.getDistanceFromPlane(fs, g, h, i) > 0.0)) {
				return false;
			}
		}

		return true;
	}
}
