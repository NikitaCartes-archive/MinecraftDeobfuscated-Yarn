package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_663 extends class_703 {
	private final class_3611 field_3789;
	private int field_3790;

	protected class_663(class_1937 arg, double d, double e, double f, class_3611 arg2) {
		super(arg, d, e, f, 0.0, 0.0, 0.0);
		this.field_3852 = 0.0;
		this.field_3869 = 0.0;
		this.field_3850 = 0.0;
		if (arg2.method_15791(class_3486.field_15517)) {
			this.field_3861 = 0.0F;
			this.field_3842 = 0.0F;
			this.field_3859 = 1.0F;
		} else {
			this.field_3861 = 1.0F;
			this.field_3842 = 0.0F;
			this.field_3859 = 0.0F;
		}

		this.method_3076(113);
		this.method_3080(0.01F, 0.01F);
		this.field_3844 = 0.06F;
		this.field_3789 = arg2;
		this.field_3790 = 40;
		this.field_3847 = (int)(64.0 / (Math.random() * 0.8 + 0.2));
		this.field_3852 = 0.0;
		this.field_3869 = 0.0;
		this.field_3850 = 0.0;
	}

	@Override
	public int method_3068(float f) {
		return this.field_3789.method_15791(class_3486.field_15517) ? super.method_3068(f) : 257;
	}

	@Override
	public void method_3070() {
		this.field_3858 = this.field_3874;
		this.field_3838 = this.field_3854;
		this.field_3856 = this.field_3871;
		if (this.field_3789.method_15791(class_3486.field_15517)) {
			this.field_3861 = 0.2F;
			this.field_3842 = 0.3F;
			this.field_3859 = 1.0F;
		} else {
			this.field_3861 = 1.0F;
			this.field_3842 = 16.0F / (float)(40 - this.field_3790 + 16);
			this.field_3859 = 4.0F / (float)(40 - this.field_3790 + 8);
		}

		this.field_3869 = this.field_3869 - (double)this.field_3844;
		if (this.field_3790-- > 0) {
			this.field_3852 *= 0.02;
			this.field_3869 *= 0.02;
			this.field_3850 *= 0.02;
			this.method_3076(113);
		} else {
			this.method_3076(112);
		}

		this.method_3069(this.field_3852, this.field_3869, this.field_3850);
		this.field_3852 *= 0.98F;
		this.field_3869 *= 0.98F;
		this.field_3850 *= 0.98F;
		if (this.field_3847-- <= 0) {
			this.method_3085();
		}

		if (this.field_3845) {
			if (this.field_3789.method_15791(class_3486.field_15517)) {
				this.method_3085();
				this.field_3851.method_8406(class_2398.field_11202, this.field_3874, this.field_3854, this.field_3871, 0.0, 0.0, 0.0);
			} else {
				this.method_3076(114);
			}

			this.field_3852 *= 0.7F;
			this.field_3850 *= 0.7F;
		}

		class_2338 lv = new class_2338(this.field_3874, this.field_3854, this.field_3871);
		class_3610 lv2 = this.field_3851.method_8316(lv);
		if (lv2.method_15772() == this.field_3789) {
			double d = (double)((float)class_3532.method_15357(this.field_3854) + lv2.method_15763());
			if (this.field_3854 < d) {
				this.method_3085();
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_664 implements class_707<class_2400> {
		public class_703 method_3017(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_663(arg2, d, e, f, class_3612.field_15908);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_665 implements class_707<class_2400> {
		public class_703 method_3018(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_663(arg2, d, e, f, class_3612.field_15910);
		}
	}
}
