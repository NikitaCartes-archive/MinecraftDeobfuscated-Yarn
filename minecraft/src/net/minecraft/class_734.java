package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_734 extends class_708 {
	private class_734(class_1937 arg, double d, double e, double f, double g, double h, double i, class_4002 arg2) {
		super(arg, d, e, f, arg2, -0.05F);
		this.field_3852 = g;
		this.field_3869 = h;
		this.field_3850 = i;
		this.field_17867 *= 0.75F;
		this.field_3847 = 60 + this.field_3840.nextInt(12);
		this.method_18142(arg2);
		if (this.field_3840.nextInt(4) == 0) {
			this.method_3084(0.6F + this.field_3840.nextFloat() * 0.2F, 0.6F + this.field_3840.nextFloat() * 0.3F, this.field_3840.nextFloat() * 0.2F);
		} else {
			this.method_3084(0.1F + this.field_3840.nextFloat() * 0.2F, 0.4F + this.field_3840.nextFloat() * 0.3F, this.field_3840.nextFloat() * 0.2F);
		}

		this.method_3091(0.6F);
	}

	@Environment(EnvType.CLIENT)
	public static class class_735 implements class_707<class_2400> {
		private final class_4002 field_17887;

		public class_735(class_4002 arg) {
			this.field_17887 = arg;
		}

		public class_703 method_3113(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_734(arg2, d, e, f, g, h, i, this.field_17887);
		}
	}
}
