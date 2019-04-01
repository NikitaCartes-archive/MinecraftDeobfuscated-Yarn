package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_740 extends class_4003 {
	protected class_740(class_1937 arg, double d, double e, double f) {
		super(arg, d, e, f, 0.0, 0.0, 0.0);
		this.field_3852 *= 0.3F;
		this.field_3869 = Math.random() * 0.2F + 0.1F;
		this.field_3850 *= 0.3F;
		this.method_3080(0.01F, 0.01F);
		this.field_3844 = 0.06F;
		this.field_3847 = (int)(8.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17828;
	}

	@Override
	public void method_3070() {
		this.field_3858 = this.field_3874;
		this.field_3838 = this.field_3854;
		this.field_3856 = this.field_3871;
		if (this.field_3847-- <= 0) {
			this.method_3085();
		} else {
			this.field_3869 = this.field_3869 - (double)this.field_3844;
			this.method_3069(this.field_3852, this.field_3869, this.field_3850);
			this.field_3852 *= 0.98F;
			this.field_3869 *= 0.98F;
			this.field_3850 *= 0.98F;
			if (this.field_3845) {
				if (Math.random() < 0.5) {
					this.method_3085();
				}

				this.field_3852 *= 0.7F;
				this.field_3850 *= 0.7F;
			}

			class_2338 lv = new class_2338(this.field_3874, this.field_3854, this.field_3871);
			double d = Math.max(
				this.field_3851
					.method_8320(lv)
					.method_11628(this.field_3851, lv)
					.method_1102(class_2350.class_2351.field_11052, this.field_3874 - (double)lv.method_10263(), this.field_3871 - (double)lv.method_10260()),
				(double)this.field_3851.method_8316(lv).method_15763(this.field_3851, lv)
			);
			if (d > 0.0 && this.field_3854 < (double)lv.method_10264() + d) {
				this.method_3085();
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_741 implements class_707<class_2400> {
		private final class_4002 field_17891;

		public class_741(class_4002 arg) {
			this.field_17891 = arg;
		}

		public class_703 method_3116(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_740 lv = new class_740(arg2, d, e, f);
			lv.method_18140(this.field_17891);
			return lv;
		}
	}
}
