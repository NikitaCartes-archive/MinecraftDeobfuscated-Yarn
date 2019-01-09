package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_889<T extends class_1297> extends class_583<T> {
	private final class_630 field_4656;
	private final class_630 field_4658;
	private final class_630 field_4657;
	private final class_630 field_4655;

	public class_889() {
		this.field_17138 = 64;
		this.field_17139 = 64;
		float f = 18.0F;
		float g = -8.0F;
		this.field_4658 = new class_630(this, 22, 0);
		this.field_4658.method_2844(-4.0F, -7.0F, 0.0F, 8, 7, 13);
		this.field_4658.method_2851(0.0F, 22.0F, -5.0F);
		class_630 lv = new class_630(this, 51, 0);
		lv.method_2844(-0.5F, 0.0F, 8.0F, 1, 4, 5);
		lv.field_3654 = (float) (Math.PI / 3);
		this.field_4658.method_2845(lv);
		class_630 lv2 = new class_630(this, 48, 20);
		lv2.field_3666 = true;
		lv2.method_2844(-0.5F, -4.0F, 0.0F, 1, 4, 7);
		lv2.method_2851(2.0F, -2.0F, 4.0F);
		lv2.field_3654 = (float) (Math.PI / 3);
		lv2.field_3674 = (float) (Math.PI * 2.0 / 3.0);
		this.field_4658.method_2845(lv2);
		class_630 lv3 = new class_630(this, 48, 20);
		lv3.method_2844(-0.5F, -4.0F, 0.0F, 1, 4, 7);
		lv3.method_2851(-2.0F, -2.0F, 4.0F);
		lv3.field_3654 = (float) (Math.PI / 3);
		lv3.field_3674 = (float) (-Math.PI * 2.0 / 3.0);
		this.field_4658.method_2845(lv3);
		this.field_4657 = new class_630(this, 0, 19);
		this.field_4657.method_2844(-2.0F, -2.5F, 0.0F, 4, 5, 11);
		this.field_4657.method_2851(0.0F, -2.5F, 11.0F);
		this.field_4657.field_3654 = -0.10471976F;
		this.field_4658.method_2845(this.field_4657);
		this.field_4655 = new class_630(this, 19, 20);
		this.field_4655.method_2844(-5.0F, -0.5F, 0.0F, 10, 1, 6);
		this.field_4655.method_2851(0.0F, 0.0F, 9.0F);
		this.field_4655.field_3654 = 0.0F;
		this.field_4657.method_2845(this.field_4655);
		this.field_4656 = new class_630(this, 0, 0);
		this.field_4656.method_2844(-4.0F, -3.0F, -3.0F, 8, 7, 6);
		this.field_4656.method_2851(0.0F, -4.0F, -3.0F);
		class_630 lv4 = new class_630(this, 0, 13);
		lv4.method_2844(-1.0F, 2.0F, -7.0F, 2, 2, 4);
		this.field_4656.method_2845(lv4);
		this.field_4658.method_2845(this.field_4656);
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.field_4658.method_2846(k);
	}

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		this.field_4658.field_3654 = j * (float) (Math.PI / 180.0);
		this.field_4658.field_3675 = i * (float) (Math.PI / 180.0);
		if (arg.field_5967 != 0.0 || arg.field_6006 != 0.0) {
			this.field_4658.field_3654 = this.field_4658.field_3654 + -0.05F + -0.05F * class_3532.method_15362(h * 0.3F);
			this.field_4657.field_3654 = -0.1F * class_3532.method_15362(h * 0.3F);
			this.field_4655.field_3654 = -0.2F * class_3532.method_15362(h * 0.3F);
		}
	}
}
