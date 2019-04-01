package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_736 extends class_4003 {
	private float field_3897;

	private class_736(class_1937 arg, double d, double e, double f) {
		super(arg, d, e, f);
		this.field_3847 = (int)(Math.random() * 60.0) + 30;
		this.field_3862 = false;
		this.field_3852 = 0.0;
		this.field_3869 = -0.05;
		this.field_3850 = 0.0;
		this.method_3080(0.02F, 0.02F);
		this.field_17867 = this.field_17867 * (this.field_3840.nextFloat() * 0.6F + 0.2F);
		this.field_3844 = 0.002F;
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
		if (this.field_3866++ >= this.field_3847) {
			this.method_3085();
		} else {
			float f = 0.6F;
			this.field_3852 = this.field_3852 + (double)(0.6F * class_3532.method_15362(this.field_3897));
			this.field_3850 = this.field_3850 + (double)(0.6F * class_3532.method_15374(this.field_3897));
			this.field_3852 *= 0.07;
			this.field_3850 *= 0.07;
			this.method_3069(this.field_3852, this.field_3869, this.field_3850);
			if (!this.field_3851.method_8316(new class_2338(this.field_3874, this.field_3854, this.field_3871)).method_15767(class_3486.field_15517) || this.field_3845) {
				this.method_3085();
			}

			this.field_3897 = (float)((double)this.field_3897 + 0.08);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_737 implements class_707<class_2400> {
		private final class_4002 field_17890;

		public class_737(class_4002 arg) {
			this.field_17890 = arg;
		}

		public class_703 method_3114(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_736 lv = new class_736(arg2, d, e, f);
			lv.method_18140(this.field_17890);
			return lv;
		}
	}
}
