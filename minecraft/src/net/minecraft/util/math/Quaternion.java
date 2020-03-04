package net.minecraft.util.math;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;

public final class Quaternion {
	public static final Quaternion IDENTITY = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
	private float field_21582;
	private float field_21583;
	private float field_21584;
	private float a;

	public Quaternion(float b, float c, float d, float a) {
		this.field_21582 = b;
		this.field_21583 = c;
		this.field_21584 = d;
		this.a = a;
	}

	public Quaternion(Vector3f axis, float rotationAngle, boolean degrees) {
		if (degrees) {
			rotationAngle *= (float) (Math.PI / 180.0);
		}

		float f = sin(rotationAngle / 2.0F);
		this.field_21582 = axis.getX() * f;
		this.field_21583 = axis.getY() * f;
		this.field_21584 = axis.getZ() * f;
		this.a = cos(rotationAngle / 2.0F);
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
		this.field_21582 = f * i * k + g * h * j;
		this.field_21583 = g * h * k - f * i * j;
		this.field_21584 = f * h * k + g * i * j;
		this.a = g * i * k - f * h * j;
	}

	public Quaternion(Quaternion other) {
		this.field_21582 = other.field_21582;
		this.field_21583 = other.field_21583;
		this.field_21584 = other.field_21584;
		this.a = other.a;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			Quaternion quaternion = (Quaternion)o;
			if (Float.compare(quaternion.field_21582, this.field_21582) != 0) {
				return false;
			} else if (Float.compare(quaternion.field_21583, this.field_21583) != 0) {
				return false;
			} else {
				return Float.compare(quaternion.field_21584, this.field_21584) != 0 ? false : Float.compare(quaternion.a, this.a) == 0;
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = Float.floatToIntBits(this.field_21582);
		i = 31 * i + Float.floatToIntBits(this.field_21583);
		i = 31 * i + Float.floatToIntBits(this.field_21584);
		return 31 * i + Float.floatToIntBits(this.a);
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
		return this.field_21582;
	}

	public float getC() {
		return this.field_21583;
	}

	public float getD() {
		return this.field_21584;
	}

	public float getA() {
		return this.a;
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
		this.field_21582 = i * j + f * m + g * l - h * k;
		this.field_21583 = i * k - f * l + g * m + h * j;
		this.field_21584 = i * l + f * k - g * j + h * m;
		this.a = i * m - f * j - g * k - h * l;
	}

	@Environment(EnvType.CLIENT)
	public void scale(float scale) {
		this.field_21582 *= scale;
		this.field_21583 *= scale;
		this.field_21584 *= scale;
		this.a *= scale;
	}

	public void conjugate() {
		this.field_21582 = -this.field_21582;
		this.field_21583 = -this.field_21583;
		this.field_21584 = -this.field_21584;
	}

	@Environment(EnvType.CLIENT)
	public void set(float a, float b, float c, float d) {
		this.field_21582 = a;
		this.field_21583 = b;
		this.field_21584 = c;
		this.a = d;
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
			this.field_21582 *= g;
			this.field_21583 *= g;
			this.field_21584 *= g;
			this.a *= g;
		} else {
			this.field_21582 = 0.0F;
			this.field_21583 = 0.0F;
			this.field_21584 = 0.0F;
			this.a = 0.0F;
		}
	}

	@Environment(EnvType.CLIENT)
	public Quaternion copy() {
		return new Quaternion(this);
	}
}
