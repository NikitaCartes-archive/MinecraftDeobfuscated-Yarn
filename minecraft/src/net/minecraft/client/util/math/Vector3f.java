package net.minecraft.client.util.math;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public final class Vector3f {
	public static Vector3f NEGATIVE_X = new Vector3f(-1.0F, 0.0F, 0.0F);
	public static Vector3f POSITIVE_X = new Vector3f(1.0F, 0.0F, 0.0F);
	public static Vector3f NEGATIVE_Y = new Vector3f(0.0F, -1.0F, 0.0F);
	public static Vector3f POSITIVE_Y = new Vector3f(0.0F, 1.0F, 0.0F);
	public static Vector3f NEGATIVE_Z = new Vector3f(0.0F, 0.0F, -1.0F);
	public static Vector3f POSITIVE_Z = new Vector3f(0.0F, 0.0F, 1.0F);
	private float x;
	private float y;
	private float z;

	public Vector3f() {
	}

	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3f(Vec3d other) {
		this((float)other.x, (float)other.y, (float)other.z);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			Vector3f vector3f = (Vector3f)o;
			if (Float.compare(vector3f.x, this.x) != 0) {
				return false;
			} else {
				return Float.compare(vector3f.y, this.y) != 0 ? false : Float.compare(vector3f.z, this.z) == 0;
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = Float.floatToIntBits(this.x);
		i = 31 * i + Float.floatToIntBits(this.y);
		return 31 * i + Float.floatToIntBits(this.z);
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

	@Environment(EnvType.CLIENT)
	public void scale(float scale) {
		this.x *= scale;
		this.y *= scale;
		this.z *= scale;
	}

	@Environment(EnvType.CLIENT)
	public void multiplyComponentwise(float x, float y, float z) {
		this.x *= x;
		this.y *= y;
		this.z *= z;
	}

	@Environment(EnvType.CLIENT)
	public void clamp(float min, float max) {
		this.x = MathHelper.clamp(this.x, min, max);
		this.y = MathHelper.clamp(this.y, min, max);
		this.z = MathHelper.clamp(this.z, min, max);
	}

	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Environment(EnvType.CLIENT)
	public void add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}

	@Environment(EnvType.CLIENT)
	public void add(Vector3f vector) {
		this.x = this.x + vector.x;
		this.y = this.y + vector.y;
		this.z = this.z + vector.z;
	}

	@Environment(EnvType.CLIENT)
	public void subtract(Vector3f other) {
		this.x = this.x - other.x;
		this.y = this.y - other.y;
		this.z = this.z - other.z;
	}

	@Environment(EnvType.CLIENT)
	public float dot(Vector3f other) {
		return this.x * other.x + this.y * other.y + this.z * other.z;
	}

	@Environment(EnvType.CLIENT)
	public boolean normalize() {
		float f = this.x * this.x + this.y * this.y + this.z * this.z;
		if ((double)f < 1.0E-5) {
			return false;
		} else {
			float g = MathHelper.fastInverseSqrt(f);
			this.x *= g;
			this.y *= g;
			this.z *= g;
			return true;
		}
	}

	@Environment(EnvType.CLIENT)
	public void cross(Vector3f vector) {
		float f = this.x;
		float g = this.y;
		float h = this.z;
		float i = vector.getX();
		float j = vector.getY();
		float k = vector.getZ();
		this.x = g * k - h * j;
		this.y = h * i - f * k;
		this.z = f * j - g * i;
	}

	@Environment(EnvType.CLIENT)
	public void transform(Matrix3f matrix3f) {
		float f = this.x;
		float g = this.y;
		float h = this.z;
		this.x = matrix3f.a00 * f + matrix3f.a01 * g + matrix3f.a02 * h;
		this.y = matrix3f.a10 * f + matrix3f.a11 * g + matrix3f.a12 * h;
		this.z = matrix3f.a20 * f + matrix3f.a21 * g + matrix3f.a22 * h;
	}

	public void rotate(Quaternion rotation) {
		Quaternion quaternion = new Quaternion(rotation);
		quaternion.hamiltonProduct(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0F));
		Quaternion quaternion2 = new Quaternion(rotation);
		quaternion2.conjugate();
		quaternion.hamiltonProduct(quaternion2);
		this.set(quaternion.getX(), quaternion.getY(), quaternion.getZ());
	}

	@Environment(EnvType.CLIENT)
	public void lerp(Vector3f vector, float delta) {
		float f = 1.0F - delta;
		this.x = this.x * f + vector.x * delta;
		this.y = this.y * f + vector.y * delta;
		this.z = this.z * f + vector.z * delta;
	}

	@Environment(EnvType.CLIENT)
	public Quaternion getRadialQuaternion(float angle) {
		return new Quaternion(this, angle, false);
	}

	@Environment(EnvType.CLIENT)
	public Quaternion getDegreesQuaternion(float angle) {
		return new Quaternion(this, angle, true);
	}

	@Environment(EnvType.CLIENT)
	public Vector3f copy() {
		return new Vector3f(this.x, this.y, this.z);
	}

	@Environment(EnvType.CLIENT)
	public void modify(Float2FloatFunction function) {
		this.x = function.get(this.x);
		this.y = function.get(this.y);
		this.z = function.get(this.z);
	}

	public String toString() {
		return "[" + this.x + ", " + this.y + ", " + this.z + "]";
	}
}
