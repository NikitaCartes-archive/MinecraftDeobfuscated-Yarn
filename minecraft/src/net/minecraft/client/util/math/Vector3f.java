package net.minecraft.client.util.math;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class Vector3f {
	private final float[] components;

	public Vector3f(Vector3f vector3f) {
		this.components = Arrays.copyOf(vector3f.components, 3);
	}

	public Vector3f() {
		this.components = new float[3];
	}

	public Vector3f(float f, float g, float h) {
		this.components = new float[]{f, g, h};
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			Vector3f vector3f = (Vector3f)object;
			return Arrays.equals(this.components, vector3f.components);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Arrays.hashCode(this.components);
	}

	public float x() {
		return this.components[0];
	}

	public float y() {
		return this.components[1];
	}

	public float z() {
		return this.components[2];
	}

	public void scale(float f) {
		for (int i = 0; i < 3; i++) {
			this.components[i] = this.components[i] * f;
		}
	}

	private static float clampFloat(float f, float g, float h) {
		if (f < g) {
			return g;
		} else {
			return f > h ? h : f;
		}
	}

	public void clamp(float f, float g) {
		this.components[0] = clampFloat(this.components[0], f, g);
		this.components[1] = clampFloat(this.components[1], f, g);
		this.components[2] = clampFloat(this.components[2], f, g);
	}

	public void set(float f, float g, float h) {
		this.components[0] = f;
		this.components[1] = g;
		this.components[2] = h;
	}

	public void add(float f, float g, float h) {
		this.components[0] = this.components[0] + f;
		this.components[1] = this.components[1] + g;
		this.components[2] = this.components[2] + h;
	}

	public void subtract(Vector3f vector3f) {
		for (int i = 0; i < 3; i++) {
			this.components[i] = this.components[i] - vector3f.components[i];
		}
	}

	public float dot(Vector3f vector3f) {
		float f = 0.0F;

		for (int i = 0; i < 3; i++) {
			f += this.components[i] * vector3f.components[i];
		}

		return f;
	}

	public void reciprocal() {
		float f = 0.0F;

		for (int i = 0; i < 3; i++) {
			f += this.components[i] * this.components[i];
		}

		for (int i = 0; i < 3; i++) {
			this.components[i] = this.components[i] / f;
		}
	}

	public void cross(Vector3f vector3f) {
		float f = this.components[0];
		float g = this.components[1];
		float h = this.components[2];
		float i = vector3f.x();
		float j = vector3f.y();
		float k = vector3f.z();
		this.components[0] = g * k - h * j;
		this.components[1] = h * i - f * k;
		this.components[2] = f * j - g * i;
	}
}
