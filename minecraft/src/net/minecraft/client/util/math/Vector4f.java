package net.minecraft.client.util.math;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class Vector4f {
	private final float[] components;

	public Vector4f() {
		this.components = new float[4];
	}

	public Vector4f(float f, float g, float h, float i) {
		this.components = new float[]{f, g, h, i};
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

	public float getW() {
		return this.components[3];
	}

	public void multiply(Vector3f vector3f) {
		this.components[0] = this.components[0] * vector3f.getX();
		this.components[1] = this.components[1] * vector3f.getY();
		this.components[2] = this.components[2] * vector3f.getZ();
	}

	public void set(float f, float g, float h, float i) {
		this.components[0] = f;
		this.components[1] = g;
		this.components[2] = h;
		this.components[3] = i;
	}

	public void method_22674(Matrix4f matrix4f) {
		float[] fs = Arrays.copyOf(this.components, 4);

		for (int i = 0; i < 4; i++) {
			this.components[i] = 0.0F;

			for (int j = 0; j < 4; j++) {
				this.components[i] = this.components[i] + matrix4f.method_22669(i, j) * fs[j];
			}
		}
	}

	public void method_4959(Quaternion quaternion) {
		Quaternion quaternion2 = new Quaternion(quaternion);
		quaternion2.copyFrom(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0F));
		Quaternion quaternion3 = new Quaternion(quaternion);
		quaternion3.reverse();
		quaternion2.copyFrom(quaternion3);
		this.set(quaternion2.getX(), quaternion2.getY(), quaternion2.getZ(), this.getW());
	}
}
