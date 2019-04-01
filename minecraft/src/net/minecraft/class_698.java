package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_698 extends class_4003 {
	private class_698(class_1937 arg, double d, double e, double f, double g) {
		super(arg, d, e, f, 0.0, 0.0, 0.0);
		this.field_3852 *= 0.01F;
		this.field_3869 *= 0.01F;
		this.field_3850 *= 0.01F;
		this.field_3869 += 0.2;
		this.field_3861 = Math.max(0.0F, class_3532.method_15374(((float)g + 0.0F) * (float) (Math.PI * 2)) * 0.65F + 0.35F);
		this.field_3842 = Math.max(0.0F, class_3532.method_15374(((float)g + 0.33333334F) * (float) (Math.PI * 2)) * 0.65F + 0.35F);
		this.field_3859 = Math.max(0.0F, class_3532.method_15374(((float)g + 0.6666667F) * (float) (Math.PI * 2)) * 0.65F + 0.35F);
		this.field_17867 *= 1.5F;
		this.field_3847 = 6;
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17828;
	}

	@Override
	public float method_18132(float f) {
		return this.field_17867 * class_3532.method_15363(((float)this.field_3866 + f) / (float)this.field_3847 * 32.0F, 0.0F, 1.0F);
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
	}

	@Environment(EnvType.CLIENT)
	public static class class_699 implements class_707<class_2400> {
		private final class_4002 field_17819;

		public class_699(class_4002 arg) {
			this.field_17819 = arg;
		}

		public class_703 method_3041(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_698 lv = new class_698(arg2, d, e, f, g);
			lv.method_18140(this.field_17819);
			return lv;
		}
	}
}
