package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_734 extends class_708 {
	public class_734(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(arg, d, e, f, 176, 8, -0.05F);
		this.field_3852 = g;
		this.field_3869 = h;
		this.field_3850 = i;
		this.field_3863 *= 0.75F;
		this.field_3847 = 60 + this.field_3840.nextInt(12);
		if (this.field_3840.nextInt(4) == 0) {
			this.method_3084(0.6F + this.field_3840.nextFloat() * 0.2F, 0.6F + this.field_3840.nextFloat() * 0.3F, this.field_3840.nextFloat() * 0.2F);
		} else {
			this.method_3084(0.1F + this.field_3840.nextFloat() * 0.2F, 0.4F + this.field_3840.nextFloat() * 0.3F, this.field_3840.nextFloat() * 0.2F);
		}

		this.method_3091(0.6F);
	}

	@Environment(EnvType.CLIENT)
	public static class class_735 implements class_707<class_2400> {
		public class_703 method_3113(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_734(arg2, d, e, f, g, h, i);
		}
	}
}
