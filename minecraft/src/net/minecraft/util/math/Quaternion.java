package net.minecraft.util.math;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;

public final class Quaternion {
	public static final Quaternion IDENTITY = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
	private final float[] components;

	private Quaternion(float[] fs) {
		this.components = fs;
	}

	public Quaternion(float b, float c, float d, float a) {
		this(new float[4]);
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

	@Environment(EnvType.CLIENT)
	public void scale(float scale) {
		this.components[0] = this.components[0] * scale;
		this.components[1] = this.components[1] * scale;
		this.components[2] = this.components[2] * scale;
		this.components[3] = this.components[3] * scale;
	}

	public void conjugate() {
		this.components[0] = -this.components[0];
		this.components[1] = -this.components[1];
		this.components[2] = -this.components[2];
	}

	@Environment(EnvType.CLIENT)
	public void method_23758(float f, float g, float h, float i) {
		this.components[0] = f;
		this.components[1] = g;
		this.components[2] = h;
		this.components[3] = i;
	}

	private static float cos(float value) {
		return (float)Math.cos((double)value);
	}

	private static float sin(float value) {
		return (float)Math.sin((double)value);
	}

	@Environment(EnvType.CLIENT)
	public void normalize() {
		float f = this.getB() * this.getB() + this.getC() * this.getC() + this.getD() * this.getD() + this.getA() * this.getA();
		if (f > 1.0E-6F) {
			float g = MathHelper.fastInverseSqrt(f);
			this.components[0] = this.components[0] * g;
			this.components[1] = this.components[1] * g;
			this.components[2] = this.components[2] * g;
			this.components[3] = this.components[3] * g;
		} else {
			this.components[0] = 0.0F;
			this.components[1] = 0.0F;
			this.components[2] = 0.0F;
			this.components[3] = 0.0F;
		}
	}

	@Environment(EnvType.CLIENT)
	public Quaternion copy() {
		return new Quaternion((float[])this.components.clone());
	}
}
