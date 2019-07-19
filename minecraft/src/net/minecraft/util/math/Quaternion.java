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

	public Quaternion(float b, float c, float d, float a) {
		this.components = new float[4];
		this.components[0] = b;
		this.components[1] = c;
		this.components[2] = d;
		this.components[3] = a;
	}

	public Quaternion(Vector3f axis, float rotationAngle, boolean degrees) {
		if (degrees) {
			rotationAngle *= (float) (Math.PI / 180.0);
		}

		float f = sin(rotationAngle / 2.0F);
		this.components = new float[4];
		this.components[0] = axis.getX() * f;
		this.components[1] = axis.getY() * f;
		this.components[2] = axis.getZ() * f;
		this.components[3] = cos(rotationAngle / 2.0F);
	}

	@Environment(EnvType.CLIENT)
	public Quaternion(float x, float y, float z, boolean degrees) {
		if (degrees) {
			x *= (float) (Math.PI / 180.0);
			y *= (float) (Math.PI / 180.0);
			z *= (float) (Math.PI / 180.0);
		}

		float f = sin(0.5F * x);
		float g = cos(0.5F * x);
		float h = sin(0.5F * y);
		float i = cos(0.5F * y);
		float j = sin(0.5F * z);
		float k = cos(0.5F * z);
		this.components = new float[4];
		this.components[0] = f * i * k + g * h * j;
		this.components[1] = g * h * k - f * i * j;
		this.components[2] = f * h * k + g * i * j;
		this.components[3] = g * i * k - f * h * j;
	}

	public Quaternion(Quaternion other) {
		this.components = Arrays.copyOf(other.components, 4);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			Quaternion quaternion = (Quaternion)o;
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
		stringBuilder.append("Quaternion[").append(this.getA()).append(" + ");
		stringBuilder.append(this.getB()).append("i + ");
		stringBuilder.append(this.getC()).append("j + ");
		stringBuilder.append(this.getD()).append("k]");
		return stringBuilder.toString();
	}

	public float getB() {
		return this.components[0];
	}

	public float getC() {
		return this.components[1];
	}

	public float getD() {
		return this.components[2];
	}

	public float getA() {
		return this.components[3];
	}

	public void hamiltonProduct(Quaternion other) {
		float f = this.getB();
		float g = this.getC();
		float h = this.getD();
		float i = this.getA();
		float j = other.getB();
		float k = other.getC();
		float l = other.getD();
		float m = other.getA();
		this.components[0] = i * j + f * m + g * l - h * k;
		this.components[1] = i * k - f * l + g * m + h * j;
		this.components[2] = i * l + f * k - g * j + h * m;
		this.components[3] = i * m - f * j - g * k - h * l;
	}

	public void conjugate() {
		this.components[0] = -this.components[0];
		this.components[1] = -this.components[1];
		this.components[2] = -this.components[2];
	}

	private static float cos(float value) {
		return (float)Math.cos((double)value);
	}

	private static float sin(float value) {
		return (float)Math.sin((double)value);
	}
}
