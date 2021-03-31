package net.minecraft.util.math;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.util.Util;

/**
 * A mutable vector composed of 3 floats.
 */
public final class Vec3f {
	public static final Codec<Vec3f> CODEC = Codec.FLOAT
		.listOf()
		.comapFlatMap(
			list -> Util.toArray(list, 3).map(listx -> new Vec3f((Float)listx.get(0), (Float)listx.get(1), (Float)listx.get(2))),
			vec3f -> ImmutableList.of(vec3f.x, vec3f.y, vec3f.z)
		);
	public static Vec3f NEGATIVE_X = new Vec3f(-1.0F, 0.0F, 0.0F);
	public static Vec3f POSITIVE_X = new Vec3f(1.0F, 0.0F, 0.0F);
	public static Vec3f NEGATIVE_Y = new Vec3f(0.0F, -1.0F, 0.0F);
	public static Vec3f POSITIVE_Y = new Vec3f(0.0F, 1.0F, 0.0F);
	public static Vec3f NEGATIVE_Z = new Vec3f(0.0F, 0.0F, -1.0F);
	public static Vec3f POSITIVE_Z = new Vec3f(0.0F, 0.0F, 1.0F);
	public static Vec3f field_29501 = new Vec3f(0.0F, 0.0F, 0.0F);
	private float x;
	private float y;
	private float z;

	public Vec3f() {
	}

	public Vec3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3f(Vector4f vector4f) {
		this(vector4f.getX(), vector4f.getY(), vector4f.getZ());
	}

	public Vec3f(Vec3d other) {
		this((float)other.x, (float)other.y, (float)other.z);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			Vec3f vec3f = (Vec3f)o;
			if (Float.compare(vec3f.x, this.x) != 0) {
				return false;
			} else {
				return Float.compare(vec3f.y, this.y) != 0 ? false : Float.compare(vec3f.z, this.z) == 0;
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

	public void scale(float scale) {
		this.x *= scale;
		this.y *= scale;
		this.z *= scale;
	}

	public void multiplyComponentwise(float x, float y, float z) {
		this.x *= x;
		this.y *= y;
		this.z *= z;
	}

	public void method_35921(Vec3f vec3f, Vec3f vec3f2) {
		this.x = MathHelper.clamp(this.x, vec3f.getX(), vec3f2.getX());
		this.y = MathHelper.clamp(this.y, vec3f.getX(), vec3f2.getY());
		this.z = MathHelper.clamp(this.z, vec3f.getZ(), vec3f2.getZ());
	}

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

	public void method_35920(Vec3f vec3f) {
		this.x = vec3f.x;
		this.y = vec3f.y;
		this.z = vec3f.z;
	}

	public void add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}

	public void add(Vec3f vector) {
		this.x = this.x + vector.x;
		this.y = this.y + vector.y;
		this.z = this.z + vector.z;
	}

	public void subtract(Vec3f other) {
		this.x = this.x - other.x;
		this.y = this.y - other.y;
		this.z = this.z - other.z;
	}

	public float dot(Vec3f other) {
		return this.x * other.x + this.y * other.y + this.z * other.z;
	}

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

	public void cross(Vec3f vector) {
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

	public void lerp(Vec3f vector, float delta) {
		float f = 1.0F - delta;
		this.x = this.x * f + vector.x * delta;
		this.y = this.y * f + vector.y * delta;
		this.z = this.z * f + vector.z * delta;
	}

	public Quaternion getRadialQuaternion(float angle) {
		return new Quaternion(this, angle, false);
	}

	public Quaternion getDegreesQuaternion(float angle) {
		return new Quaternion(this, angle, true);
	}

	public Vec3f copy() {
		return new Vec3f(this.x, this.y, this.z);
	}

	public void modify(Float2FloatFunction function) {
		this.x = function.get(this.x);
		this.y = function.get(this.y);
		this.z = function.get(this.z);
	}

	public String toString() {
		return "[" + this.x + ", " + this.y + ", " + this.z + "]";
	}
}
