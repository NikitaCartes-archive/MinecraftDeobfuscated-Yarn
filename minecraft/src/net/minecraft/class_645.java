package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_645 extends class_4003 {
	private final class_4002 field_17781;

	private class_645(class_1937 arg, double d, double e, double f, double g, class_4002 arg2) {
		super(arg, d, e, f, 0.0, 0.0, 0.0);
		this.field_17781 = arg2;
		this.field_3847 = 4;
		float h = this.field_3840.nextFloat() * 0.6F + 0.4F;
		this.field_3861 = h;
		this.field_3842 = h;
		this.field_3859 = h;
		this.field_17867 = 1.0F - (float)g * 0.5F;
		this.method_18142(arg2);
	}

	@Override
	public int method_3068(float f) {
		return 15728880;
	}

	@Override
	public void method_3070() {
		this.field_3858 = this.field_3874;
		this.field_3838 = this.field_3854;
		this.field_3856 = this.field_3871;
		if (this.field_3866++ >= this.field_3847) {
			this.method_3085();
		} else {
			this.method_18142(this.field_17781);
		}
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17830;
	}

	@Environment(EnvType.CLIENT)
	public static class class_646 implements class_707<class_2400> {
		private final class_4002 field_17782;

		public class_646(class_4002 arg) {
			this.field_17782 = arg;
		}

		public class_703 method_3006(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_645(arg2, d, e, f, g, this.field_17782);
		}
	}
}
