package net.minecraft;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class class_1160 {
	private final float[] field_5658;

	@Environment(EnvType.CLIENT)
	public class_1160(class_1160 arg) {
		this.field_5658 = Arrays.copyOf(arg.field_5658, 3);
	}

	public class_1160() {
		this.field_5658 = new float[3];
	}

	@Environment(EnvType.CLIENT)
	public class_1160(float f, float g, float h) {
		this.field_5658 = new float[]{f, g, h};
	}

	public class_1160(class_243 arg) {
		this.field_5658 = new float[]{(float)arg.field_1352, (float)arg.field_1351, (float)arg.field_1350};
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

	@Environment(EnvType.CLIENT)
	public void method_4942(float f) {
		for (int i = 0; i < 3; i++) {
			this.field_5658[i] = this.field_5658[i] * f;
		}
	}

	@Environment(EnvType.CLIENT)
	private static float method_16004(float f, float g, float h) {
		if (f < g) {
			return g;
		} else {
			return f > h ? h : f;
		}
	}

	@Environment(EnvType.CLIENT)
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

	@Environment(EnvType.CLIENT)
	public void method_4948(float f, float g, float h) {
		this.field_5658[0] = this.field_5658[0] + f;
		this.field_5658[1] = this.field_5658[1] + g;
		this.field_5658[2] = this.field_5658[2] + h;
	}

	@Environment(EnvType.CLIENT)
	public void method_4944(class_1160 arg) {
		for (int i = 0; i < 3; i++) {
			this.field_5658[i] = this.field_5658[i] - arg.field_5658[i];
		}
	}

	@Environment(EnvType.CLIENT)
	public float method_4950(class_1160 arg) {
		float f = 0.0F;

		for (int i = 0; i < 3; i++) {
			f += this.field_5658[i] * arg.field_5658[i];
		}

		return f;
	}

	@Environment(EnvType.CLIENT)
	public void method_4952() {
		float f = 0.0F;

		for (int i = 0; i < 3; i++) {
			f += this.field_5658[i] * this.field_5658[i];
		}

		for (int i = 0; i < 3; i++) {
			this.field_5658[i] = this.field_5658[i] / f;
		}
	}

	@Environment(EnvType.CLIENT)
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

	public void method_19262(class_1158 arg) {
		class_1158 lv = new class_1158(arg);
		lv.method_4925(new class_1158(this.method_4943(), this.method_4945(), this.method_4947(), 0.0F));
		class_1158 lv2 = new class_1158(arg);
		lv2.method_4926();
		lv.method_4925(lv2);
		this.method_4949(lv.method_4921(), lv.method_4922(), lv.method_4923());
	}
}
