package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_694 extends class_703 {
	private final float field_3827;

	protected class_694(class_1937 arg, double d, double e, double f) {
		super(arg, d, e, f, 0.0, 0.0, 0.0);
		this.field_3852 *= 0.8F;
		this.field_3869 *= 0.8F;
		this.field_3850 *= 0.8F;
		this.field_3869 = (double)(this.field_3840.nextFloat() * 0.4F + 0.05F);
		this.field_3861 = 1.0F;
		this.field_3842 = 1.0F;
		this.field_3859 = 1.0F;
		this.field_3863 = this.field_3863 * (this.field_3840.nextFloat() * 2.0F + 0.2F);
		this.field_3827 = this.field_3863;
		this.field_3847 = (int)(16.0 / (Math.random() * 0.8 + 0.2));
		this.method_3076(49);
	}

	@Override
	public int method_3068(float f) {
		int i = super.method_3068(f);
		int j = 240;
		int k = i >> 16 & 0xFF;
		return 240 | k << 16;
	}

	@Override
	public void method_3074(class_287 arg, class_1297 arg2, float f, float g, float h, float i, float j, float k) {
		float l = ((float)this.field_3866 + f) / (float)this.field_3847;
		this.field_3863 = this.field_3827 * (1.0F - l * l);
		super.method_3074(arg, arg2, f, g, h, i, j, k);
	}

	@Override
	public void method_3070() {
		this.field_3858 = this.field_3874;
		this.field_3838 = this.field_3854;
		this.field_3856 = this.field_3871;
		if (this.field_3866++ >= this.field_3847) {
			this.method_3085();
		}

		float f = (float)this.field_3866 / (float)this.field_3847;
		if (this.field_3840.nextFloat() > f) {
			this.field_3851.method_8406(class_2398.field_11251, this.field_3874, this.field_3854, this.field_3871, this.field_3852, this.field_3869, this.field_3850);
		}

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

	@Environment(EnvType.CLIENT)
	public static class class_695 implements class_707<class_2400> {
		public class_703 method_3039(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_694(arg2, d, e, f);
		}
	}
}
