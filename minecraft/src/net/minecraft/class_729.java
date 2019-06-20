package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_729 extends class_4003 {
	private class_729(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(arg, d, e, f, g, h, i);
		float j = this.field_3840.nextFloat() * 0.1F + 0.2F;
		this.field_3861 = j;
		this.field_3842 = j;
		this.field_3859 = j;
		this.method_3080(0.02F, 0.02F);
		this.field_17867 = this.field_17867 * (this.field_3840.nextFloat() * 0.6F + 0.5F);
		this.field_3852 *= 0.02F;
		this.field_3869 *= 0.02F;
		this.field_3850 *= 0.02F;
		this.field_3847 = (int)(20.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17828;
	}

	@Override
	public void method_3069(double d, double e, double f) {
		this.method_3067(this.method_3064().method_989(d, e, f));
		this.method_3072();
	}

	@Override
	public void method_3070() {
		this.field_3858 = this.field_3874;
		this.field_3838 = this.field_3854;
		this.field_3856 = this.field_3871;
		if (this.field_3847-- <= 0) {
			this.method_3085();
		} else {
			this.method_3069(this.field_3852, this.field_3869, this.field_3850);
			this.field_3852 *= 0.99;
			this.field_3869 *= 0.99;
			this.field_3850 *= 0.99;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_3991 implements class_707<class_2400> {
		private final class_4002 field_17880;

		public class_3991(class_4002 arg) {
			this.field_17880 = arg;
		}

		public class_703 method_18044(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_729 lv = new class_729(arg2, d, e, f, g, h, i);
			lv.method_18140(this.field_17880);
			lv.method_3084(1.0F, 1.0F, 1.0F);
			lv.method_3077(3 + arg2.method_8409().nextInt(5));
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_730 implements class_707<class_2400> {
		private final class_4002 field_17881;

		public class_730(class_4002 arg) {
			this.field_17881 = arg;
		}

		public class_703 method_3110(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_729 lv = new class_729(arg2, d, e, f, g, h, i);
			lv.method_3084(0.3F, 0.5F, 1.0F);
			lv.method_18140(this.field_17881);
			lv.method_3083(1.0F - arg2.field_9229.nextFloat() * 0.7F);
			lv.method_3077(lv.method_3082() / 2);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_731 implements class_707<class_2400> {
		private final class_4002 field_17882;

		public class_731(class_4002 arg) {
			this.field_17882 = arg;
		}

		public class_703 method_3111(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_729 lv = new class_729(arg2, d, e, f, g, h, i);
			lv.method_18140(this.field_17882);
			lv.method_3084(1.0F, 1.0F, 1.0F);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_732 implements class_707<class_2400> {
		private final class_4002 field_17883;

		public class_732(class_4002 arg) {
			this.field_17883 = arg;
		}

		public class_703 method_3112(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_729 lv = new class_729(arg2, d, e, f, g, h, i);
			lv.method_18140(this.field_17883);
			return lv;
		}
	}
}
