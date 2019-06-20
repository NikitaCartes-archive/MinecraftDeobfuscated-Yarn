package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_557 extends class_3879 {
	private final class_630 field_3336 = new class_630(this).method_2850(0, 0).method_2844(-6.0F, -5.0F, 0.0F, 6, 10, 0);
	private final class_630 field_3338 = new class_630(this).method_2850(16, 0).method_2844(0.0F, -5.0F, 0.0F, 6, 10, 0);
	private final class_630 field_3337;
	private final class_630 field_3335;
	private final class_630 field_3334;
	private final class_630 field_3339;
	private final class_630 field_3333 = new class_630(this).method_2850(12, 0).method_2844(-1.0F, -5.0F, 0.0F, 2, 10, 0);

	public class_557() {
		this.field_3337 = new class_630(this).method_2850(0, 10).method_2844(0.0F, -4.0F, -0.99F, 5, 8, 1);
		this.field_3335 = new class_630(this).method_2850(12, 10).method_2844(0.0F, -4.0F, -0.01F, 5, 8, 1);
		this.field_3334 = new class_630(this).method_2850(24, 10).method_2844(0.0F, -4.0F, 0.0F, 5, 8, 0);
		this.field_3339 = new class_630(this).method_2850(24, 10).method_2844(0.0F, -4.0F, 0.0F, 5, 8, 0);
		this.field_3336.method_2851(0.0F, 0.0F, -1.0F);
		this.field_3338.method_2851(0.0F, 0.0F, 1.0F);
		this.field_3333.field_3675 = (float) (Math.PI / 2);
	}

	public void method_17072(float f, float g, float h, float i, float j, float k) {
		this.method_17073(f, g, h, i, j, k);
		this.field_3336.method_2846(k);
		this.field_3338.method_2846(k);
		this.field_3333.method_2846(k);
		this.field_3337.method_2846(k);
		this.field_3335.method_2846(k);
		this.field_3334.method_2846(k);
		this.field_3339.method_2846(k);
	}

	private void method_17073(float f, float g, float h, float i, float j, float k) {
		float l = (class_3532.method_15374(f * 0.02F) * 0.1F + 1.25F) * i;
		this.field_3336.field_3675 = (float) Math.PI + l;
		this.field_3338.field_3675 = -l;
		this.field_3337.field_3675 = l;
		this.field_3335.field_3675 = -l;
		this.field_3334.field_3675 = l - l * 2.0F * g;
		this.field_3339.field_3675 = l - l * 2.0F * h;
		this.field_3337.field_3657 = class_3532.method_15374(l);
		this.field_3335.field_3657 = class_3532.method_15374(l);
		this.field_3334.field_3657 = class_3532.method_15374(l);
		this.field_3339.field_3657 = class_3532.method_15374(l);
	}
}
