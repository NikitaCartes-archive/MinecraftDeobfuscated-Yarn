package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_725 extends class_708 {
	protected class_725(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(arg, d, e, f, 0, 8, 0.0F);
		this.field_3863 = 5.0F;
		this.method_3083(1.0F);
		this.method_3084(0.0F, 0.0F, 0.0F);
		this.method_3076(0);
		this.field_3847 = (int)((double)(this.field_3863 * 12.0F) / (Math.random() * 0.8F + 0.2F));
		this.field_3862 = false;
		this.field_3852 = g;
		this.field_3869 = h;
		this.field_3850 = i;
		this.method_3091(0.0F);
	}

	@Override
	public void method_3070() {
		this.field_3858 = this.field_3874;
		this.field_3838 = this.field_3854;
		this.field_3856 = this.field_3871;
		if (this.field_3866++ >= this.field_3847) {
			this.method_3085();
		}

		if (this.field_3866 > this.field_3847 / 2) {
			this.method_3083(1.0F - ((float)this.field_3866 - (float)(this.field_3847 / 2)) / (float)this.field_3847);
		}

		this.method_3076(this.field_3883 + this.field_3882 - 1 - this.field_3866 * this.field_3882 / this.field_3847);
		this.method_3069(this.field_3852, this.field_3869, this.field_3850);
		if (this.field_3851.method_8320(new class_2338(this.field_3874, this.field_3854, this.field_3871)).method_11588()) {
			this.field_3869 -= 0.008F;
		}

		this.field_3852 *= 0.92F;
		this.field_3869 *= 0.92F;
		this.field_3850 *= 0.92F;
		if (this.field_3845) {
			this.field_3852 *= 0.7F;
			this.field_3850 *= 0.7F;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_726 implements class_707<class_2400> {
		public class_703 method_3105(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_725(arg2, d, e, f, g, h, i);
		}
	}
}
