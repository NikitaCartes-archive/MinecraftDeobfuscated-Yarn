package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_684 extends class_703 {
	private final float field_3811;

	protected class_684(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		this(arg, d, e, f, g, h, i, 2.0F);
	}

	protected class_684(class_1937 arg, double d, double e, double f, double g, double h, double i, float j) {
		super(arg, d, e, f, 0.0, 0.0, 0.0);
		this.field_3852 *= 0.01F;
		this.field_3869 *= 0.01F;
		this.field_3850 *= 0.01F;
		this.field_3869 += 0.1;
		this.field_3863 *= 0.75F;
		this.field_3863 *= j;
		this.field_3811 = this.field_3863;
		this.field_3847 = 16;
		this.method_3076(80);
		this.field_3862 = false;
	}

	@Override
	public void method_3074(class_287 arg, class_1297 arg2, float f, float g, float h, float i, float j, float k) {
		float l = ((float)this.field_3866 + f) / (float)this.field_3847 * 32.0F;
		l = class_3532.method_15363(l, 0.0F, 1.0F);
		this.field_3863 = this.field_3811 * l;
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

		this.method_3069(this.field_3852, this.field_3869, this.field_3850);
		if (this.field_3854 == this.field_3838) {
			this.field_3852 *= 1.1;
			this.field_3850 *= 1.1;
		}

		this.field_3852 *= 0.86F;
		this.field_3869 *= 0.86F;
		this.field_3850 *= 0.86F;
		if (this.field_3845) {
			this.field_3852 *= 0.7F;
			this.field_3850 *= 0.7F;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_685 implements class_707<class_2400> {
		public class_703 method_3034(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_703 lv = new class_684(arg2, d, e + 0.5, f, g, h, i);
			lv.method_3076(81);
			lv.method_3084(1.0F, 1.0F, 1.0F);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_686 implements class_707<class_2400> {
		public class_703 method_3035(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_684(arg2, d, e, f, g, h, i);
		}
	}
}
