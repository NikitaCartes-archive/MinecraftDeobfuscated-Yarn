package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_595<T extends class_1297> extends class_583<T> {
	private final class_630 field_3516;
	private final class_630 field_3518;
	private final class_630 field_3517;
	private final class_630 field_3513;
	private final class_630 field_3511;
	private final class_630 field_3519;
	private final class_630 field_3510;
	private final class_630 field_3512;
	private final class_630 field_3514;
	private final class_630 field_3509;
	private final class_630 field_3515;

	public class_595() {
		this.field_17138 = 32;
		this.field_17139 = 32;
		int i = 22;
		this.field_3516 = new class_630(this, 12, 22);
		this.field_3516.method_2844(-2.5F, -5.0F, -2.5F, 5, 5, 5);
		this.field_3516.method_2851(0.0F, 22.0F, 0.0F);
		this.field_3518 = new class_630(this, 24, 0);
		this.field_3518.method_2844(-2.0F, 0.0F, 0.0F, 2, 0, 2);
		this.field_3518.method_2851(-2.5F, 17.0F, -1.5F);
		this.field_3517 = new class_630(this, 24, 3);
		this.field_3517.method_2844(0.0F, 0.0F, 0.0F, 2, 0, 2);
		this.field_3517.method_2851(2.5F, 17.0F, -1.5F);
		this.field_3513 = new class_630(this, 15, 16);
		this.field_3513.method_2844(-2.5F, -1.0F, 0.0F, 5, 1, 1);
		this.field_3513.method_2851(0.0F, 17.0F, -2.5F);
		this.field_3513.field_3654 = (float) (Math.PI / 4);
		this.field_3511 = new class_630(this, 10, 16);
		this.field_3511.method_2844(-2.5F, -1.0F, -1.0F, 5, 1, 1);
		this.field_3511.method_2851(0.0F, 17.0F, 2.5F);
		this.field_3511.field_3654 = (float) (-Math.PI / 4);
		this.field_3519 = new class_630(this, 8, 16);
		this.field_3519.method_2844(-1.0F, -5.0F, 0.0F, 1, 5, 1);
		this.field_3519.method_2851(-2.5F, 22.0F, -2.5F);
		this.field_3519.field_3675 = (float) (-Math.PI / 4);
		this.field_3510 = new class_630(this, 8, 16);
		this.field_3510.method_2844(-1.0F, -5.0F, 0.0F, 1, 5, 1);
		this.field_3510.method_2851(-2.5F, 22.0F, 2.5F);
		this.field_3510.field_3675 = (float) (Math.PI / 4);
		this.field_3512 = new class_630(this, 4, 16);
		this.field_3512.method_2844(0.0F, -5.0F, 0.0F, 1, 5, 1);
		this.field_3512.method_2851(2.5F, 22.0F, 2.5F);
		this.field_3512.field_3675 = (float) (-Math.PI / 4);
		this.field_3514 = new class_630(this, 0, 16);
		this.field_3514.method_2844(0.0F, -5.0F, 0.0F, 1, 5, 1);
		this.field_3514.method_2851(2.5F, 22.0F, -2.5F);
		this.field_3514.field_3675 = (float) (Math.PI / 4);
		this.field_3509 = new class_630(this, 8, 22);
		this.field_3509.method_2844(0.0F, 0.0F, 0.0F, 1, 1, 1);
		this.field_3509.method_2851(0.5F, 22.0F, 2.5F);
		this.field_3509.field_3654 = (float) (Math.PI / 4);
		this.field_3515 = new class_630(this, 17, 21);
		this.field_3515.method_2844(-2.5F, 0.0F, 0.0F, 5, 1, 1);
		this.field_3515.method_2851(0.0F, 22.0F, -2.5F);
		this.field_3515.field_3654 = (float) (-Math.PI / 4);
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);
		this.field_3516.method_2846(k);
		this.field_3518.method_2846(k);
		this.field_3517.method_2846(k);
		this.field_3513.method_2846(k);
		this.field_3511.method_2846(k);
		this.field_3519.method_2846(k);
		this.field_3510.method_2846(k);
		this.field_3512.method_2846(k);
		this.field_3514.method_2846(k);
		this.field_3509.method_2846(k);
		this.field_3515.method_2846(k);
	}

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		this.field_3518.field_3674 = -0.2F + 0.4F * class_3532.method_15374(h * 0.2F);
		this.field_3517.field_3674 = 0.2F - 0.4F * class_3532.method_15374(h * 0.2F);
	}
}
