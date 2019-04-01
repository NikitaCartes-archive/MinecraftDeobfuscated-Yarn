package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_738 extends class_4003 {
	private final class_4002 field_17888;

	private class_738(class_1937 arg, double d, double e, double f, double g, double h, double i, class_4002 arg2) {
		super(arg, d, e, f, 0.0, 0.0, 0.0);
		this.field_17888 = arg2;
		this.field_3852 *= 0.3F;
		this.field_3869 = Math.random() * 0.2F + 0.1F;
		this.field_3850 *= 0.3F;
		this.method_3080(0.01F, 0.01F);
		this.field_3847 = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.method_18142(arg2);
		this.field_3844 = 0.0F;
		this.field_3852 = g;
		this.field_3869 = h;
		this.field_3850 = i;
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
		int i = 60 - this.field_3847;
		if (this.field_3847-- <= 0) {
			this.method_3085();
		} else {
			this.field_3869 = this.field_3869 - (double)this.field_3844;
			this.method_3069(this.field_3852, this.field_3869, this.field_3850);
			this.field_3852 *= 0.98F;
			this.field_3869 *= 0.98F;
			this.field_3850 *= 0.98F;
			float f = (float)i * 0.001F;
			this.method_3080(f, f);
			this.method_18141(this.field_17888.method_18138(i % 4, 4));
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_739 implements class_707<class_2400> {
		private final class_4002 field_17889;

		public class_739(class_4002 arg) {
			this.field_17889 = arg;
		}

		public class_703 method_3115(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_738(arg2, d, e, f, g, h, i, this.field_17889);
		}
	}
}
