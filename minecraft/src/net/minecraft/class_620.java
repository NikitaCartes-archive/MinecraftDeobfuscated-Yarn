package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_620<T extends class_1297> extends class_583<T> implements class_3882, class_3884 {
	protected final class_630 field_3608;
	protected class_630 field_17141;
	protected final class_630 field_17142;
	protected final class_630 field_3610;
	protected final class_630 field_17143;
	protected final class_630 field_3609;
	protected final class_630 field_3607;
	protected final class_630 field_3606;
	protected final class_630 field_3611;

	public class_620(float f) {
		this(f, 64, 64);
	}

	public class_620(float f, int i, int j) {
		float g = 0.5F;
		this.field_3608 = new class_630(this).method_2853(i, j);
		this.field_3608.method_2851(0.0F, 0.0F, 0.0F);
		this.field_3608.method_2850(0, 0).method_2856(-4.0F, -10.0F, -4.0F, 8, 10, 8, f);
		this.field_17141 = new class_630(this).method_2853(i, j);
		this.field_17141.method_2851(0.0F, 0.0F, 0.0F);
		this.field_17141.method_2850(32, 0).method_2856(-4.0F, -10.0F, -4.0F, 8, 10, 8, f + 0.5F);
		this.field_3608.method_2845(this.field_17141);
		this.field_17142 = new class_630(this).method_2853(i, j);
		this.field_17142.method_2851(0.0F, 0.0F, 0.0F);
		this.field_17142.method_2850(30, 47).method_2856(-8.0F, -8.0F, -6.0F, 16, 16, 1, f);
		this.field_17142.field_3654 = (float) (-Math.PI / 2);
		this.field_17141.method_2845(this.field_17142);
		this.field_3611 = new class_630(this).method_2853(i, j);
		this.field_3611.method_2851(0.0F, -2.0F, 0.0F);
		this.field_3611.method_2850(24, 0).method_2856(-1.0F, -1.0F, -6.0F, 2, 4, 2, f);
		this.field_3608.method_2845(this.field_3611);
		this.field_3610 = new class_630(this).method_2853(i, j);
		this.field_3610.method_2851(0.0F, 0.0F, 0.0F);
		this.field_3610.method_2850(16, 20).method_2856(-4.0F, 0.0F, -3.0F, 8, 12, 6, f);
		this.field_17143 = new class_630(this).method_2853(i, j);
		this.field_17143.method_2851(0.0F, 0.0F, 0.0F);
		this.field_17143.method_2850(0, 38).method_2856(-4.0F, 0.0F, -3.0F, 8, 18, 6, f + 0.5F);
		this.field_3610.method_2845(this.field_17143);
		this.field_3609 = new class_630(this).method_2853(i, j);
		this.field_3609.method_2851(0.0F, 2.0F, 0.0F);
		this.field_3609.method_2850(44, 22).method_2856(-8.0F, -2.0F, -2.0F, 4, 8, 4, f);
		this.field_3609.method_2850(44, 22).method_2849(4.0F, -2.0F, -2.0F, 4, 8, 4, f, true);
		this.field_3609.method_2850(40, 38).method_2856(-4.0F, 2.0F, -2.0F, 8, 4, 4, f);
		this.field_3607 = new class_630(this, 0, 22).method_2853(i, j);
		this.field_3607.method_2851(-2.0F, 12.0F, 0.0F);
		this.field_3607.method_2856(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.field_3606 = new class_630(this, 0, 22).method_2853(i, j);
		this.field_3606.field_3666 = true;
		this.field_3606.method_2851(2.0F, 12.0F, 0.0F);
		this.field_3606.method_2856(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);
		this.field_3608.method_2846(k);
		this.field_3610.method_2846(k);
		this.field_3607.method_2846(k);
		this.field_3606.method_2846(k);
		this.field_3609.method_2846(k);
	}

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		boolean bl = false;
		if (arg instanceof class_3988) {
			bl = ((class_3988)arg).method_20506() > 0;
		}

		this.field_3608.field_3675 = i * (float) (Math.PI / 180.0);
		this.field_3608.field_3654 = j * (float) (Math.PI / 180.0);
		if (bl) {
			this.field_3608.field_3674 = 0.3F * class_3532.method_15374(0.45F * h);
			this.field_3608.field_3654 = 0.4F;
		} else {
			this.field_3608.field_3674 = 0.0F;
		}

		this.field_3609.field_3656 = 3.0F;
		this.field_3609.field_3655 = -1.0F;
		this.field_3609.field_3654 = -0.75F;
		this.field_3607.field_3654 = class_3532.method_15362(f * 0.6662F) * 1.4F * g * 0.5F;
		this.field_3606.field_3654 = class_3532.method_15362(f * 0.6662F + (float) Math.PI) * 1.4F * g * 0.5F;
		this.field_3607.field_3675 = 0.0F;
		this.field_3606.field_3675 = 0.0F;
	}

	@Override
	public class_630 method_2838() {
		return this.field_3608;
	}

	@Override
	public void method_17150(boolean bl) {
		this.field_3608.field_3665 = bl;
		this.field_17141.field_3665 = bl;
		this.field_17142.field_3665 = bl;
	}
}
