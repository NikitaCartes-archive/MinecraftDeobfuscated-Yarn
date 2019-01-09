package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_553 extends class_583<class_1420> {
	private final class_630 field_3321;
	private final class_630 field_3323;
	private final class_630 field_3322;
	private final class_630 field_3320;
	private final class_630 field_3319;
	private final class_630 field_3324;

	public class_553() {
		this.field_17138 = 64;
		this.field_17139 = 64;
		this.field_3321 = new class_630(this, 0, 0);
		this.field_3321.method_2844(-3.0F, -3.0F, -3.0F, 6, 6, 6);
		class_630 lv = new class_630(this, 24, 0);
		lv.method_2844(-4.0F, -6.0F, -2.0F, 3, 4, 1);
		this.field_3321.method_2845(lv);
		class_630 lv2 = new class_630(this, 24, 0);
		lv2.field_3666 = true;
		lv2.method_2844(1.0F, -6.0F, -2.0F, 3, 4, 1);
		this.field_3321.method_2845(lv2);
		this.field_3323 = new class_630(this, 0, 16);
		this.field_3323.method_2844(-3.0F, 4.0F, -3.0F, 6, 12, 6);
		this.field_3323.method_2850(0, 34).method_2844(-5.0F, 16.0F, 0.0F, 10, 6, 1);
		this.field_3322 = new class_630(this, 42, 0);
		this.field_3322.method_2844(-12.0F, 1.0F, 1.5F, 10, 16, 1);
		this.field_3319 = new class_630(this, 24, 16);
		this.field_3319.method_2851(-12.0F, 1.0F, 1.5F);
		this.field_3319.method_2844(-8.0F, 1.0F, 0.0F, 8, 12, 1);
		this.field_3320 = new class_630(this, 42, 0);
		this.field_3320.field_3666 = true;
		this.field_3320.method_2844(2.0F, 1.0F, 1.5F, 10, 16, 1);
		this.field_3324 = new class_630(this, 24, 16);
		this.field_3324.field_3666 = true;
		this.field_3324.method_2851(12.0F, 1.0F, 1.5F);
		this.field_3324.method_2844(0.0F, 1.0F, 0.0F, 8, 12, 1);
		this.field_3323.method_2845(this.field_3322);
		this.field_3323.method_2845(this.field_3320);
		this.field_3322.method_2845(this.field_3319);
		this.field_3320.method_2845(this.field_3324);
	}

	public void method_17068(class_1420 arg, float f, float g, float h, float i, float j, float k) {
		this.method_17069(arg, f, g, h, i, j, k);
		this.field_3321.method_2846(k);
		this.field_3323.method_2846(k);
	}

	public void method_17069(class_1420 arg, float f, float g, float h, float i, float j, float k) {
		if (arg.method_6450()) {
			this.field_3321.field_3654 = j * (float) (Math.PI / 180.0);
			this.field_3321.field_3675 = (float) Math.PI - i * (float) (Math.PI / 180.0);
			this.field_3321.field_3674 = (float) Math.PI;
			this.field_3321.method_2851(0.0F, -2.0F, 0.0F);
			this.field_3322.method_2851(-3.0F, 0.0F, 3.0F);
			this.field_3320.method_2851(3.0F, 0.0F, 3.0F);
			this.field_3323.field_3654 = (float) Math.PI;
			this.field_3322.field_3654 = (float) (-Math.PI / 20);
			this.field_3322.field_3675 = (float) (-Math.PI * 2.0 / 5.0);
			this.field_3319.field_3675 = -1.7278761F;
			this.field_3320.field_3654 = this.field_3322.field_3654;
			this.field_3320.field_3675 = -this.field_3322.field_3675;
			this.field_3324.field_3675 = -this.field_3319.field_3675;
		} else {
			this.field_3321.field_3654 = j * (float) (Math.PI / 180.0);
			this.field_3321.field_3675 = i * (float) (Math.PI / 180.0);
			this.field_3321.field_3674 = 0.0F;
			this.field_3321.method_2851(0.0F, 0.0F, 0.0F);
			this.field_3322.method_2851(0.0F, 0.0F, 0.0F);
			this.field_3320.method_2851(0.0F, 0.0F, 0.0F);
			this.field_3323.field_3654 = (float) (Math.PI / 4) + class_3532.method_15362(h * 0.1F) * 0.15F;
			this.field_3323.field_3675 = 0.0F;
			this.field_3322.field_3675 = class_3532.method_15362(h * 1.3F) * (float) Math.PI * 0.25F;
			this.field_3320.field_3675 = -this.field_3322.field_3675;
			this.field_3319.field_3675 = this.field_3322.field_3675 * 0.5F;
			this.field_3324.field_3675 = -this.field_3322.field_3675 * 0.5F;
		}
	}
}
