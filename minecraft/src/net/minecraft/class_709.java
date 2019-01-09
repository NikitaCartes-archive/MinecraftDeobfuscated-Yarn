package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_709 extends class_703 {
	private final float field_3887;
	private final double field_3886;
	private final double field_3885;
	private final double field_3884;

	protected class_709(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(arg, d, e, f, g, h, i);
		this.field_3852 = g;
		this.field_3869 = h;
		this.field_3850 = i;
		this.field_3874 = d;
		this.field_3854 = e;
		this.field_3871 = f;
		this.field_3886 = this.field_3874;
		this.field_3885 = this.field_3854;
		this.field_3884 = this.field_3871;
		float j = this.field_3840.nextFloat() * 0.6F + 0.4F;
		this.field_3863 = this.field_3840.nextFloat() * 0.2F + 0.5F;
		this.field_3887 = this.field_3863;
		this.field_3861 = j * 0.9F;
		this.field_3842 = j * 0.3F;
		this.field_3859 = j;
		this.field_3847 = (int)(Math.random() * 10.0) + 40;
		this.method_3076((int)(Math.random() * 8.0));
	}

	@Override
	public void method_3069(double d, double e, double f) {
		this.method_3067(this.method_3064().method_989(d, e, f));
		this.method_3072();
	}

	@Override
	public void method_3074(class_287 arg, class_1297 arg2, float f, float g, float h, float i, float j, float k) {
		float l = ((float)this.field_3866 + f) / (float)this.field_3847;
		l = 1.0F - l;
		l *= l;
		l = 1.0F - l;
		this.field_3863 = this.field_3887 * l;
		super.method_3074(arg, arg2, f, g, h, i, j, k);
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
		float var3 = -f + f * f * 2.0F;
		float var4 = 1.0F - var3;
		this.field_3874 = this.field_3886 + this.field_3852 * (double)var4;
		this.field_3854 = this.field_3885 + this.field_3869 * (double)var4 + (double)(1.0F - f);
		this.field_3871 = this.field_3884 + this.field_3850 * (double)var4;
		if (this.field_3866++ >= this.field_3847) {
			this.method_3085();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_710 implements class_707<class_2400> {
		public class_703 method_3094(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_709(arg2, d, e, f, g, h, i);
		}
	}
}
