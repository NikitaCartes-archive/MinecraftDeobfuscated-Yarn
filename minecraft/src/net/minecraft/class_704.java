package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_704 extends class_4003 {
	private final class_4002 field_17862;

	private class_704(class_1937 arg, double d, double e, double f, double g, double h, double i, class_4002 arg2) {
		super(arg, d, e, f, 0.0, 0.0, 0.0);
		this.field_17862 = arg2;
		float j = 2.5F;
		this.field_3852 *= 0.1F;
		this.field_3869 *= 0.1F;
		this.field_3850 *= 0.1F;
		this.field_3852 += g;
		this.field_3869 += h;
		this.field_3850 += i;
		float k = 1.0F - (float)(Math.random() * 0.3F);
		this.field_3861 = k;
		this.field_3842 = k;
		this.field_3859 = k;
		this.field_17867 *= 1.875F;
		int l = (int)(8.0 / (Math.random() * 0.8 + 0.3));
		this.field_3847 = (int)Math.max((float)l * 2.5F, 1.0F);
		this.field_3862 = false;
		this.method_18142(arg2);
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17829;
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
			this.method_18142(this.field_17862);
			this.method_3069(this.field_3852, this.field_3869, this.field_3850);
			this.field_3852 *= 0.96F;
			this.field_3869 *= 0.96F;
			this.field_3850 *= 0.96F;
			class_1657 lv = this.field_3851.method_18459(this.field_3874, this.field_3854, this.field_3871, 2.0, false);
			if (lv != null) {
				class_238 lv2 = lv.method_5829();
				if (this.field_3854 > lv2.field_1322) {
					this.field_3854 = this.field_3854 + (lv2.field_1322 - this.field_3854) * 0.2;
					this.field_3869 = this.field_3869 + (lv.method_18798().field_1351 - this.field_3869) * 0.2;
					this.method_3063(this.field_3874, this.field_3854, this.field_3871);
				}
			}

			if (this.field_3845) {
				this.field_3852 *= 0.7F;
				this.field_3850 *= 0.7F;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_705 implements class_707<class_2400> {
		private final class_4002 field_17863;

		public class_705(class_4002 arg) {
			this.field_17863 = arg;
		}

		public class_703 method_3088(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_704(arg2, d, e, f, g, h, i, this.field_17863);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_706 implements class_707<class_2400> {
		private final class_4002 field_17864;

		public class_706(class_4002 arg) {
			this.field_17864 = arg;
		}

		public class_703 method_3089(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_703 lv = new class_704(arg2, d, e, f, g, h, i, this.field_17864);
			lv.method_3084(200.0F, 50.0F, 120.0F);
			lv.method_3083(0.4F);
			return lv;
		}
	}
}
