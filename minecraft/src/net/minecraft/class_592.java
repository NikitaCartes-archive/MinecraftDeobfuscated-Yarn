package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_592<T extends class_1297> extends class_583<T> {
	private final class_630 field_3493;
	private final class_630 field_3499;
	private final class_630 field_3494;
	private final class_630 field_3490;
	private final class_630 field_3496;
	private final class_630 field_3495;
	private final class_630 field_3489;
	private final class_630 field_3497;
	private final class_630 field_3491;
	private final class_630 field_3487;
	private final class_630 field_3492;
	private final class_630 field_3498;
	private final class_630 field_3488;

	public class_592() {
		this.field_17138 = 32;
		this.field_17139 = 32;
		int i = 22;
		this.field_3493 = new class_630(this, 0, 0);
		this.field_3493.method_2844(-4.0F, -8.0F, -4.0F, 8, 8, 8);
		this.field_3493.method_2851(0.0F, 22.0F, 0.0F);
		this.field_3499 = new class_630(this, 24, 0);
		this.field_3499.method_2844(-2.0F, 0.0F, -1.0F, 2, 1, 2);
		this.field_3499.method_2851(-4.0F, 15.0F, -2.0F);
		this.field_3494 = new class_630(this, 24, 3);
		this.field_3494.method_2844(0.0F, 0.0F, -1.0F, 2, 1, 2);
		this.field_3494.method_2851(4.0F, 15.0F, -2.0F);
		this.field_3490 = new class_630(this, 15, 17);
		this.field_3490.method_2844(-4.0F, -1.0F, 0.0F, 8, 1, 0);
		this.field_3490.method_2851(0.0F, 14.0F, -4.0F);
		this.field_3490.field_3654 = (float) (Math.PI / 4);
		this.field_3496 = new class_630(this, 14, 16);
		this.field_3496.method_2844(-4.0F, -1.0F, 0.0F, 8, 1, 1);
		this.field_3496.method_2851(0.0F, 14.0F, 0.0F);
		this.field_3495 = new class_630(this, 23, 18);
		this.field_3495.method_2844(-4.0F, -1.0F, 0.0F, 8, 1, 0);
		this.field_3495.method_2851(0.0F, 14.0F, 4.0F);
		this.field_3495.field_3654 = (float) (-Math.PI / 4);
		this.field_3489 = new class_630(this, 5, 17);
		this.field_3489.method_2844(-1.0F, -8.0F, 0.0F, 1, 8, 0);
		this.field_3489.method_2851(-4.0F, 22.0F, -4.0F);
		this.field_3489.field_3675 = (float) (-Math.PI / 4);
		this.field_3497 = new class_630(this, 1, 17);
		this.field_3497.method_2844(0.0F, -8.0F, 0.0F, 1, 8, 0);
		this.field_3497.method_2851(4.0F, 22.0F, -4.0F);
		this.field_3497.field_3675 = (float) (Math.PI / 4);
		this.field_3491 = new class_630(this, 15, 20);
		this.field_3491.method_2844(-4.0F, 0.0F, 0.0F, 8, 1, 0);
		this.field_3491.method_2851(0.0F, 22.0F, -4.0F);
		this.field_3491.field_3654 = (float) (-Math.PI / 4);
		this.field_3492 = new class_630(this, 15, 20);
		this.field_3492.method_2844(-4.0F, 0.0F, 0.0F, 8, 1, 0);
		this.field_3492.method_2851(0.0F, 22.0F, 0.0F);
		this.field_3487 = new class_630(this, 15, 20);
		this.field_3487.method_2844(-4.0F, 0.0F, 0.0F, 8, 1, 0);
		this.field_3487.method_2851(0.0F, 22.0F, 4.0F);
		this.field_3487.field_3654 = (float) (Math.PI / 4);
		this.field_3498 = new class_630(this, 9, 17);
		this.field_3498.method_2844(-1.0F, -8.0F, 0.0F, 1, 8, 0);
		this.field_3498.method_2851(-4.0F, 22.0F, 4.0F);
		this.field_3498.field_3675 = (float) (Math.PI / 4);
		this.field_3488 = new class_630(this, 9, 17);
		this.field_3488.method_2844(0.0F, -8.0F, 0.0F, 1, 8, 0);
		this.field_3488.method_2851(4.0F, 22.0F, 4.0F);
		this.field_3488.field_3675 = (float) (-Math.PI / 4);
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);
		this.field_3493.method_2846(k);
		this.field_3499.method_2846(k);
		this.field_3494.method_2846(k);
		this.field_3490.method_2846(k);
		this.field_3496.method_2846(k);
		this.field_3495.method_2846(k);
		this.field_3489.method_2846(k);
		this.field_3497.method_2846(k);
		this.field_3491.method_2846(k);
		this.field_3492.method_2846(k);
		this.field_3487.method_2846(k);
		this.field_3498.method_2846(k);
		this.field_3488.method_2846(k);
	}

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		this.field_3499.field_3674 = -0.2F + 0.4F * class_3532.method_15374(h * 0.2F);
		this.field_3494.field_3674 = 0.2F - 0.4F * class_3532.method_15374(h * 0.2F);
	}
}
