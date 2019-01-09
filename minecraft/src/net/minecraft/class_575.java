package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_575<T extends class_1543> extends class_583<T> implements class_3881, class_3882 {
	protected final class_630 field_3422;
	private final class_630 field_3419;
	protected final class_630 field_3425;
	protected final class_630 field_3423;
	protected final class_630 field_3420;
	protected final class_630 field_3418;
	private final class_630 field_3421;
	protected final class_630 field_3426;
	protected final class_630 field_3417;
	private float field_3424;

	public class_575(float f, float g, int i, int j) {
		this.field_3422 = new class_630(this).method_2853(i, j);
		this.field_3422.method_2851(0.0F, 0.0F + g, 0.0F);
		this.field_3422.method_2850(0, 0).method_2856(-4.0F, -10.0F, -4.0F, 8, 10, 8, f);
		this.field_3419 = new class_630(this, 32, 0).method_2853(i, j);
		this.field_3419.method_2856(-4.0F, -10.0F, -4.0F, 8, 12, 8, f + 0.45F);
		this.field_3422.method_2845(this.field_3419);
		this.field_3419.field_3665 = false;
		this.field_3421 = new class_630(this).method_2853(i, j);
		this.field_3421.method_2851(0.0F, g - 2.0F, 0.0F);
		this.field_3421.method_2850(24, 0).method_2856(-1.0F, -1.0F, -6.0F, 2, 4, 2, f);
		this.field_3422.method_2845(this.field_3421);
		this.field_3425 = new class_630(this).method_2853(i, j);
		this.field_3425.method_2851(0.0F, 0.0F + g, 0.0F);
		this.field_3425.method_2850(16, 20).method_2856(-4.0F, 0.0F, -3.0F, 8, 12, 6, f);
		this.field_3425.method_2850(0, 38).method_2856(-4.0F, 0.0F, -3.0F, 8, 18, 6, f + 0.5F);
		this.field_3423 = new class_630(this).method_2853(i, j);
		this.field_3423.method_2851(0.0F, 0.0F + g + 2.0F, 0.0F);
		this.field_3423.method_2850(44, 22).method_2856(-8.0F, -2.0F, -2.0F, 4, 8, 4, f);
		class_630 lv = new class_630(this, 44, 22).method_2853(i, j);
		lv.field_3666 = true;
		lv.method_2856(4.0F, -2.0F, -2.0F, 4, 8, 4, f);
		this.field_3423.method_2845(lv);
		this.field_3423.method_2850(40, 38).method_2856(-4.0F, 2.0F, -2.0F, 8, 4, 4, f);
		this.field_3420 = new class_630(this, 0, 22).method_2853(i, j);
		this.field_3420.method_2851(-2.0F, 12.0F + g, 0.0F);
		this.field_3420.method_2856(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.field_3418 = new class_630(this, 0, 22).method_2853(i, j);
		this.field_3418.field_3666 = true;
		this.field_3418.method_2851(2.0F, 12.0F + g, 0.0F);
		this.field_3418.method_2856(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.field_3426 = new class_630(this, 40, 46).method_2853(i, j);
		this.field_3426.method_2856(-3.0F, -2.0F, -2.0F, 4, 12, 4, f);
		this.field_3426.method_2851(-5.0F, 2.0F + g, 0.0F);
		this.field_3417 = new class_630(this, 40, 46).method_2853(i, j);
		this.field_3417.field_3666 = true;
		this.field_3417.method_2856(-1.0F, -2.0F, -2.0F, 4, 12, 4, f);
		this.field_3417.method_2851(5.0F, 2.0F + g, 0.0F);
	}

	public void method_17093(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17094(arg, f, g, h, i, j, k);
		this.field_3422.method_2846(k);
		this.field_3425.method_2846(k);
		this.field_3420.method_2846(k);
		this.field_3418.method_2846(k);
		if (arg.method_6990() == class_1543.class_1544.field_7207) {
			this.field_3423.method_2846(k);
		} else {
			this.field_3426.method_2846(k);
			this.field_3417.method_2846(k);
		}
	}

	public void method_17094(T arg, float f, float g, float h, float i, float j, float k) {
		this.field_3422.field_3675 = i * (float) (Math.PI / 180.0);
		this.field_3422.field_3654 = j * (float) (Math.PI / 180.0);
		this.field_3423.field_3656 = 3.0F;
		this.field_3423.field_3655 = -1.0F;
		this.field_3423.field_3654 = -0.75F;
		if (this.field_3449) {
			this.field_3426.field_3654 = (float) (-Math.PI / 5);
			this.field_3426.field_3675 = 0.0F;
			this.field_3426.field_3674 = 0.0F;
			this.field_3417.field_3654 = (float) (-Math.PI / 5);
			this.field_3417.field_3675 = 0.0F;
			this.field_3417.field_3674 = 0.0F;
			this.field_3420.field_3654 = -1.4137167F;
			this.field_3420.field_3675 = (float) (Math.PI / 10);
			this.field_3420.field_3674 = 0.07853982F;
			this.field_3418.field_3654 = -1.4137167F;
			this.field_3418.field_3675 = (float) (-Math.PI / 10);
			this.field_3418.field_3674 = -0.07853982F;
		} else {
			this.field_3426.field_3654 = class_3532.method_15362(f * 0.6662F + (float) Math.PI) * 2.0F * g * 0.5F;
			this.field_3426.field_3675 = 0.0F;
			this.field_3426.field_3674 = 0.0F;
			this.field_3417.field_3654 = class_3532.method_15362(f * 0.6662F) * 2.0F * g * 0.5F;
			this.field_3417.field_3675 = 0.0F;
			this.field_3417.field_3674 = 0.0F;
			this.field_3420.field_3654 = class_3532.method_15362(f * 0.6662F) * 1.4F * g * 0.5F;
			this.field_3420.field_3675 = 0.0F;
			this.field_3420.field_3674 = 0.0F;
			this.field_3418.field_3654 = class_3532.method_15362(f * 0.6662F + (float) Math.PI) * 1.4F * g * 0.5F;
			this.field_3418.field_3675 = 0.0F;
			this.field_3418.field_3674 = 0.0F;
		}

		class_1543.class_1544 lv = arg.method_6990();
		if (lv == class_1543.class_1544.field_7211) {
			float l = class_3532.method_15374(this.field_3447 * (float) Math.PI);
			float m = class_3532.method_15374((1.0F - (1.0F - this.field_3447) * (1.0F - this.field_3447)) * (float) Math.PI);
			this.field_3426.field_3674 = 0.0F;
			this.field_3417.field_3674 = 0.0F;
			this.field_3426.field_3675 = (float) (Math.PI / 20);
			this.field_3417.field_3675 = (float) (-Math.PI / 20);
			if (arg.method_6068() == class_1306.field_6183) {
				this.field_3426.field_3654 = -1.8849558F + class_3532.method_15362(h * 0.09F) * 0.15F;
				this.field_3417.field_3654 = -0.0F + class_3532.method_15362(h * 0.19F) * 0.5F;
				this.field_3426.field_3654 += l * 2.2F - m * 0.4F;
				this.field_3417.field_3654 += l * 1.2F - m * 0.4F;
			} else {
				this.field_3426.field_3654 = -0.0F + class_3532.method_15362(h * 0.19F) * 0.5F;
				this.field_3417.field_3654 = -1.8849558F + class_3532.method_15362(h * 0.09F) * 0.15F;
				this.field_3426.field_3654 += l * 1.2F - m * 0.4F;
				this.field_3417.field_3654 += l * 2.2F - m * 0.4F;
			}

			this.field_3426.field_3674 = this.field_3426.field_3674 + class_3532.method_15362(h * 0.09F) * 0.05F + 0.05F;
			this.field_3417.field_3674 = this.field_3417.field_3674 - (class_3532.method_15362(h * 0.09F) * 0.05F + 0.05F);
			this.field_3426.field_3654 = this.field_3426.field_3654 + class_3532.method_15374(h * 0.067F) * 0.05F;
			this.field_3417.field_3654 = this.field_3417.field_3654 - class_3532.method_15374(h * 0.067F) * 0.05F;
		} else if (lv == class_1543.class_1544.field_7212) {
			this.field_3426.field_3655 = 0.0F;
			this.field_3426.field_3657 = -5.0F;
			this.field_3417.field_3655 = 0.0F;
			this.field_3417.field_3657 = 5.0F;
			this.field_3426.field_3654 = class_3532.method_15362(h * 0.6662F) * 0.25F;
			this.field_3417.field_3654 = class_3532.method_15362(h * 0.6662F) * 0.25F;
			this.field_3426.field_3674 = (float) (Math.PI * 3.0 / 4.0);
			this.field_3417.field_3674 = (float) (-Math.PI * 3.0 / 4.0);
			this.field_3426.field_3675 = 0.0F;
			this.field_3417.field_3675 = 0.0F;
		} else if (lv == class_1543.class_1544.field_7208) {
			this.field_3426.field_3675 = -0.1F + this.field_3422.field_3675;
			this.field_3426.field_3654 = (float) (-Math.PI / 2) + this.field_3422.field_3654;
			this.field_3417.field_3654 = -0.9424779F + this.field_3422.field_3654;
			this.field_3417.field_3675 = this.field_3422.field_3675 - 0.4F;
			this.field_3417.field_3674 = (float) (Math.PI / 2);
		} else if (lv == class_1543.class_1544.field_7213) {
			this.field_3426.field_3675 = -0.3F + this.field_3422.field_3675;
			this.field_3417.field_3675 = 0.6F + this.field_3422.field_3675;
			this.field_3426.field_3654 = (float) (-Math.PI / 2) + this.field_3422.field_3654 + 0.1F;
			this.field_3417.field_3654 = -1.5F + this.field_3422.field_3654;
		} else if (lv == class_1543.class_1544.field_7210) {
			this.field_3426.field_3675 = -0.8F;
			this.field_3426.field_3654 = -0.97079635F;
			this.field_3417.field_3654 = -0.97079635F;
			float l = class_3532.method_15363(this.field_3424, 0.0F, 25.0F);
			this.field_3417.field_3675 = class_3532.method_16439(l / 25.0F, 0.4F, 0.85F);
			this.field_3417.field_3654 = class_3532.method_16439(l / 25.0F, this.field_3417.field_3654, (float) (-Math.PI / 2));
		}
	}

	public void method_17092(T arg, float f, float g, float h) {
		this.field_3424 = (float)arg.method_6048();
		super.method_2816(arg, f, g, h);
	}

	private class_630 method_2813(class_1306 arg) {
		return arg == class_1306.field_6182 ? this.field_3417 : this.field_3426;
	}

	public class_630 method_2812() {
		return this.field_3419;
	}

	@Override
	public class_630 method_2838() {
		return this.field_3422;
	}

	@Override
	public void method_2803(float f, class_1306 arg) {
		this.method_2813(arg).method_2847(0.0625F);
	}
}
