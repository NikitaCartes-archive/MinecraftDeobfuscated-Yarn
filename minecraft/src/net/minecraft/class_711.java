package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_711 extends class_4003 {
	private static final Random field_3888 = new Random();
	private final class_4002 field_17870;

	private class_711(class_1937 arg, double d, double e, double f, double g, double h, double i, class_4002 arg2) {
		super(arg, d, e, f, 0.5 - field_3888.nextDouble(), h, 0.5 - field_3888.nextDouble());
		this.field_17870 = arg2;
		this.field_3869 *= 0.2F;
		if (g == 0.0 && i == 0.0) {
			this.field_3852 *= 0.1F;
			this.field_3850 *= 0.1F;
		}

		this.field_17867 *= 0.75F;
		this.field_3847 = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.field_3862 = false;
		this.method_18142(arg2);
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17829;
	}

	@Override
	public void method_3070() {
		this.field_3858 = this.field_3874;
		this.field_3838 = this.field_3854;
		this.field_3856 = this.field_3871;
		if (this.field_3866++ >= this.field_3847) {
			this.method_3085();
		} else {
			this.method_18142(this.field_17870);
			this.field_3869 += 0.004;
			this.method_3069(this.field_3852, this.field_3869, this.field_3850);
			if (this.field_3854 == this.field_3838) {
				this.field_3852 *= 1.1;
				this.field_3850 *= 1.1;
			}

			this.field_3852 *= 0.96F;
			this.field_3869 *= 0.96F;
			this.field_3850 *= 0.96F;
			if (this.field_3845) {
				this.field_3852 *= 0.7F;
				this.field_3850 *= 0.7F;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_712 implements class_707<class_2400> {
		private final class_4002 field_17871;

		public class_712(class_4002 arg) {
			this.field_17871 = arg;
		}

		public class_703 method_3096(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_703 lv = new class_711(arg2, d, e, f, g, h, i, this.field_17871);
			lv.method_3083(0.15F);
			lv.method_3084((float)g, (float)h, (float)i);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_713 implements class_707<class_2400> {
		private final class_4002 field_17872;

		public class_713(class_4002 arg) {
			this.field_17872 = arg;
		}

		public class_703 method_3097(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_711(arg2, d, e, f, g, h, i, this.field_17872);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_714 implements class_707<class_2400> {
		private final class_4002 field_17873;

		public class_714(class_4002 arg) {
			this.field_17873 = arg;
		}

		public class_703 method_3098(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_703 lv = new class_711(arg2, d, e, f, g, h, i, this.field_17873);
			lv.method_3084((float)g, (float)h, (float)i);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_715 implements class_707<class_2400> {
		private final class_4002 field_17874;

		public class_715(class_4002 arg) {
			this.field_17874 = arg;
		}

		public class_703 method_3099(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_711(arg2, d, e, f, g, h, i, this.field_17874);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_716 implements class_707<class_2400> {
		private final class_4002 field_17875;

		public class_716(class_4002 arg) {
			this.field_17875 = arg;
		}

		public class_703 method_3100(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_711 lv = new class_711(arg2, d, e, f, g, h, i, this.field_17875);
			float j = arg2.field_9229.nextFloat() * 0.5F + 0.35F;
			lv.method_3084(1.0F * j, 0.0F * j, 1.0F * j);
			return lv;
		}
	}
}
