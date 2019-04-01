package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_684 extends class_4003 {
	private class_684(class_1937 arg, double d, double e, double f) {
		super(arg, d, e, f, 0.0, 0.0, 0.0);
		this.field_3852 *= 0.01F;
		this.field_3869 *= 0.01F;
		this.field_3850 *= 0.01F;
		this.field_3869 += 0.1;
		this.field_17867 *= 1.5F;
		this.field_3847 = 16;
		this.field_3862 = false;
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

			this.field_3852 *= 0.86F;
			this.field_3869 *= 0.86F;
			this.field_3850 *= 0.86F;
			if (this.field_3845) {
				this.field_3852 *= 0.7F;
				this.field_3850 *= 0.7F;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_685 implements class_707<class_2400> {
		private final class_4002 field_17813;

		public class_685(class_4002 arg) {
			this.field_17813 = arg;
		}

		public class_703 method_3034(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_684 lv = new class_684(arg2, d, e + 0.5, f);
			lv.method_18140(this.field_17813);
			lv.method_3084(1.0F, 1.0F, 1.0F);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_686 implements class_707<class_2400> {
		private final class_4002 field_17814;

		public class_686(class_4002 arg) {
			this.field_17814 = arg;
		}

		public class_703 method_3035(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_684 lv = new class_684(arg2, d, e, f);
			lv.method_18140(this.field_17814);
			return lv;
		}
	}
}
