package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_723 extends class_4003 {
	private class_723(class_1937 arg, double d, double e, double f) {
		super(arg, d, e - 0.125, f);
		this.field_3861 = 0.4F;
		this.field_3842 = 0.4F;
		this.field_3859 = 0.7F;
		this.method_3080(0.01F, 0.01F);
		this.field_17867 = this.field_17867 * (this.field_3840.nextFloat() * 0.6F + 0.2F);
		this.field_3847 = (int)(16.0 / (Math.random() * 0.8 + 0.2));
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
			this.method_3069(this.field_3852, this.field_3869, this.field_3850);
			if (!this.field_3851.method_8316(new class_2338(this.field_3874, this.field_3854, this.field_3871)).method_15767(class_3486.field_15517)) {
				this.method_3085();
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_724 implements class_707<class_2400> {
		private final class_4002 field_17879;

		public class_724(class_4002 arg) {
			this.field_17879 = arg;
		}

		public class_703 method_3104(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_723 lv = new class_723(arg2, d, e, f);
			lv.method_18140(this.field_17879);
			return lv;
		}
	}
}
