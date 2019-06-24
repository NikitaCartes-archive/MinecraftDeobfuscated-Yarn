package net.minecraft.util.math;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;

public final class Quaternion {
	private final float[] components;

	public Quaternion() {
		this.components = new float[4];
		this.components[4] = 1.0F;
	}

	public Quaternion(float f, float g, float h, float i) {
		this.components = new float[4];
		this.components[0] = f;
		this.components[1] = g;
		this.components[2] = h;
		this.components[3] = i;
	}

	public Quaternion(Vector3f vector3f, float f, boolean bl) {
		if (bl) {
			f *= (float) (Math.PI / 180.0);
		}

		float g = sin(f / 2.0F);
		this.components = new float[4];
		this.components[0] = vector3f.x() * g;
		this.components[1] = vector3f.y() * g;
		this.components[2] = vector3f.z() * g;
		this.components[3] = cos(f / 2.0F);
	}

	@Environment(EnvType.CLIENT)
	public Quaternion(float f, float g, float h, boolean bl) {
		if (bl) {
			f *= (float) (Math.PI / 180.0);
			g *= (float) (Math.PI / 180.0);
			h *= (float) (Math.PI / 180.0);
		}

		float i = sin(0.5F * f);
		float j = cos(0.5F * f);
		float k = sin(0.5F * g);
		float l = cos(0.5F * g);
		float m = sin(0.5F * h);
		float n = cos(0.5F * h);
		this.components = new float[4];
		this.components[0] = i * l * n + j * k * m;
		this.components[1] = j * k * n - i * l * m;
		this.components[2] = i * k * n + j * l * m;
		this.components[3] = j * l * n - i * k * m;
	}

	public Quaternion(Quaternion quaternion) {
		this.components = Arrays.copyOf(quaternion.components, 4);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			Quaternion quaternion = (Quaternion)object;
			return Arrays.equals(this.components, quaternion.components);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Arrays.hashCode(this.components);
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Quaternion[").append(this.getW()).append(" + ");
		stringBuilder.append(this.getX()).append("i + ");
		stringBuilder.append(this.getY()).append("j + ");
		stringBuilder.append(this.getZ()).append("k]");
		return stringBuilder.toString();
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

	public void copyFrom(Quaternion quaternion) {
		float f = this.getX();
		float g = this.getY();
		float h = this.getZ();
		float i = this.getW();
		float j = quaternion.getX();
		float k = quaternion.getY();
		float l = quaternion.getZ();
		float m = quaternion.getW();
		this.components[0] = i * j + f * m + g * l - h * k;
		this.components[1] = i * k - f * l + g * m + h * j;
		this.components[2] = i * l + f * k - g * j + h * m;
		this.components[3] = i * m - f * j - g * k - h * l;
	}

	public void reverse() {
		this.components[0] = -this.components[0];
		this.components[1] = -this.components[1];
		this.components[2] = -this.components[2];
	}

	private static float cos(float f) {
		return (float)Math.cos((double)f);
	}

	private static float sin(float f) {
		return (float)Math.sin((double)f);
	}
}
