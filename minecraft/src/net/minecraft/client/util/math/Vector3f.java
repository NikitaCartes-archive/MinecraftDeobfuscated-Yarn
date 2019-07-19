package net.minecraft.client.util.math;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public final class Vector3f {
	private final float[] components;

	@Environment(EnvType.CLIENT)
	public Vector3f(Vector3f vector3f) {
		this.components = Arrays.copyOf(vector3f.components, 3);
	}

	public Vector3f() {
		this.components = new float[3];
	}

	@Environment(EnvType.CLIENT)
	public Vector3f(float x, float y, float z) {
		this.components = new float[]{x, y, z};
	}

	public Vector3f(Vec3d vec3d) {
		this.components = new float[]{(float)vec3d.x, (float)vec3d.y, (float)vec3d.z};
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			Vector3f vector3f = (Vector3f)o;
			return Arrays.equals(this.components, vector3f.components);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Arrays.hashCode(this.components);
	}

	public float getX() {
		return this.components[0];
	}

	public float getY() {
		return this.components[1];
	}

	public float getZ() {
		return this.components[2];
	}

	@Environment(EnvType.CLIENT)
	public void scale(float scale) {
		for (int i = 0; i < 3; i++) {
			this.components[i] = this.components[i] * scale;
		}
	}

	@Environment(EnvType.CLIENT)
	private static float clampFloat(float v, float min, float max) {
		if (v < min) {
			return min;
		} else {
			return v > max ? max : v;
		}
	}

	@Environment(EnvType.CLIENT)
	public void clamp(float min, float max) {
		this.components[0] = clampFloat(this.components[0], min, max);
		this.components[1] = clampFloat(this.components[1], min, max);
		this.components[2] = clampFloat(this.components[2], min, max);
	}

	public void set(float x, float y, float z) {
		this.components[0] = x;
		this.components[1] = y;
		this.components[2] = z;
	}

	@Environment(EnvType.CLIENT)
	public void add(float x, float y, float z) {
		this.components[0] = this.components[0] + x;
		this.components[1] = this.components[1] + y;
		this.components[2] = this.components[2] + z;
	}

	@Environment(EnvType.CLIENT)
	public void subtract(Vector3f other) {
		for (int i = 0; i < 3; i++) {
			this.components[i] = this.components[i] - other.components[i];
		}
	}

	@Environment(EnvType.CLIENT)
	public float dot(Vector3f other) {
		float f = 0.0F;

		for (int i = 0; i < 3; i++) {
			f += this.components[i] * other.components[i];
		}

		return f;
	}

	@Environment(EnvType.CLIENT)
	public void reciprocal() {
		float f = 0.0F;

		for (int i = 0; i < 3; i++) {
			f += this.components[i] * this.components[i];
		}

		for (int i = 0; i < 3; i++) {
			this.components[i] = this.components[i] / f;
		}
	}

	@Environment(EnvType.CLIENT)
	public void cross(Vector3f vector) {
		float f = this.components[0];
		float g = this.components[1];
		float h = this.components[2];
		float i = vector.getX();
		float j = vector.getY();
		float k = vector.getZ();
		this.components[0] = g * k - h * j;
		this.components[1] = h * i - f * k;
		this.components[2] = f * j - g * i;
	}

	public void method_19262(Quaternion quaternion) {
		Quaternion quaternion2 = new Quaternion(quaternion);
		quaternion2.hamiltonProduct(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0F));
		Quaternion quaternion3 = new Quaternion(quaternion);
		quaternion3.conjugate();
		quaternion2.hamiltonProduct(quaternion3);
		this.set(quaternion2.getB(), quaternion2.getC(), quaternion2.getD());
	}
}
