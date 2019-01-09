package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_666 extends class_703 {
	private final float field_3791;
	private boolean field_3792;

	protected class_666(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(arg, d, e, f, g, h, i);
		this.field_3852 = g;
		this.field_3869 = h;
		this.field_3850 = i;
		this.field_3861 = class_3532.method_15344(this.field_3840, 0.7176471F, 0.8745098F);
		this.field_3842 = class_3532.method_15344(this.field_3840, 0.0F, 0.0F);
		this.field_3859 = class_3532.method_15344(this.field_3840, 0.8235294F, 0.9764706F);
		this.field_3863 *= 0.75F;
		this.field_3791 = this.field_3863;
		this.field_3847 = (int)(20.0 / ((double)this.field_3840.nextFloat() * 0.8 + 0.2));
		this.field_3792 = false;
		this.field_3862 = false;
	}

	@Override
	public void method_3070() {
		this.field_3858 = this.field_3874;
		this.field_3838 = this.field_3854;
		this.field_3856 = this.field_3871;
		if (this.field_3866++ >= this.field_3847) {
			this.method_3085();
		} else {
			this.method_3076(3 * this.field_3866 / this.field_3847 + 5);
			if (this.field_3845) {
				this.field_3869 = 0.0;
				this.field_3792 = true;
			}

			if (this.field_3792) {
				this.field_3869 += 0.002;
			}

			this.method_3069(this.field_3852, this.field_3869, this.field_3850);
			if (this.field_3854 == this.field_3838) {
				this.field_3852 *= 1.1;
				this.field_3850 *= 1.1;
			}

			this.field_3852 *= 0.96F;
			this.field_3850 *= 0.96F;
			if (this.field_3792) {
				this.field_3869 *= 0.96F;
			}
		}
	}

	@Override
	public void method_3074(class_287 arg, class_1297 arg2, float f, float g, float h, float i, float j, float k) {
		this.field_3863 = this.field_3791 * class_3532.method_15363(((float)this.field_3866 + f) / (float)this.field_3847 * 32.0F, 0.0F, 1.0F);
		super.method_3074(arg, arg2, f, g, h, i, j, k);
	}

	@Environment(EnvType.CLIENT)
	public static class class_667 implements class_707<class_2400> {
		public class_703 method_3019(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_666(arg2, d, e, f, g, h, i);
		}
	}
}
