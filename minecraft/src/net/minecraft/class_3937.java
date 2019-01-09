package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3937 extends class_3940 {
	private class_3937(class_1937 arg, double d, double e, double f, double g, double h, double i, boolean bl) {
		super(arg, d, e, f, g, h, i);
		this.field_3861 = 1.0F;
		this.field_3842 = 1.0F;
		this.field_3859 = 1.0F;
		this.method_3087(3.0F);
		this.method_3080(0.25F, 0.25F);
		if (bl) {
			this.field_3847 = this.field_3840.nextInt(50) + 280;
		} else {
			this.field_3847 = this.field_3840.nextInt(50) + 80;
		}

		this.field_3844 = 3.0E-6F;
		this.field_3852 = g;
		this.field_3869 = h + (double)(this.field_3840.nextFloat() / 500.0F);
		this.field_3850 = i;
		this.method_3076(304 + this.field_3840.nextInt(10));
	}

	@Override
	public void method_3070() {
		this.field_3858 = this.field_3874;
		this.field_3838 = this.field_3854;
		this.field_3856 = this.field_3871;
		this.field_3852 = this.field_3852 + (double)(this.field_3840.nextFloat() / 5000.0F * (float)(this.field_3840.nextBoolean() ? 1 : -1));
		this.field_3850 = this.field_3850 + (double)(this.field_3840.nextFloat() / 5000.0F * (float)(this.field_3840.nextBoolean() ? 1 : -1));
		this.field_3869 = this.field_3869 - (double)this.field_3844;
		this.method_3069(this.field_3852, this.field_3869, this.field_3850);
		if (this.field_3866++ < this.field_3847 && !(this.field_3841 <= 0.0F)) {
			if (this.field_3866 >= this.field_3847 - 60 && this.field_3841 > 0.01F) {
				this.field_3841 -= 0.015F;
			}
		} else {
			this.method_3085();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_3938 implements class_707<class_2400> {
		public class_703 method_17579(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_703 lv = new class_3937(arg2, d, e, f, g, h, i, false);
			lv.method_3083(0.9F);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_3939 implements class_707<class_2400> {
		public class_703 method_17580(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_703 lv = new class_3937(arg2, d, e, f, g, h, i, true);
			lv.method_3083(0.95F);
			return lv;
		}
	}
}
