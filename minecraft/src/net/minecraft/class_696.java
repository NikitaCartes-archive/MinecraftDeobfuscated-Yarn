package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_696 extends class_717 {
	protected class_696(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(arg, d, e, f, g, h, i, 2.5F);
	}

	@Environment(EnvType.CLIENT)
	public static class class_697 implements class_707<class_2400> {
		public class_703 method_3040(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_696(arg2, d, e, f, g, h, i);
		}
	}
}
