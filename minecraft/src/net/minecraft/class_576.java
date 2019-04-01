package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_576<T extends class_1621> extends class_583<T> {
	private final class_630[] field_3427 = new class_630[8];
	private final class_630 field_3428;

	public class_576() {
		for (int i = 0; i < this.field_3427.length; i++) {
			int j = 0;
			int k = i;
			if (i == 2) {
				j = 24;
				k = 10;
			} else if (i == 3) {
				j = 24;
				k = 19;
			}

			this.field_3427[i] = new class_630(this, j, k);
			this.field_3427[i].method_2844(-4.0F, (float)(16 + i), -4.0F, 8, 1, 8);
		}

		this.field_3428 = new class_630(this, 0, 16);
		this.field_3428.method_2844(-2.0F, 18.0F, -2.0F, 4, 4, 4);
	}

	public void method_17098(T arg, float f, float g, float h) {
		float i = class_3532.method_16439(h, arg.field_7387, arg.field_7388);
		if (i < 0.0F) {
			i = 0.0F;
		}

		for (int j = 0; j < this.field_3427.length; j++) {
			this.field_3427[j].field_3656 = (float)(-(4 - j)) * i * 1.7F;
		}
	}

	public void method_17099(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);
		this.field_3428.method_2846(k);

		for (class_630 lv : this.field_3427) {
			lv.method_2846(k);
		}
	}
}
