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

	public void setPosition(double cameraX, double cameraY, double cameraZ) {
		this.x = cameraX;
		this.y = cameraY;
		this.z = cameraZ;
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

	private void transform(Matrix4f function, int x, int y, int z, int index) {
		Vector4f vector4f = new Vector4f((float)x, (float)y, (float)z, 1.0F);
		vector4f.multiply(function);
		vector4f.normalize();
		this.homogeneousCoordinates[index] = vector4f;
	}

	public boolean isVisible(Box box) {
		return this.isVisible(box.x1, box.y1, box.z1, box.x2, box.y2, box.z2);
	}

	private boolean isVisible(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		float f = (float)(minX - this.x);
		float g = (float)(minY - this.y);
		float h = (float)(minZ - this.z);
		float i = (float)(maxX - this.x);
		float j = (float)(maxY - this.y);
		float k = (float)(maxZ - this.z);
		return this.isAnyCornerVisible(f, g, h, i, j, k);
	}

	private boolean isAnyCornerVisible(float x1, float y1, float z1, float x2, float y2, float z2) {
		for (int i = 0; i < 6; i++) {
			Vector4f vector4f = this.homogeneousCoordinates[i];
			if (!(vector4f.dotProduct(new Vector4f(x1, y1, z1, 1.0F)) > 0.0F)
				&& !(vector4f.dotProduct(new Vector4f(x2, y1, z1, 1.0F)) > 0.0F)
				&& !(vector4f.dotProduct(new Vector4f(x1, y2, z1, 1.0F)) > 0.0F)
				&& !(vector4f.dotProduct(new Vector4f(x2, y2, z1, 1.0F)) > 0.0F)
				&& !(vector4f.dotProduct(new Vector4f(x1, y1, z2, 1.0F)) > 0.0F)
				&& !(vector4f.dotProduct(new Vector4f(x2, y1, z2, 1.0F)) > 0.0F)
				&& !(vector4f.dotProduct(new Vector4f(x1, y2, z2, 1.0F)) > 0.0F)
				&& !(vector4f.dotProduct(new Vector4f(x2, y2, z2, 1.0F)) > 0.0F)) {
				return false;
			}
		}

		return true;
	}
}
