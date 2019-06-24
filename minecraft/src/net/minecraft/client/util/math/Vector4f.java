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

	public void multiply(Vector3f vector3f) {
		this.components[0] = this.components[0] * vector3f.x();
		this.components[1] = this.components[1] * vector3f.y();
		this.components[2] = this.components[2] * vector3f.z();
	}

	public void set(float f, float g, float h, float i) {
		this.components[0] = f;
		this.components[1] = g;
		this.components[2] = h;
		this.components[3] = i;
	}

	public void method_4959(Quaternion quaternion) {
		Quaternion quaternion2 = new Quaternion(quaternion);
		quaternion2.copyFrom(new Quaternion(this.x(), this.y(), this.z(), 0.0F));
		Quaternion quaternion3 = new Quaternion(quaternion);
		quaternion3.reverse();
		quaternion2.copyFrom(quaternion3);
		this.set(quaternion2.getX(), quaternion2.getY(), quaternion2.getZ(), this.w());
	}
}
