package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_738 extends class_703 {
	protected class_738(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(arg, d, e, f, 0.0, 0.0, 0.0);
		this.field_3852 *= 0.3F;
		this.field_3869 = Math.random() * 0.2F + 0.1F;
		this.field_3850 *= 0.3F;
		this.field_3861 = 1.0F;
		this.field_3842 = 1.0F;
		this.field_3859 = 1.0F;
		this.method_3076(19);
		this.method_3080(0.01F, 0.01F);
		this.field_3847 = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.field_3844 = 0.0F;
		this.field_3852 = g;
		this.field_3869 = h;
		this.field_3850 = i;
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
		int i = 60 - this.field_3847;
		float f = (float)i * 0.001F;
		this.method_3080(f, f);
		this.method_3076(19 + i % 4);
		if (this.field_3847-- <= 0) {
			this.method_3085();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_739 implements class_707<class_2400> {
		public class_703 method_3115(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_738(arg2, d, e, f, g, h, i);
		}
	}
}
