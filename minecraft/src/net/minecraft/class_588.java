package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_588<T extends class_1297> extends class_583<T> {
	private final class_630 field_3475;
	private final class_630 field_3477;
	private final class_630 field_3476;
	private final class_630 field_3474;
	private final class_630 field_3472;
	private final class_630 field_3478;
	private final class_630 field_3471;
	private final class_630 field_3473;

	public class_588() {
		this.field_17138 = 64;
		this.field_17139 = 64;
		this.field_3475 = new class_630(this, 0, 8);
		this.field_3475.method_2844(-3.0F, -2.0F, -8.0F, 5, 3, 9);
		this.field_3471 = new class_630(this, 3, 20);
		this.field_3471.method_2844(-2.0F, 0.0F, 0.0F, 3, 2, 6);
		this.field_3471.method_2851(0.0F, -2.0F, 1.0F);
		this.field_3475.method_2845(this.field_3471);
		this.field_3473 = new class_630(this, 4, 29);
		this.field_3473.method_2844(-1.0F, 0.0F, 0.0F, 1, 1, 6);
		this.field_3473.method_2851(0.0F, 0.5F, 6.0F);
		this.field_3471.method_2845(this.field_3473);
		this.field_3477 = new class_630(this, 23, 12);
		this.field_3477.method_2844(0.0F, 0.0F, 0.0F, 6, 2, 9);
		this.field_3477.method_2851(2.0F, -2.0F, -8.0F);
		this.field_3476 = new class_630(this, 16, 24);
		this.field_3476.method_2844(0.0F, 0.0F, 0.0F, 13, 1, 9);
		this.field_3476.method_2851(6.0F, 0.0F, 0.0F);
		this.field_3477.method_2845(this.field_3476);
		this.field_3474 = new class_630(this, 23, 12);
		this.field_3474.field_3666 = true;
		this.field_3474.method_2844(-6.0F, 0.0F, 0.0F, 6, 2, 9);
		this.field_3474.method_2851(-3.0F, -2.0F, -8.0F);
		this.field_3472 = new class_630(this, 16, 24);
		this.field_3472.field_3666 = true;
		this.field_3472.method_2844(-13.0F, 0.0F, 0.0F, 13, 1, 9);
		this.field_3472.method_2851(-6.0F, 0.0F, 0.0F);
		this.field_3474.method_2845(this.field_3472);
		this.field_3477.field_3674 = 0.1F;
		this.field_3476.field_3674 = 0.1F;
		this.field_3474.field_3674 = -0.1F;
		this.field_3472.field_3674 = -0.1F;
		this.field_3475.field_3654 = -0.1F;
		this.field_3478 = new class_630(this, 0, 0);
		this.field_3478.method_2844(-4.0F, -2.0F, -5.0F, 7, 3, 5);
		this.field_3478.method_2851(0.0F, 1.0F, -7.0F);
		this.field_3478.field_3654 = 0.2F;
		this.field_3475.method_2845(this.field_3478);
		this.field_3475.method_2845(this.field_3477);
		this.field_3475.method_2845(this.field_3474);
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.field_3475.method_2846(k);
	}

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		float l = ((float)(arg.method_5628() * 3) + h) * 0.13F;
		float m = 16.0F;
		this.field_3477.field_3674 = class_3532.method_15362(l) * 16.0F * (float) (Math.PI / 180.0);
		this.field_3476.field_3674 = class_3532.method_15362(l) * 16.0F * (float) (Math.PI / 180.0);
		this.field_3474.field_3674 = -this.field_3477.field_3674;
		this.field_3472.field_3674 = -this.field_3476.field_3674;
		this.field_3471.field_3654 = -(5.0F + class_3532.method_15362(l * 2.0F) * 5.0F) * (float) (Math.PI / 180.0);
		this.field_3473.field_3654 = -(5.0F + class_3532.method_15362(l * 2.0F) * 5.0F) * (float) (Math.PI / 180.0);
	}
}
