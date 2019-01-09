package net.minecraft;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class class_1160 {
	private final float[] field_5658;

	public class_1160(class_1160 arg) {
		this.field_5658 = Arrays.copyOf(arg.field_5658, 3);
	}

	public class_1160() {
		this.field_5658 = new float[3];
	}

	public class_1160(float f, float g, float h) {
		this.field_5658 = new float[]{f, g, h};
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_1160 lv = (class_1160)object;
			return Arrays.equals(this.field_5658, lv.field_5658);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Arrays.hashCode(this.field_5658);
	}

	public float method_4943() {
		return this.field_5658[0];
	}

	public float method_4945() {
		return this.field_5658[1];
	}

	public float method_4947() {
		return this.field_5658[2];
	}

	public void method_4942(float f) {
		for (int i = 0; i < 3; i++) {
			this.field_5658[i] = this.field_5658[i] * f;
		}
	}

	private static float method_16004(float f, float g, float h) {
		if (f < g) {
			return g;
		} else {
			return f > h ? h : f;
		}
	}

	public void method_4946(float f, float g) {
		this.field_5658[0] = method_16004(this.field_5658[0], f, g);
		this.field_5658[1] = method_16004(this.field_5658[1], f, g);
		this.field_5658[2] = method_16004(this.field_5658[2], f, g);
	}

	public void method_4949(float f, float g, float h) {
		this.field_5658[0] = f;
		this.field_5658[1] = g;
		this.field_5658[2] = h;
	}

	public void method_4948(float f, float g, float h) {
		this.field_5658[0] = this.field_5658[0] + f;
		this.field_5658[1] = this.field_5658[1] + g;
		this.field_5658[2] = this.field_5658[2] + h;
	}

	public void method_4944(class_1160 arg) {
		for (int i = 0; i < 3; i++) {
			this.field_5658[i] = this.field_5658[i] - arg.field_5658[i];
		}
	}

	public float method_4950(class_1160 arg) {
		float f = 0.0F;

		for (int i = 0; i < 3; i++) {
			f += this.field_5658[i] * arg.field_5658[i];
		}

		return f;
	}

	public void method_4952() {
		float f = 0.0F;

		for (int i = 0; i < 3; i++) {
			f += this.field_5658[i] * this.field_5658[i];
		}

		for (int i = 0; i < 3; i++) {
			this.field_5658[i] = this.field_5658[i] / f;
		}
	}

	public void method_4951(class_1160 arg) {
		float f = this.field_5658[0];
		float g = this.field_5658[1];
		float h = this.field_5658[2];
		float i = arg.method_4943();
		float j = arg.method_4945();
		float k = arg.method_4947();
		this.field_5658[0] = g * k - h * j;
		this.field_5658[1] = h * i - f * k;
		this.field_5658[2] = f * j - g * i;
	}
}
