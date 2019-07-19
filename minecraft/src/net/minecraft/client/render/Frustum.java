package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Frustum {
	public final float[][] sides = new float[6][4];
	public final float[] projectionMatrix = new float[16];
	public final float[] modelViewMatrix = new float[16];
	public final float[] mvpMatrix = new float[16];

	private double getDistanceFromPlane(float[] plane, double x, double y, double z) {
		return (double)plane[0] * x + (double)plane[1] * y + (double)plane[2] * z + (double)plane[3];
	}

	public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		for (int i = 0; i < 6; i++) {
			float[] fs = this.sides[i];
			if (!(this.getDistanceFromPlane(fs, minX, minY, minZ) > 0.0)
				&& !(this.getDistanceFromPlane(fs, maxX, minY, minZ) > 0.0)
				&& !(this.getDistanceFromPlane(fs, minX, maxY, minZ) > 0.0)
				&& !(this.getDistanceFromPlane(fs, maxX, maxY, minZ) > 0.0)
				&& !(this.getDistanceFromPlane(fs, minX, minY, maxZ) > 0.0)
				&& !(this.getDistanceFromPlane(fs, maxX, minY, maxZ) > 0.0)
				&& !(this.getDistanceFromPlane(fs, minX, maxY, maxZ) > 0.0)
				&& !(this.getDistanceFromPlane(fs, maxX, maxY, maxZ) > 0.0)) {
				return false;
			}
		}

		return true;
	}
}
