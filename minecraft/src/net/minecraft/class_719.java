package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_719 extends class_740 {
	private class_719(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(arg, d, e, f);
		this.field_3844 = 0.04F;
		if (h == 0.0 && (g != 0.0 || i != 0.0)) {
			this.field_3852 = g;
			this.field_3869 = 0.1;
			this.field_3850 = i;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_720 implements class_707<class_2400> {
		private final class_4002 field_17877;

		public class_720(class_4002 arg) {
			this.field_17877 = arg;
		}

		public class_703 method_3102(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_719 lv = new class_719(arg2, d, e, f, g, h, i);
			lv.method_18140(this.field_17877);
			return lv;
		}
	}
}
