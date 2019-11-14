package net.minecraft.client.util.math;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class Vector4f {
	private float x;
	private float y;
	private float z;
	private float w;

	public Vector4f() {
	}

	public Vector4f(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Vector4f(Vector3f vector) {
		this(vector.getX(), vector.getY(), vector.getZ(), 1.0F);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			Vector4f vector4f = (Vector4f)o;
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

	public float getW() {
		return this.w;
	}

	public void multiplyComponentwise(Vector3f vector) {
		this.x = this.x * vector.getX();
		this.y = this.y * vector.getY();
		this.z = this.z * vector.getZ();
	}

	public void set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public float dotProduct(Vector4f other) {
		return this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w;
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

	public void multiply(Matrix4f matrix) {
		float f = this.x;
		float g = this.y;
		float h = this.z;
		float i = this.w;
		this.x = calculateProductRow(0, matrix, f, g, h, i);
		this.y = calculateProductRow(1, matrix, f, g, h, i);
		this.z = calculateProductRow(2, matrix, f, g, h, i);
		this.w = calculateProductRow(3, matrix, f, g, h, i);
	}

	private static float calculateProductRow(int row, Matrix4f matrix, float x, float y, float z, float w) {
		return matrix.get(row, 0) * x + matrix.get(row, 1) * y + matrix.get(row, 2) * z + matrix.get(row, 3) * w;
	}

	public void setQuarternion(Quaternion quarternion) {
		Quaternion quaternion = new Quaternion(quarternion);
		quaternion.hamiltonProduct(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0F));
		Quaternion quaternion2 = new Quaternion(quarternion);
		quaternion2.conjugate();
		quaternion.hamiltonProduct(quaternion2);
		this.set(quaternion.getB(), quaternion.getC(), quaternion.getD(), this.getW());
	}

	public void normalizeProjectiveCoordinates() {
		this.x = this.x / this.w;
		this.y = this.y / this.w;
		this.z = this.z / this.w;
		this.w = 1.0F;
	}

	public String toString() {
		return "[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
	}
}
