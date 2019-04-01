package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_689 extends class_3998 {
	private int field_3814;
	private final int field_3813 = 8;

	private class_689(class_1937 arg, double d, double e, double f) {
		super(arg, d, e, f, 0.0, 0.0, 0.0);
	}

	@Override
	public void method_3070() {
		for (int i = 0; i < 6; i++) {
			double d = this.field_3874 + (this.field_3840.nextDouble() - this.field_3840.nextDouble()) * 4.0;
			double e = this.field_3854 + (this.field_3840.nextDouble() - this.field_3840.nextDouble()) * 4.0;
			double f = this.field_3871 + (this.field_3840.nextDouble() - this.field_3840.nextDouble()) * 4.0;
			this.field_3851.method_8406(class_2398.field_11236, d, e, f, (double)((float)this.field_3814 / (float)this.field_3813), 0.0, 0.0);
		}

		this.field_3814++;
		if (this.field_3814 == this.field_3813) {
			this.method_3085();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_690 implements class_707<class_2400> {
		public class_703 method_3037(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_689(arg2, d, e, f);
		}
	}
}
