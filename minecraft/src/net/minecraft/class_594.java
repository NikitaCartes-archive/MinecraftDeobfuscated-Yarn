package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_594<T extends class_1297> extends class_583<T> {
	private final class_630 field_3505;
	private final class_630 field_3507;
	private final class_630 field_3506;
	private final class_630 field_3504;
	private final class_630 field_3503;
	private final class_630 field_3508;

	public class_594() {
		this.field_17138 = 32;
		this.field_17139 = 32;
		int i = 23;
		this.field_3505 = new class_630(this, 0, 27);
		this.field_3505.method_2844(-1.5F, -2.0F, -1.5F, 3, 2, 3);
		this.field_3505.method_2851(0.0F, 23.0F, 0.0F);
		this.field_3507 = new class_630(this, 24, 6);
		this.field_3507.method_2844(-1.5F, 0.0F, -1.5F, 1, 1, 1);
		this.field_3507.method_2851(0.0F, 20.0F, 0.0F);
		this.field_3506 = new class_630(this, 28, 6);
		this.field_3506.method_2844(0.5F, 0.0F, -1.5F, 1, 1, 1);
		this.field_3506.method_2851(0.0F, 20.0F, 0.0F);
		this.field_3508 = new class_630(this, -3, 0);
		this.field_3508.method_2844(-1.5F, 0.0F, 0.0F, 3, 0, 3);
		this.field_3508.method_2851(0.0F, 22.0F, 1.5F);
		this.field_3504 = new class_630(this, 25, 0);
		this.field_3504.method_2844(-1.0F, 0.0F, 0.0F, 1, 0, 2);
		this.field_3504.method_2851(-1.5F, 22.0F, -1.5F);
		this.field_3503 = new class_630(this, 25, 0);
		this.field_3503.method_2844(0.0F, 0.0F, 0.0F, 1, 0, 2);
		this.field_3503.method_2851(1.5F, 22.0F, -1.5F);
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);
		this.field_3505.method_2846(k);
		this.field_3507.method_2846(k);
		this.field_3506.method_2846(k);
		this.field_3508.method_2846(k);
		this.field_3504.method_2846(k);
		this.field_3503.method_2846(k);
	}

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		this.field_3504.field_3674 = -0.2F + 0.4F * class_3532.method_15374(h * 0.2F);
		this.field_3503.field_3674 = 0.2F - 0.4F * class_3532.method_15374(h * 0.2F);
	}
}
