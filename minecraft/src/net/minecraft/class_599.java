package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_599<T extends class_1297> extends class_583<T> {
	private final class_630 field_3546;
	private final class_630 field_3548;
	private final class_630 field_3547;
	private final class_630 field_3545;
	private final class_630 field_3543;
	private final class_630 field_3549;
	private final class_630 field_3542;
	private final class_630 field_3544;

	public class_599() {
		this.field_17138 = 32;
		this.field_17139 = 32;
		int i = 20;
		this.field_3546 = new class_630(this, 0, 0);
		this.field_3546.method_2844(-1.5F, -2.5F, 0.0F, 3, 5, 8);
		this.field_3546.method_2851(0.0F, 20.0F, 0.0F);
		this.field_3548 = new class_630(this, 0, 13);
		this.field_3548.method_2844(-1.5F, -2.5F, 0.0F, 3, 5, 8);
		this.field_3548.method_2851(0.0F, 20.0F, 8.0F);
		this.field_3547 = new class_630(this, 22, 0);
		this.field_3547.method_2844(-1.0F, -2.0F, -3.0F, 2, 4, 3);
		this.field_3547.method_2851(0.0F, 20.0F, 0.0F);
		this.field_3549 = new class_630(this, 20, 10);
		this.field_3549.method_2844(0.0F, -2.5F, 0.0F, 0, 5, 6);
		this.field_3549.method_2851(0.0F, 0.0F, 8.0F);
		this.field_3548.method_2845(this.field_3549);
		this.field_3545 = new class_630(this, 2, 1);
		this.field_3545.method_2844(0.0F, 0.0F, 0.0F, 0, 2, 3);
		this.field_3545.method_2851(0.0F, -4.5F, 5.0F);
		this.field_3546.method_2845(this.field_3545);
		this.field_3543 = new class_630(this, 0, 2);
		this.field_3543.method_2844(0.0F, 0.0F, 0.0F, 0, 2, 4);
		this.field_3543.method_2851(0.0F, -4.5F, -1.0F);
		this.field_3548.method_2845(this.field_3543);
		this.field_3542 = new class_630(this, -4, 0);
		this.field_3542.method_2844(-2.0F, 0.0F, 0.0F, 2, 0, 2);
		this.field_3542.method_2851(-1.5F, 21.5F, 0.0F);
		this.field_3542.field_3674 = (float) (-Math.PI / 4);
		this.field_3544 = new class_630(this, 0, 0);
		this.field_3544.method_2844(0.0F, 0.0F, 0.0F, 2, 0, 2);
		this.field_3544.method_2851(1.5F, 21.5F, 0.0F);
		this.field_3544.field_3674 = (float) (Math.PI / 4);
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);
		this.field_3546.method_2846(k);
		this.field_3548.method_2846(k);
		this.field_3547.method_2846(k);
		this.field_3542.method_2846(k);
		this.field_3544.method_2846(k);
	}

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		float l = 1.0F;
		float m = 1.0F;
		if (!arg.method_5799()) {
			l = 1.3F;
			m = 1.7F;
		}

		this.field_3548.field_3675 = -l * 0.25F * class_3532.method_15374(m * 0.6F * h);
	}
}
