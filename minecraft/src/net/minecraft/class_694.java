package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_694 extends class_4003 {
	private class_694(class_1937 arg, double d, double e, double f) {
		super(arg, d, e, f, 0.0, 0.0, 0.0);
		this.field_3852 *= 0.8F;
		this.field_3869 *= 0.8F;
		this.field_3850 *= 0.8F;
		this.field_3869 = (double)(this.field_3840.nextFloat() * 0.4F + 0.05F);
		this.field_17867 = this.field_17867 * (this.field_3840.nextFloat() * 2.0F + 0.2F);
		this.field_3847 = (int)(16.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17828;
	}

	@Override
	public int method_3068(float f) {
		int i = super.method_3068(f);
		int j = 240;
		int k = i >> 16 & 0xFF;
		return 240 | k << 16;
	}

	@Override
	public float method_18132(float f) {
		float g = ((float)this.field_3866 + f) / (float)this.field_3847;
		return this.field_17867 * (1.0F - g * g);
	}

	@Override
	public void method_3070() {
		this.field_3858 = this.field_3874;
		this.field_3838 = this.field_3854;
		this.field_3856 = this.field_3871;
		float f = (float)this.field_3866 / (float)this.field_3847;
		if (this.field_3840.nextFloat() > f) {
			this.field_3851.method_8406(class_2398.field_11251, this.field_3874, this.field_3854, this.field_3871, this.field_3852, this.field_3869, this.field_3850);
		}

		if (this.field_3866++ >= this.field_3847) {
			this.method_3085();
		} else {
			this.field_3869 -= 0.03;
			this.method_3069(this.field_3852, this.field_3869, this.field_3850);
			this.field_3852 *= 0.999F;
			this.field_3869 *= 0.999F;
			this.field_3850 *= 0.999F;
			if (this.field_3845) {
				this.field_3852 *= 0.7F;
				this.field_3850 *= 0.7F;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_695 implements class_707<class_2400> {
		private final class_4002 field_17818;

		public class_695(class_4002 arg) {
			this.field_17818 = arg;
		}

		public class_703 method_3039(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_694 lv = new class_694(arg2, d, e, f);
			lv.method_18140(this.field_17818);
			return lv;
		}
	}
}
