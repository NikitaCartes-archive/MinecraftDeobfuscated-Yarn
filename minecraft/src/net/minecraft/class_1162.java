package net.minecraft;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1162 {
	private final float[] field_5662;

	public class_1162() {
		this.field_5662 = new float[4];
	}

	public class_1162(float f, float g, float h, float i) {
		this.field_5662 = new float[]{f, g, h, i};
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_1162 lv = (class_1162)object;
			return Arrays.equals(this.field_5662, lv.field_5662);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Arrays.hashCode(this.field_5662);
	}

	public float method_4953() {
		return this.field_5662[0];
	}

	public float method_4956() {
		return this.field_5662[1];
	}

	public float method_4957() {
		return this.field_5662[2];
	}

	public float method_4958() {
		return this.field_5662[3];
	}

	public void method_4954(class_1160 arg) {
		this.field_5662[0] = this.field_5662[0] * arg.method_4943();
		this.field_5662[1] = this.field_5662[1] * arg.method_4945();
		this.field_5662[2] = this.field_5662[2] * arg.method_4947();
	}

	public void method_4955(float f, float g, float h, float i) {
		this.field_5662[0] = f;
		this.field_5662[1] = g;
		this.field_5662[2] = h;
		this.field_5662[3] = i;
	}

	public void method_4960(class_1159 arg) {
		float[] fs = Arrays.copyOf(this.field_5662, 4);

		for (int i = 0; i < 4; i++) {
			this.field_5662[i] = 0.0F;

			for (int j = 0; j < 4; j++) {
				this.field_5662[i] = this.field_5662[i] + arg.method_4938(i, j) * fs[j];
			}
		}
	}

	public void method_4959(class_1158 arg) {
		class_1158 lv = new class_1158(arg);
		lv.method_4925(new class_1158(this.method_4953(), this.method_4956(), this.method_4957(), 0.0F));
		class_1158 lv2 = new class_1158(arg);
		lv2.method_4926();
		lv.method_4925(lv2);
		this.method_4955(lv.method_4921(), lv.method_4922(), lv.method_4923(), this.method_4958());
	}
}
