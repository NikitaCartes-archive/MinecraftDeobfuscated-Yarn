package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_721 extends class_673 {
	protected class_721(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(arg, d, e, f, g, h, i);
		this.field_3844 = 0.5F;
	}

	@Override
	public void method_3070() {
		super.method_3070();
		this.field_3869 = this.field_3869 - (0.004 + 0.04 * (double)this.field_3844);
	}

	@Environment(EnvType.CLIENT)
	public static class class_722 implements class_707<class_2400> {
		public class_703 method_3103(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_721(arg2, d, e, f, g, h, i);
		}
	}
}
