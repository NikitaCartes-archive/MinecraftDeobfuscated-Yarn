package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_687 extends class_4003 {
	private class_687(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(arg, d, e, f, g, h, i);
		this.field_3852 = this.field_3852 * 0.01F + g;
		this.field_3869 = this.field_3869 * 0.01F + h;
		this.field_3850 = this.field_3850 * 0.01F + i;
		this.field_3874 = this.field_3874 + (double)((this.field_3840.nextFloat() - this.field_3840.nextFloat()) * 0.05F);
		this.field_3854 = this.field_3854 + (double)((this.field_3840.nextFloat() - this.field_3840.nextFloat()) * 0.05F);
		this.field_3871 = this.field_3871 + (double)((this.field_3840.nextFloat() - this.field_3840.nextFloat()) * 0.05F);
		this.field_3847 = (int)(8.0 / (Math.random() * 0.8 + 0.2)) + 4;
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17828;
	}

	@Override
	public void method_3069(double d, double e, double f) {
		this.method_3067(this.method_3064().method_989(d, e, f));
		this.method_3072();
	}

	@Override
	public float method_18132(float f) {
		float g = ((float)this.field_3866 + f) / (float)this.field_3847;
		return this.field_17867 * (1.0F - g * g * 0.5F);
	}

	@Override
	public int method_3068(float f) {
		float g = ((float)this.field_3866 + f) / (float)this.field_3847;
		g = class_3532.method_15363(g, 0.0F, 1.0F);
		int i = super.method_3068(f);
		int j = i & 0xFF;
		int k = i >> 16 & 0xFF;
		j += (int)(g * 15.0F * 16.0F);
		if (j > 240) {
			j = 240;
		}

		return j | k << 16;
	}

	@Override
	public void method_3070() {
		this.field_3858 = this.field_3874;
		this.field_3838 = this.field_3854;
		this.field_3856 = this.field_3871;
		if (this.field_3866++ >= this.field_3847) {
			this.method_3085();
		} else {
			this.method_3069(this.field_3852, this.field_3869, this.field_3850);
			this.field_3852 *= 0.96F;
			this.field_3869 *= 0.96F;
			this.field_3850 *= 0.96F;
			if (this.field_3845) {
				this.field_3852 *= 0.7F;
				this.field_3850 *= 0.7F;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_688 implements class_707<class_2400> {
		private final class_4002 field_17812;

		public class_688(class_4002 arg) {
			this.field_17812 = arg;
		}

		public class_703 method_3036(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_687 lv = new class_687(arg2, d, e, f, g, h, i);
			lv.method_18140(this.field_17812);
			return lv;
		}
	}
}
