package net.minecraft.client.util.math;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
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

	public Vector3f(float f, float g, float h) {
		this.x = f;
		this.y = g;
		this.z = h;
	}

	@Environment(EnvType.CLIENT)
	public Vector3f(Vector3f vector3f) {
		this(vector3f.x, vector3f.y, vector3f.z);
	}

	public Vector3f(Vec3d vec3d) {
		this((float)vec3d.x, (float)vec3d.y, (float)vec3d.z);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			Vector3f vector3f = (Vector3f)object;
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
	public void scale(float f) {
		this.x *= f;
		this.y *= f;
		this.z *= f;
	}

	@Environment(EnvType.CLIENT)
	private static float clampFloat(float f, float g, float h) {
		if (f < g) {
			return g;
		} else {
			return f > h ? h : f;
		}
	}

	@Environment(EnvType.CLIENT)
	public void clamp(float f, float g) {
		this.x = clampFloat(this.x, f, g);
		this.y = clampFloat(this.y, f, g);
		this.z = clampFloat(this.z, f, g);
	}

	public void set(float f, float g, float h) {
		this.x = f;
		this.y = g;
		this.z = h;
	}

	@Environment(EnvType.CLIENT)
	public void add(float f, float g, float h) {
		this.x += f;
		this.y += g;
		this.z += h;
	}

	@Environment(EnvType.CLIENT)
	public void subtract(Vector3f vector3f) {
		this.x = this.x - vector3f.x;
		this.y = this.y - vector3f.y;
		this.z = this.z - vector3f.z;
	}

	@Environment(EnvType.CLIENT)
	public float dot(Vector3f vector3f) {
		return this.x * vector3f.x + this.y * vector3f.y + this.z * vector3f.z;
	}

	@Environment(EnvType.CLIENT)
	public boolean reciprocal() {
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
	public void cross(Vector3f vector3f) {
		float f = this.x;
		float g = this.y;
		float h = this.z;
		float i = vector3f.getX();
		float j = vector3f.getY();
		float k = vector3f.getZ();
		this.x = g * k - h * j;
		this.y = h * i - f * k;
		this.z = f * j - g * i;
	}

	@Environment(EnvType.CLIENT)
	public void multiply(Matrix3f matrix3f) {
		float f = this.x;
		float g = this.y;
		float h = this.z;
		this.x = method_23691(0, matrix3f, f, g, h);
		this.y = method_23691(1, matrix3f, f, g, h);
		this.z = method_23691(2, matrix3f, f, g, h);
	}

	@Environment(EnvType.CLIENT)
	private static float method_23691(int i, Matrix3f matrix3f, float f, float g, float h) {
		return matrix3f.get(i, 0) * f + matrix3f.get(i, 1) * g + matrix3f.get(i, 2) * h;
	}

	public void method_19262(Quaternion quaternion) {
		Quaternion quaternion2 = new Quaternion(quaternion);
		quaternion2.hamiltonProduct(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0F));
		Quaternion quaternion3 = new Quaternion(quaternion);
		quaternion3.conjugate();
		quaternion2.hamiltonProduct(quaternion3);
		this.set(quaternion2.getB(), quaternion2.getC(), quaternion2.getD());
	}

	@Environment(EnvType.CLIENT)
	public Quaternion method_23626(float f) {
		return new Quaternion(this, f, false);
	}

	@Environment(EnvType.CLIENT)
	public Quaternion getRotationQuaternion(float f) {
		return new Quaternion(this, f, true);
	}
}
