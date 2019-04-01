package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_602<T extends class_1606> extends class_583<T> {
	private final class_630 field_3553;
	private final class_630 field_3555;
	private final class_630 field_3554;

	public class_602() {
		this.field_17139 = 64;
		this.field_17138 = 64;
		this.field_3555 = new class_630(this);
		this.field_3553 = new class_630(this);
		this.field_3554 = new class_630(this);
		this.field_3555.method_2850(0, 0).method_2844(-8.0F, -16.0F, -8.0F, 16, 12, 16);
		this.field_3555.method_2851(0.0F, 24.0F, 0.0F);
		this.field_3553.method_2850(0, 28).method_2844(-8.0F, -8.0F, -8.0F, 16, 8, 16);
		this.field_3553.method_2851(0.0F, 24.0F, 0.0F);
		this.field_3554.method_2850(0, 52).method_2844(-3.0F, 0.0F, -3.0F, 6, 6, 6);
		this.field_3554.method_2851(0.0F, 12.0F, 0.0F);
	}

	public void method_17122(T arg, float f, float g, float h, float i, float j, float k) {
		float l = h - (float)arg.field_6012;
		float m = (0.5F + arg.method_7116(l)) * (float) Math.PI;
		float n = -1.0F + class_3532.method_15374(m);
		float o = 0.0F;
		if (m > (float) Math.PI) {
			o = class_3532.method_15374(h * 0.1F) * 0.7F;
		}

		this.field_3555.method_2851(0.0F, 16.0F + class_3532.method_15374(m) * 8.0F + o, 0.0F);
		if (arg.method_7116(l) > 0.3F) {
			this.field_3555.field_3675 = n * n * n * n * (float) Math.PI * 0.125F;
		} else {
			this.field_3555.field_3675 = 0.0F;
		}

		this.field_3554.field_3654 = j * (float) (Math.PI / 180.0);
		this.field_3554.field_3675 = i * (float) (Math.PI / 180.0);
	}

	public void method_17123(T arg, float f, float g, float h, float i, float j, float k) {
		this.field_3553.method_2846(k);
		this.field_3555.method_2846(k);
	}

	public class_630 method_2831() {
		return this.field_3553;
	}

	public class_630 method_2829() {
		return this.field_3555;
	}

	public class_630 method_2830() {
		return this.field_3554;
	}
}
