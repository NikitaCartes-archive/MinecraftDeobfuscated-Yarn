package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Box;

@Environment(EnvType.CLIENT)
public class Frustum {
	private final Vector4f[] homogeneousCoordinates = new Vector4f[6];
	private double x;
	private double y;
	private double z;

	public Frustum(Matrix4f matrix4f, Matrix4f matrix4f2) {
		this.init(matrix4f, matrix4f2);
	}

	public void setPosition(double d, double e, double f) {
		this.x = d;
		this.y = e;
		this.z = f;
	}

	private void init(Matrix4f matrix4f, Matrix4f matrix4f2) {
		Matrix4f matrix4f3 = new Matrix4f(matrix4f2);
		matrix4f3.multiply(matrix4f);
		matrix4f3.transpose();
		this.transform(matrix4f3, -1, 0, 0, 0);
		this.transform(matrix4f3, 1, 0, 0, 1);
		this.transform(matrix4f3, 0, -1, 0, 2);
		this.transform(matrix4f3, 0, 1, 0, 3);
		this.transform(matrix4f3, 0, 0, -1, 4);
		this.transform(matrix4f3, 0, 0, 1, 5);
	}

	private void transform(Matrix4f matrix4f, int i, int j, int k, int l) {
		Vector4f vector4f = new Vector4f((float)i, (float)j, (float)k, 1.0F);
		vector4f.multiply(matrix4f);
		vector4f.normalize();
		this.homogeneousCoordinates[l] = vector4f;
	}

	public boolean isVisible(Box box) {
		return this.isVisible(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
	}

	private boolean isVisible(double d, double e, double f, double g, double h, double i) {
		float j = (float)(d - this.x);
		float k = (float)(e - this.y);
		float l = (float)(f - this.z);
		float m = (float)(g - this.x);
		float n = (float)(h - this.y);
		float o = (float)(i - this.z);
		return this.isAnyCornerVisible(j, k, l, m, n, o);
	}

	private boolean isAnyCornerVisible(float f, float g, float h, float i, float j, float k) {
		for (int l = 0; l < 6; l++) {
			Vector4f vector4f = this.homogeneousCoordinates[l];
			if (!(vector4f.dotProduct(new Vector4f(f, g, h, 1.0F)) > 0.0F)
				&& !(vector4f.dotProduct(new Vector4f(i, g, h, 1.0F)) > 0.0F)
				&& !(vector4f.dotProduct(new Vector4f(f, j, h, 1.0F)) > 0.0F)
				&& !(vector4f.dotProduct(new Vector4f(i, j, h, 1.0F)) > 0.0F)
				&& !(vector4f.dotProduct(new Vector4f(f, g, k, 1.0F)) > 0.0F)
				&& !(vector4f.dotProduct(new Vector4f(i, g, k, 1.0F)) > 0.0F)
				&& !(vector4f.dotProduct(new Vector4f(f, j, k, 1.0F)) > 0.0F)
				&& !(vector4f.dotProduct(new Vector4f(i, j, k, 1.0F)) > 0.0F)) {
				return false;
			}
		}

		return true;
	}
}
