package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_687 extends class_703 {
	private final float field_3812;

	protected class_687(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(arg, d, e, f, g, h, i);
		this.field_3852 = this.field_3852 * 0.01F + g;
		this.field_3869 = this.field_3869 * 0.01F + h;
		this.field_3850 = this.field_3850 * 0.01F + i;
		this.field_3874 = this.field_3874 + (double)((this.field_3840.nextFloat() - this.field_3840.nextFloat()) * 0.05F);
		this.field_3854 = this.field_3854 + (double)((this.field_3840.nextFloat() - this.field_3840.nextFloat()) * 0.05F);
		this.field_3871 = this.field_3871 + (double)((this.field_3840.nextFloat() - this.field_3840.nextFloat()) * 0.05F);
		this.field_3812 = this.field_3863;
		this.field_3861 = 1.0F;
		this.field_3842 = 1.0F;
		this.field_3859 = 1.0F;
		this.field_3847 = (int)(8.0 / (Math.random() * 0.8 + 0.2)) + 4;
		this.method_3076(48);
	}

	@Override
	public void method_3069(double d, double e, double f) {
		this.method_3067(this.method_3064().method_989(d, e, f));
		this.method_3072();
	}

	@Override
	public void method_3074(class_287 arg, class_1297 arg2, float f, float g, float h, float i, float j, float k) {
		float l = ((float)this.field_3866 + f) / (float)this.field_3847;
		this.field_3863 = this.field_3812 * (1.0F - l * l * 0.5F);
		super.method_3074(arg, arg2, f, g, h, i, j, k);
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
		}

		this.method_3069(this.field_3852, this.field_3869, this.field_3850);
		this.field_3852 *= 0.96F;
		this.field_3869 *= 0.96F;
		this.field_3850 *= 0.96F;
		if (this.field_3845) {
			this.field_3852 *= 0.7F;
			this.field_3850 *= 0.7F;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_688 implements class_707<class_2400> {
		public class_703 method_3036(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_687(arg2, d, e, f, g, h, i);
		}
	}
}
