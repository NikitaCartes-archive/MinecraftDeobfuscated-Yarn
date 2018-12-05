package net.minecraft.sortme;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Quaternion;

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

	public float x() {
		return this.components[0];
	}

	public float y() {
		return this.components[1];
	}

	public float z() {
		return this.components[2];
	}

	public float w() {
		return this.components[3];
	}

	public void method_4954(Vector3f vector3f) {
		this.components[0] = this.components[0] * vector3f.x();
		this.components[1] = this.components[1] * vector3f.y();
		this.components[2] = this.components[2] * vector3f.z();
	}

	public void method_4955(float f, float g, float h, float i) {
		this.components[0] = f;
		this.components[1] = g;
		this.components[2] = h;
		this.components[3] = i;
	}

	public void method_4960(Matrix4f matrix4f) {
		float[] fs = Arrays.copyOf(this.components, 4);

		for (int i = 0; i < 4; i++) {
			this.components[i] = 0.0F;

			for (int j = 0; j < 4; j++) {
				this.components[i] = this.components[i] + matrix4f.method_4938(i, j) * fs[j];
			}
		}
	}

	public void method_4959(Quaternion quaternion) {
		Quaternion quaternion2 = new Quaternion(quaternion);
		quaternion2.method_4925(new Quaternion(this.x(), this.y(), this.z(), 0.0F));
		Quaternion quaternion3 = new Quaternion(quaternion);
		quaternion3.method_4926();
		quaternion2.method_4925(quaternion3);
		this.method_4955(quaternion2.method_4921(), quaternion2.method_4922(), quaternion2.method_4923(), this.w());
	}
}
