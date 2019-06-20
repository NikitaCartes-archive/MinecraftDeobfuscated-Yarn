package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_647 extends class_4003 {
	private final float field_17783;
	private final float field_17784;

	private class_647(class_1937 arg, double d, double e, double f, double g, double h, double i, class_1799 arg2) {
		this(arg, d, e, f, arg2);
		this.field_3852 *= 0.1F;
		this.field_3869 *= 0.1F;
		this.field_3850 *= 0.1F;
		this.field_3852 += g;
		this.field_3869 += h;
		this.field_3850 += i;
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17827;
	}

	protected class_647(class_1937 arg, double d, double e, double f, class_1799 arg2) {
		super(arg, d, e, f, 0.0, 0.0, 0.0);
		this.method_18141(class_310.method_1551().method_1480().method_4019(arg2, arg, null).method_4711());
		this.field_3844 = 1.0F;
		this.field_17867 /= 2.0F;
		this.field_17783 = this.field_3840.nextFloat() * 3.0F;
		this.field_17784 = this.field_3840.nextFloat() * 3.0F;
	}

	@Override
	protected float method_18133() {
		return this.field_17886.method_4580((double)((this.field_17783 + 1.0F) / 4.0F * 16.0F));
	}

	@Override
	protected float method_18134() {
		return this.field_17886.method_4580((double)(this.field_17783 / 4.0F * 16.0F));
	}

	@Override
	protected float method_18135() {
		return this.field_17886.method_4570((double)(this.field_17784 / 4.0F * 16.0F));
	}

	@Override
	protected float method_18136() {
		return this.field_17886.method_4570((double)((this.field_17784 + 1.0F) / 4.0F * 16.0F));
	}

	@Environment(EnvType.CLIENT)
	public static class class_648 implements class_707<class_2392> {
		public class_703 method_3007(class_2392 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_647(arg2, d, e, f, g, h, i, arg.method_10289());
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_649 implements class_707<class_2400> {
		public class_703 method_3008(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_647(arg2, d, e, f, new class_1799(class_1802.field_8777));
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_650 implements class_707<class_2400> {
		public class_703 method_3009(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_647(arg2, d, e, f, new class_1799(class_1802.field_8543));
		}
	}
}
