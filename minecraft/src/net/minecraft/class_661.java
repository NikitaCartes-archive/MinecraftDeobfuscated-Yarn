package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_661 extends class_4003 {
	private final class_4002 field_17787;

	private class_661(class_1937 arg, double d, double e, double f, double g, double h, double i, class_4002 arg2) {
		super(arg, d, e, f);
		this.field_17787 = arg2;
		this.field_3847 = 4;
		this.field_3844 = 0.008F;
		this.field_3852 = g;
		this.field_3869 = h;
		this.field_3850 = i;
		this.method_18142(arg2);
	}

	@Override
	public void method_3070() {
		this.field_3858 = this.field_3874;
		this.field_3838 = this.field_3854;
		this.field_3856 = this.field_3871;
		if (this.field_3866++ >= this.field_3847) {
			this.method_3085();
		} else {
			this.field_3869 = this.field_3869 - (double)this.field_3844;
			this.method_3069(this.field_3852, this.field_3869, this.field_3850);
			this.method_18142(this.field_17787);
		}
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17828;
	}

	@Environment(EnvType.CLIENT)
	public static class class_662 implements class_707<class_2400> {
		private final class_4002 field_17788;

		public class_662(class_4002 arg) {
			this.field_17788 = arg;
		}

		public class_703 method_3016(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_661(arg2, d, e, f, g, h, i, this.field_17788);
		}
	}
}
