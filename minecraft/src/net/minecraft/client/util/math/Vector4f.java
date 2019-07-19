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

	public Vector4f(float x, float y, float z, float w) {
		this.components = new float[]{x, y, z, w};
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			Vector4f vector4f = (Vector4f)o;
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

	public void multiplyComponentwise(Vector3f vector) {
		this.components[0] = this.components[0] * vector.getX();
		this.components[1] = this.components[1] * vector.getY();
		this.components[2] = this.components[2] * vector.getZ();
	}

	public void set(float x, float y, float z, float w) {
		this.components[0] = x;
		this.components[1] = y;
		this.components[2] = z;
		this.components[3] = w;
	}

	public void method_4959(Quaternion quaternion) {
		Quaternion quaternion2 = new Quaternion(quaternion);
		quaternion2.hamiltonProduct(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0F));
		Quaternion quaternion3 = new Quaternion(quaternion);
		quaternion3.conjugate();
		quaternion2.hamiltonProduct(quaternion3);
		this.set(quaternion2.getB(), quaternion2.getC(), quaternion2.getD(), this.getW());
	}
}
