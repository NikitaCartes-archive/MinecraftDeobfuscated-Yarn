package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_622<T extends class_1297> extends class_620<T> {
	private boolean field_3614;
	private final class_630 field_3615 = new class_630(this).method_2853(64, 128);

	public class_622(float f) {
		super(f, 64, 128);
		this.field_3615.method_2851(0.0F, -2.0F, 0.0F);
		this.field_3615.method_2850(0, 0).method_2856(0.0F, 3.0F, -6.75F, 1, 1, 1, -0.25F);
		this.field_3611.method_2845(this.field_3615);
		this.field_3608.method_17578(this.field_17141);
		this.field_17141 = new class_630(this).method_2853(64, 128);
		this.field_17141.method_2851(-5.0F, -10.03125F, -5.0F);
		this.field_17141.method_2850(0, 64).method_2844(0.0F, 0.0F, 0.0F, 10, 2, 10);
		this.field_3608.method_2845(this.field_17141);
		class_630 lv = new class_630(this).method_2853(64, 128);
		lv.method_2851(1.75F, -4.0F, 2.0F);
		lv.method_2850(0, 76).method_2844(0.0F, 0.0F, 0.0F, 7, 4, 7);
		lv.field_3654 = -0.05235988F;
		lv.field_3674 = 0.02617994F;
		this.field_17141.method_2845(lv);
		class_630 lv2 = new class_630(this).method_2853(64, 128);
		lv2.method_2851(1.75F, -4.0F, 2.0F);
		lv2.method_2850(0, 87).method_2844(0.0F, 0.0F, 0.0F, 4, 4, 4);
		lv2.field_3654 = -0.10471976F;
		lv2.field_3674 = 0.05235988F;
		lv.method_2845(lv2);
		class_630 lv3 = new class_630(this).method_2853(64, 128);
		lv3.method_2851(1.75F, -2.0F, 2.0F);
		lv3.method_2850(0, 95).method_2856(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.25F);
		lv3.field_3654 = (float) (-Math.PI / 15);
		lv3.field_3674 = 0.10471976F;
		lv2.method_2845(lv3);
	}

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		super.method_17080(arg, f, g, h, i, j, k);
		this.field_3611.field_3673 = 0.0F;
		this.field_3611.field_3671 = 0.0F;
		this.field_3611.field_3669 = 0.0F;
		float l = 0.01F * (float)(arg.method_5628() % 10);
		this.field_3611.field_3654 = class_3532.method_15374((float)arg.field_6012 * l) * 4.5F * (float) (Math.PI / 180.0);
		this.field_3611.field_3675 = 0.0F;
		this.field_3611.field_3674 = class_3532.method_15362((float)arg.field_6012 * l) * 2.5F * (float) (Math.PI / 180.0);
		if (this.field_3614) {
			this.field_3611.field_3654 = -0.9F;
			this.field_3611.field_3669 = -0.09375F;
			this.field_3611.field_3671 = 0.1875F;
		}
	}

	public class_630 method_2839() {
		return this.field_3611;
	}

	public void method_2840(boolean bl) {
		this.field_3614 = bl;
	}
}
