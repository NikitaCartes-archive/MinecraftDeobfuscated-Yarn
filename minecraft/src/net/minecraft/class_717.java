package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_717 extends class_703 {
	private final float field_3890;

	private class_717(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		this(arg, d, e, f, g, h, i, 1.0F);
	}

	protected class_717(class_1937 arg, double d, double e, double f, double g, double h, double i, float j) {
		super(arg, d, e, f, 0.0, 0.0, 0.0);
		this.field_3852 *= 0.1F;
		this.field_3869 *= 0.1F;
		this.field_3850 *= 0.1F;
		this.field_3852 += g;
		this.field_3869 += h;
		this.field_3850 += i;
		float k = (float)(Math.random() * 0.3F);
		this.field_3861 = k;
		this.field_3842 = k;
		this.field_3859 = k;
		this.field_3863 *= 0.75F;
		this.field_3863 *= j;
		this.field_3890 = this.field_3863;
		this.field_3847 = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.field_3847 = (int)((float)this.field_3847 * j);
		this.field_3847 = Math.max(this.field_3847, 1);
	}

	@Override
	public void method_3074(class_287 arg, class_1297 arg2, float f, float g, float h, float i, float j, float k) {
		float l = ((float)this.field_3866 + f) / (float)this.field_3847 * 32.0F;
		l = class_3532.method_15363(l, 0.0F, 1.0F);
		this.field_3863 = this.field_3890 * l;
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

		this.method_3076(7 - this.field_3866 * 8 / this.field_3847);
		this.field_3869 += 0.004;
		this.method_3069(this.field_3852, this.field_3869, this.field_3850);
		if (this.field_3854 == this.field_3838) {
			this.field_3852 *= 1.1;
			this.field_3850 *= 1.1;
		}

		this.field_3852 *= 0.96F;
		this.field_3869 *= 0.96F;
		this.field_3850 *= 0.96F;
		if (this.field_3845) {
			this.field_3852 *= 0.7F;
			this.field_3850 *= 0.7F;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_718 implements class_707<class_2400> {
		public class_703 method_3101(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_717(arg2, d, e, f, g, h, i);
		}
	}
}
