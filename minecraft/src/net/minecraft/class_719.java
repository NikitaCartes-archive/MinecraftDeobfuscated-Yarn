package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_719 extends class_740 {
	protected class_719(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(arg, d, e, f);
		this.field_3844 = 0.04F;
		this.method_3076(20 + this.field_3840.nextInt(3));
		if (h == 0.0 && (g != 0.0 || i != 0.0)) {
			this.field_3852 = g;
			this.field_3869 = 0.1;
			this.field_3850 = i;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_720 implements class_707<class_2400> {
		public class_703 method_3102(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_719(arg2, d, e, f, g, h, i);
		}
	}
}
