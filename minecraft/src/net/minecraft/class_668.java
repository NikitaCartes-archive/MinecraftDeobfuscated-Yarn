package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_668 extends class_703 {
	private final double field_3795;
	private final double field_3794;
	private final double field_3793;

	protected class_668(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(arg, d, e, f, g, h, i);
		this.field_3852 = g;
		this.field_3869 = h;
		this.field_3850 = i;
		this.field_3795 = d;
		this.field_3794 = e;
		this.field_3793 = f;
		this.field_3858 = d + g;
		this.field_3838 = e + h;
		this.field_3856 = f + i;
		this.field_3874 = this.field_3858;
		this.field_3854 = this.field_3838;
		this.field_3871 = this.field_3856;
		float j = this.field_3840.nextFloat() * 0.6F + 0.4F;
		this.field_3863 = this.field_3840.nextFloat() * 0.5F + 0.2F;
		this.field_3861 = 0.9F * j;
		this.field_3842 = 0.9F * j;
		this.field_3859 = j;
		this.field_3862 = false;
		this.field_3847 = (int)(Math.random() * 10.0) + 30;
		this.method_3076((int)(Math.random() * 26.0 + 1.0 + 224.0));
	}

	@Override
	public void method_3069(double d, double e, double f) {
		this.method_3067(this.method_3064().method_989(d, e, f));
		this.method_3072();
	}

	@Override
	public int method_3068(float f) {
		int i = super.method_3068(f);
		float g = (float)this.field_3866 / (float)this.field_3847;
		g *= g;
		g *= g;
		int j = i & 0xFF;
		int k = i >> 16 & 0xFF;
		k += (int)(g * 15.0F * 16.0F);
		if (k > 240) {
			k = 240;
		}

		return j | k << 16;
	}

	@Override
	public void method_3070() {
		this.field_3858 = this.field_3874;
		this.field_3838 = this.field_3854;
		this.field_3856 = this.field_3871;
		float f = (float)this.field_3866 / (float)this.field_3847;
		f = 1.0F - f;
		float g = 1.0F - f;
		g *= g;
		g *= g;
		this.field_3874 = this.field_3795 + this.field_3852 * (double)f;
		this.field_3854 = this.field_3794 + this.field_3869 * (double)f - (double)(g * 1.2F);
		this.field_3871 = this.field_3793 + this.field_3850 * (double)f;
		if (this.field_3866++ >= this.field_3847) {
			this.method_3085();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_669 implements class_707<class_2400> {
		public class_703 method_3020(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_668 lv = new class_668(arg2, d, e, f, g, h, i);
			lv.method_3076(208);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_670 implements class_707<class_2400> {
		public class_703 method_3021(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_668(arg2, d, e, f, g, h, i);
		}
	}
}
