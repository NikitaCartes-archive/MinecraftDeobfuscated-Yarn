package net.minecraft.client.util.math;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class Vector4f {
	private float x;
	private float y;
	private float z;
	private float w;

	public Vector4f() {
	}

	public Vector4f(float f, float g, float h, float i) {
		this.x = f;
		this.y = g;
		this.z = h;
		this.w = i;
	}

	public Vector4f(Vector3f vector3f) {
		this(vector3f.getX(), vector3f.getY(), vector3f.getZ(), 1.0F);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			Vector4f vector4f = (Vector4f)object;
			if (Float.compare(vector4f.x, this.x) != 0) {
				return false;
			} else if (Float.compare(vector4f.y, this.y) != 0) {
				return false;
			} else {
				return Float.compare(vector4f.z, this.z) != 0 ? false : Float.compare(vector4f.w, this.w) == 0;
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = Float.floatToIntBits(this.x);
		i = 31 * i + Float.floatToIntBits(this.y);
		i = 31 * i + Float.floatToIntBits(this.z);
		return 31 * i + Float.floatToIntBits(this.w);
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getZ() {
		return this.z;
	}

	public void multiplyXyz(Vector3f vector3f) {
		this.x = this.x * vector3f.getX();
		this.y = this.y * vector3f.getY();
		this.z = this.z * vector3f.getZ();
	}

	public float dotProduct(Vector4f vector4f) {
		return this.x * vector4f.x + this.y * vector4f.y + this.z * vector4f.z + this.w * vector4f.w;
	}

	public boolean normalize() {
		float f = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
		if ((double)f < 1.0E-5) {
			return false;
		} else {
			float g = MathHelper.fastInverseSqrt(f);
			this.x *= g;
			this.y *= g;
			this.z *= g;
			this.w *= g;
			return true;
		}
	}

	public void multiply(Matrix4f matrix4f) {
		float f = this.x;
		float g = this.y;
		float h = this.z;
		float i = this.w;
		this.x = calculateProductRow(0, matrix4f, f, g, h, i);
		this.y = calculateProductRow(1, matrix4f, f, g, h, i);
		this.z = calculateProductRow(2, matrix4f, f, g, h, i);
		this.w = calculateProductRow(3, matrix4f, f, g, h, i);
	}

	private static float calculateProductRow(int i, Matrix4f matrix4f, float f, float g, float h, float j) {
		return matrix4f.get(i, 0) * f + matrix4f.get(i, 1) * g + matrix4f.get(i, 2) * h + matrix4f.get(i, 3) * j;
	}

	public void normalizeProjectiveCoordinates() {
		this.x = this.x / this.w;
		this.y = this.y / this.w;
		this.z = this.z / this.w;
		this.w = 1.0F;
	}
}
