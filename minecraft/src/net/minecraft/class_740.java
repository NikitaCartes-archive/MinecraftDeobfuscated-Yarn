package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_740 extends class_703 {
	protected class_740(class_1937 arg, double d, double e, double f) {
		super(arg, d, e, f, 0.0, 0.0, 0.0);
		this.field_3852 *= 0.3F;
		this.field_3869 = Math.random() * 0.2F + 0.1F;
		this.field_3850 *= 0.3F;
		this.field_3861 = 1.0F;
		this.field_3842 = 1.0F;
		this.field_3859 = 1.0F;
		this.method_3076(19 + this.field_3840.nextInt(4));
		this.method_3080(0.01F, 0.01F);
		this.field_3844 = 0.06F;
		this.field_3847 = (int)(8.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public void method_3070() {
		this.field_3858 = this.field_3874;
		this.field_3838 = this.field_3854;
		this.field_3856 = this.field_3871;
		this.field_3869 = this.field_3869 - (double)this.field_3844;
		this.method_3069(this.field_3852, this.field_3869, this.field_3850);
		this.field_3852 *= 0.98F;
		this.field_3869 *= 0.98F;
		this.field_3850 *= 0.98F;
		if (this.field_3847-- <= 0) {
			this.method_3085();
		}

		if (this.field_3845) {
			if (Math.random() < 0.5) {
				this.method_3085();
			}

			this.field_3852 *= 0.7F;
			this.field_3850 *= 0.7F;
		}

		class_2338 lv = new class_2338(this.field_3874, this.field_3854, this.field_3871);
		class_2680 lv2 = this.field_3851.method_8320(lv);
		class_3614 lv3 = lv2.method_11620();
		class_3610 lv4 = this.field_3851.method_8316(lv);
		if (!lv4.method_15769() || lv3.method_15799()) {
			double d;
			if (lv4.method_15763() > 0.0F) {
				d = (double)lv4.method_15763();
			} else {
				d = lv2.method_11628(this.field_3851, lv)
					.method_1102(class_2350.class_2351.field_11052, this.field_3874 - Math.floor(this.field_3874), this.field_3871 - Math.floor(this.field_3871));
			}

			double e = (double)class_3532.method_15357(this.field_3854) + d;
			if (this.field_3854 < e) {
				this.method_3085();
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_741 implements class_707<class_2400> {
		public class_703 method_3116(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_740(arg2, d, e, f);
		}
	}
}
