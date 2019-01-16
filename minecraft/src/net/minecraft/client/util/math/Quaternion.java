package net.minecraft.client.util.math;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
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

		float g = method_16002(f / 2.0F);
		this.components = new float[4];
		this.components[0] = vector3f.x() * g;
		this.components[1] = vector3f.y() * g;
		this.components[2] = vector3f.z() * g;
		this.components[3] = method_16003(f / 2.0F);
	}

	public Quaternion(float f, float g, float h, boolean bl) {
		if (bl) {
			f *= (float) (Math.PI / 180.0);
			g *= (float) (Math.PI / 180.0);
			h *= (float) (Math.PI / 180.0);
		}

		float i = method_16002(0.5F * f);
		float j = method_16003(0.5F * f);
		float k = method_16002(0.5F * g);
		float l = method_16003(0.5F * g);
		float m = method_16002(0.5F * h);
		float n = method_16003(0.5F * h);
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
		stringBuilder.append("Quaternion[").append(this.method_4924()).append(" + ");
		stringBuilder.append(this.method_4921()).append("i + ");
		stringBuilder.append(this.method_4922()).append("j + ");
		stringBuilder.append(this.method_4923()).append("k]");
		return stringBuilder.toString();
	}

	public float method_4921() {
		return this.components[0];
	}

	public float method_4922() {
		return this.components[1];
	}

	public float method_4923() {
		return this.components[2];
	}

	public float method_4924() {
		return this.components[3];
	}

	public void method_4925(Quaternion quaternion) {
		float f = this.method_4921();
		float g = this.method_4922();
		float h = this.method_4923();
		float i = this.method_4924();
		float j = quaternion.method_4921();
		float k = quaternion.method_4922();
		float l = quaternion.method_4923();
		float m = quaternion.method_4924();
		this.components[0] = i * j + f * m + g * l - h * k;
		this.components[1] = i * k - f * l + g * m + h * j;
		this.components[2] = i * l + f * k - g * j + h * m;
		this.components[3] = i * m - f * j - g * k - h * l;
	}

	public void method_4926() {
		this.components[0] = -this.components[0];
		this.components[1] = -this.components[1];
		this.components[2] = -this.components[2];
	}

	private static float method_16003(float f) {
		return (float)Math.cos((double)f);
	}

	private static float method_16002(float f) {
		return (float)Math.sin((double)f);
	}
}
