package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vector4f;

@Environment(EnvType.CLIENT)
public class Frustum {
	public static final int field_34820 = 4;
	private final Vector4f[] homogeneousCoordinates = new Vector4f[6];
	private Vector4f field_34821;
	private double x;
	private double y;
	private double z;

	public Frustum(Matrix4f matrix4f, Matrix4f matrix4f2) {
		this.init(matrix4f, matrix4f2);
	}

	public Frustum(Frustum frustum) {
		System.arraycopy(frustum.homogeneousCoordinates, 0, this.homogeneousCoordinates, 0, frustum.homogeneousCoordinates.length);
		this.x = frustum.x;
		this.y = frustum.y;
		this.z = frustum.z;
		this.field_34821 = frustum.field_34821;
	}

	public Frustum method_38557(int i) {
		double d = Math.floor(this.x / (double)i) * (double)i;
		double e = Math.floor(this.y / (double)i) * (double)i;
		double f = Math.floor(this.z / (double)i) * (double)i;
		double g = Math.ceil(this.x / (double)i) * (double)i;
		double h = Math.ceil(this.y / (double)i) * (double)i;

		for (double j = Math.ceil(this.z / (double)i) * (double)i;
			!this.method_38558((float)(d - this.x), (float)(e - this.y), (float)(f - this.z), (float)(g - this.x), (float)(h - this.y), (float)(j - this.z));
			this.z = this.z - (double)(this.field_34821.getZ() * 4.0F)
		) {
			this.x = this.x - (double)(this.field_34821.getX() * 4.0F);
			this.y = this.y - (double)(this.field_34821.getY() * 4.0F);
		}

		return this;
	}

	public void setPosition(double cameraX, double cameraY, double cameraZ) {
		this.x = cameraX;
		this.y = cameraY;
		this.z = cameraZ;
	}

	private void init(Matrix4f matrix4f, Matrix4f matrix4f2) {
		Matrix4f matrix4f3 = matrix4f2.copy();
		matrix4f3.multiply(matrix4f);
		matrix4f3.transpose();
		this.field_34821 = new Vector4f(0.0F, 0.0F, 1.0F, 0.0F);
		this.field_34821.transform(matrix4f3);
		this.transform(matrix4f3, -1, 0, 0, 0);
		this.transform(matrix4f3, 1, 0, 0, 1);
		this.transform(matrix4f3, 0, -1, 0, 2);
		this.transform(matrix4f3, 0, 1, 0, 3);
		this.transform(matrix4f3, 0, 0, -1, 4);
		this.transform(matrix4f3, 0, 0, 1, 5);
	}

	private void transform(Matrix4f function, int x, int y, int z, int index) {
		Vector4f vector4f = new Vector4f((float)x, (float)y, (float)z, 1.0F);
		vector4f.transform(function);
		vector4f.normalize();
		this.homogeneousCoordinates[index] = vector4f;
	}

	public boolean isVisible(Box box) {
		return this.isVisible(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
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

	private boolean method_38558(float f, float g, float h, float i, float j, float k) {
		for (int l = 0; l < 6; l++) {
			Vector4f vector4f = this.homogeneousCoordinates[l];
			if (vector4f.dotProduct(new Vector4f(f, g, h, 1.0F)) <= 0.0F) {
				return false;
			}

			if (vector4f.dotProduct(new Vector4f(i, g, h, 1.0F)) <= 0.0F) {
				return false;
			}

			if (vector4f.dotProduct(new Vector4f(f, j, h, 1.0F)) <= 0.0F) {
				return false;
			}

			if (vector4f.dotProduct(new Vector4f(i, j, h, 1.0F)) <= 0.0F) {
				return false;
			}

			if (vector4f.dotProduct(new Vector4f(f, g, k, 1.0F)) <= 0.0F) {
				return false;
			}

			if (vector4f.dotProduct(new Vector4f(i, g, k, 1.0F)) <= 0.0F) {
				return false;
			}

			if (vector4f.dotProduct(new Vector4f(f, j, k, 1.0F)) <= 0.0F) {
				return false;
			}

			if (vector4f.dotProduct(new Vector4f(i, j, k, 1.0F)) <= 0.0F) {
				return false;
			}
		}

		return true;
	}
}
