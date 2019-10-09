package net.minecraft.client.util.math;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class Vector4f {
	private final float[] components;

	public Vector4f() {
		this.components = new float[4];
	}

	public Vector4f(float f, float g, float h, float i) {
		this.components = new float[]{f, g, h, i};
	}

	public Vector4f(Vector3f vector3f) {
		this.components = new float[]{vector3f.getX(), vector3f.getY(), vector3f.getZ(), 1.0F};
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			Vector4f vector4f = (Vector4f)object;
			return Arrays.equals(this.components, vector4f.components);
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

	public void multiply(Vector3f vector3f) {
		this.components[0] = this.components[0] * vector3f.getX();
		this.components[1] = this.components[1] * vector3f.getY();
		this.components[2] = this.components[2] * vector3f.getZ();
	}

	public float method_23217(Vector4f vector4f) {
		float f = 0.0F;

		for (int i = 0; i < 4; i++) {
			f += this.components[i] * vector4f.components[i];
		}

		return f;
	}

	public boolean method_23218() {
		float f = 0.0F;

		for (int i = 0; i < 4; i++) {
			f += this.components[i] * this.components[i];
		}

		if ((double)f < 1.0E-5) {
			return false;
		} else {
			float g = MathHelper.fastInverseSqrt(f);

			for (int j = 0; j < 4; j++) {
				this.components[j] = this.components[j] * g;
			}

			return true;
		}
	}

	public void multiply(Matrix4f matrix4f) {
		float f = this.components[0];
		float g = this.components[1];
		float h = this.components[2];
		float i = this.components[3];

		for (int j = 0; j < 4; j++) {
			float k = 0.0F;
			k += matrix4f.get(j, 0) * f;
			k += matrix4f.get(j, 1) * g;
			k += matrix4f.get(j, 2) * h;
			k += matrix4f.get(j, 3) * i;
			this.components[j] = k;
		}
	}

	public void method_23219() {
		this.components[0] = this.components[0] / this.components[3];
		this.components[1] = this.components[1] / this.components[3];
		this.components[2] = this.components[2] / this.components[3];
		this.components[3] = 1.0F;
	}
}
