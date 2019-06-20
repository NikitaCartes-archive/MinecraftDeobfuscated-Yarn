package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_709 extends class_4003 {
	private final double field_3886;
	private final double field_3885;
	private final double field_3884;

	private class_709(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(arg, d, e, f);
		this.field_3852 = g;
		this.field_3869 = h;
		this.field_3850 = i;
		this.field_3874 = d;
		this.field_3854 = e;
		this.field_3871 = f;
		this.field_3886 = this.field_3874;
		this.field_3885 = this.field_3854;
		this.field_3884 = this.field_3871;
		this.field_17867 = 0.1F * (this.field_3840.nextFloat() * 0.2F + 0.5F);
		float j = this.field_3840.nextFloat() * 0.6F + 0.4F;
		this.field_3861 = j * 0.9F;
		this.field_3842 = j * 0.3F;
		this.field_3859 = j;
		this.field_3847 = (int)(Math.random() * 10.0) + 40;
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
		g = 1.0F - g;
		g *= g;
		g = 1.0F - g;
		return this.field_17867 * g;
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
		if (this.field_3866++ >= this.field_3847) {
			this.method_3085();
		} else {
			float f = (float)this.field_3866 / (float)this.field_3847;
			float var3 = -f + f * f * 2.0F;
			float var4 = 1.0F - var3;
			this.field_3874 = this.field_3886 + this.field_3852 * (double)var4;
			this.field_3854 = this.field_3885 + this.field_3869 * (double)var4 + (double)(1.0F - f);
			this.field_3871 = this.field_3884 + this.field_3850 * (double)var4;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_710 implements class_707<class_2400> {
		private final class_4002 field_17865;

		public class_710(class_4002 arg) {
			this.field_17865 = arg;
		}

		public class_703 method_3094(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_709 lv = new class_709(arg2, d, e, f, g, h, i);
			lv.method_18140(this.field_17865);
			return lv;
		}
	}
}
