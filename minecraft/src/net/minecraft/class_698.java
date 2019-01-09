package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_698 extends class_703 {
	private final float field_3828;

	protected class_698(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		this(arg, d, e, f, g, h, i, 2.0F);
	}

	protected class_698(class_1937 arg, double d, double e, double f, double g, double h, double i, float j) {
		super(arg, d, e, f, 0.0, 0.0, 0.0);
		this.field_3852 *= 0.01F;
		this.field_3869 *= 0.01F;
		this.field_3850 *= 0.01F;
		this.field_3869 += 0.2;
		this.field_3861 = class_3532.method_15374(((float)g + 0.0F) * (float) (Math.PI * 2)) * 0.65F + 0.35F;
		this.field_3842 = class_3532.method_15374(((float)g + 0.33333334F) * (float) (Math.PI * 2)) * 0.65F + 0.35F;
		this.field_3859 = class_3532.method_15374(((float)g + 0.6666667F) * (float) (Math.PI * 2)) * 0.65F + 0.35F;
		this.field_3863 *= 0.75F;
		this.field_3863 *= j;
		this.field_3828 = this.field_3863;
		this.field_3847 = 6;
		this.method_3076(64);
	}

	@Override
	public void method_3074(class_287 arg, class_1297 arg2, float f, float g, float h, float i, float j, float k) {
		float l = ((float)this.field_3866 + f) / (float)this.field_3847 * 32.0F;
		l = class_3532.method_15363(l, 0.0F, 1.0F);
		this.field_3863 = this.field_3828 * l;
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

		this.field_3852 *= 0.66F;
		this.field_3869 *= 0.66F;
		this.field_3850 *= 0.66F;
		if (this.field_3845) {
			this.field_3852 *= 0.7F;
			this.field_3850 *= 0.7F;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_699 implements class_707<class_2400> {
		public class_703 method_3041(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_698(arg2, d, e, f, g, h, i);
		}
	}
}
