package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_655 extends class_4003 {
	private class_655(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(arg, d, e, f);
		this.method_3080(0.02F, 0.02F);
		this.field_17867 = this.field_17867 * (this.field_3840.nextFloat() * 0.6F + 0.2F);
		this.field_3852 = g * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
		this.field_3869 = h * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
		this.field_3850 = i * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
		this.field_3847 = (int)(8.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public void method_3070() {
		this.field_3858 = this.field_3874;
		this.field_3838 = this.field_3854;
		this.field_3856 = this.field_3871;
		if (this.field_3847-- <= 0) {
			this.method_3085();
		} else {
			this.field_3869 += 0.002;
			this.method_3069(this.field_3852, this.field_3869, this.field_3850);
			this.field_3852 *= 0.85F;
			this.field_3869 *= 0.85F;
			this.field_3850 *= 0.85F;
			if (!this.field_3851.method_8316(new class_2338(this.field_3874, this.field_3854, this.field_3871)).method_15767(class_3486.field_15517)) {
				this.method_3085();
			}
		}
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17828;
	}

	@Environment(EnvType.CLIENT)
	public static class class_656 implements class_707<class_2400> {
		private final class_4002 field_17785;

		public class_656(class_4002 arg) {
			this.field_17785 = arg;
		}

		public class_703 method_3012(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_655 lv = new class_655(arg2, d, e, f, g, h, i);
			lv.method_18140(this.field_17785);
			return lv;
		}
	}
}
