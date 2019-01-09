package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_711 extends class_703 {
	private static final Random field_3888 = new Random();
	private int field_3889 = 128;

	protected class_711(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(arg, d, e, f, 0.5 - field_3888.nextDouble(), h, 0.5 - field_3888.nextDouble());
		this.field_3869 *= 0.2F;
		if (g == 0.0 && i == 0.0) {
			this.field_3852 *= 0.1F;
			this.field_3850 *= 0.1F;
		}

		this.field_3863 *= 0.75F;
		this.field_3847 = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.field_3862 = false;
	}

	@Override
	public boolean method_3071() {
		return true;
	}

	@Override
	public void method_3070() {
		this.field_3858 = this.field_3874;
		this.field_3838 = this.field_3854;
		this.field_3856 = this.field_3871;
		if (this.field_3866++ >= this.field_3847) {
			this.method_3085();
		}

		this.method_3076(this.field_3889 + 7 - this.field_3866 * 8 / this.field_3847);
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

	public void method_3095(int i) {
		this.field_3889 = i;
	}

	@Environment(EnvType.CLIENT)
	public static class class_712 implements class_707<class_2400> {
		public class_703 method_3096(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_703 lv = new class_711(arg2, d, e, f, g, h, i);
			lv.method_3083(0.15F);
			lv.method_3084((float)g, (float)h, (float)i);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_713 implements class_707<class_2400> {
		public class_703 method_3097(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_703 lv = new class_711(arg2, d, e, f, g, h, i);
			((class_711)lv).method_3095(144);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_714 implements class_707<class_2400> {
		public class_703 method_3098(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_703 lv = new class_711(arg2, d, e, f, g, h, i);
			lv.method_3084((float)g, (float)h, (float)i);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_715 implements class_707<class_2400> {
		public class_703 method_3099(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_711(arg2, d, e, f, g, h, i);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_716 implements class_707<class_2400> {
		public class_703 method_3100(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_703 lv = new class_711(arg2, d, e, f, g, h, i);
			((class_711)lv).method_3095(144);
			float j = arg2.field_9229.nextFloat() * 0.5F + 0.35F;
			lv.method_3084(1.0F * j, 0.0F * j, 1.0F * j);
			return lv;
		}
	}
}
