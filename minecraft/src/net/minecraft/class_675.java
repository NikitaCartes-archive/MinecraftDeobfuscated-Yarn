package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_675 extends class_708 {
	private class_675(class_1937 arg, double d, double e, double f, double g, double h, double i, class_4002 arg2) {
		super(arg, d, e, f, arg2, -5.0E-4F);
		this.field_3852 = g;
		this.field_3869 = h;
		this.field_3850 = i;
		this.field_17867 *= 0.75F;
		this.field_3847 = 60 + this.field_3840.nextInt(12);
		this.method_3092(15916745);
		this.method_18142(arg2);
	}

	@Override
	public void method_3069(double d, double e, double f) {
		this.method_3067(this.method_3064().method_989(d, e, f));
		this.method_3072();
	}

	@Environment(EnvType.CLIENT)
	public static class class_676 implements class_707<class_2400> {
		private final class_4002 field_17805;

		public class_676(class_4002 arg) {
			this.field_17805 = arg;
		}

		public class_703 method_3024(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_675(arg2, d, e, f, g, h, i, this.field_17805);
		}
	}
}
