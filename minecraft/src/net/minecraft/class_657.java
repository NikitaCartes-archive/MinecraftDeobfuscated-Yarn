package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_657 extends class_4003 {
	private class_657(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(arg, d, e, f, 0.0, 0.0, 0.0);
		this.field_3852 *= 0.1F;
		this.field_3869 *= 0.1F;
		this.field_3850 *= 0.1F;
		this.field_3852 += g * 0.4;
		this.field_3869 += h * 0.4;
		this.field_3850 += i * 0.4;
		float j = (float)(Math.random() * 0.3F + 0.6F);
		this.field_3861 = j;
		this.field_3842 = j;
		this.field_3859 = j;
		this.field_17867 *= 0.75F;
		this.field_3847 = Math.max((int)(6.0 / (Math.random() * 0.8 + 0.6)), 1);
		this.field_3862 = false;
		this.method_3070();
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
			this.field_3842 = (float)((double)this.field_3842 * 0.96);
			this.field_3859 = (float)((double)this.field_3859 * 0.9);
			this.field_3852 *= 0.7F;
			this.field_3869 *= 0.7F;
			this.field_3850 *= 0.7F;
			this.field_3869 -= 0.02F;
			if (this.field_3845) {
				this.field_3852 *= 0.7F;
				this.field_3850 *= 0.7F;
			}
		}
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17828;
	}

	@Environment(EnvType.CLIENT)
	public static class class_3939 implements class_707<class_2400> {
		private final class_4002 field_18291;

		public class_3939(class_4002 arg) {
			this.field_18291 = arg;
		}

		public class_703 method_17580(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_657 lv = new class_657(arg2, d, e, f, g, h, i);
			lv.method_18140(this.field_18291);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_658 implements class_707<class_2400> {
		private final class_4002 field_17790;

		public class_658(class_4002 arg) {
			this.field_17790 = arg;
		}

		public class_703 method_3013(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_657 lv = new class_657(arg2, d, e, f, g, h + 1.0, i);
			lv.method_3077(20);
			lv.method_18140(this.field_17790);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_659 implements class_707<class_2400> {
		private final class_4002 field_17791;

		public class_659(class_4002 arg) {
			this.field_17791 = arg;
		}

		public class_703 method_3014(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_657 lv = new class_657(arg2, d, e, f, g, h, i);
			lv.field_3861 *= 0.3F;
			lv.field_3842 *= 0.8F;
			lv.method_18140(this.field_17791);
			return lv;
		}
	}
}
