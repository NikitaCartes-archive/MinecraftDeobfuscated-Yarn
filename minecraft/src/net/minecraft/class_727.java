package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_727 extends class_4003 {
	private final class_2680 field_3892;
	private class_2338 field_3891;
	private final float field_17884;
	private final float field_17885;

	public class_727(class_1937 arg, double d, double e, double f, double g, double h, double i, class_2680 arg2) {
		super(arg, d, e, f, g, h, i);
		this.field_3892 = arg2;
		this.method_18141(class_310.method_1551().method_1541().method_3351().method_3339(arg2));
		this.field_3844 = 1.0F;
		this.field_3861 = 0.6F;
		this.field_3842 = 0.6F;
		this.field_3859 = 0.6F;
		this.field_17867 /= 2.0F;
		this.field_17884 = this.field_3840.nextFloat() * 3.0F;
		this.field_17885 = this.field_3840.nextFloat() * 3.0F;
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17827;
	}

	public class_727 method_3108(class_2338 arg) {
		this.field_3891 = arg;
		if (this.field_3892.method_11614() == class_2246.field_10219) {
			return this;
		} else {
			this.method_3107(arg);
			return this;
		}
	}

	public class_727 method_3106() {
		this.field_3891 = new class_2338(this.field_3874, this.field_3854, this.field_3871);
		class_2248 lv = this.field_3892.method_11614();
		if (lv == class_2246.field_10219) {
			return this;
		} else {
			this.method_3107(this.field_3891);
			return this;
		}
	}

	protected void method_3107(@Nullable class_2338 arg) {
		int i = class_310.method_1551().method_1505().method_1697(this.field_3892, this.field_3851, arg, 0);
		this.field_3861 *= (float)(i >> 16 & 0xFF) / 255.0F;
		this.field_3842 *= (float)(i >> 8 & 0xFF) / 255.0F;
		this.field_3859 *= (float)(i & 0xFF) / 255.0F;
	}

	@Override
	protected float method_18133() {
		return this.field_17886.method_4580((double)((this.field_17884 + 1.0F) / 4.0F * 16.0F));
	}

	@Override
	protected float method_18134() {
		return this.field_17886.method_4580((double)(this.field_17884 / 4.0F * 16.0F));
	}

	@Override
	protected float method_18135() {
		return this.field_17886.method_4570((double)(this.field_17885 / 4.0F * 16.0F));
	}

	@Override
	protected float method_18136() {
		return this.field_17886.method_4570((double)((this.field_17885 + 1.0F) / 4.0F * 16.0F));
	}

	@Override
	public int method_3068(float f) {
		int i = super.method_3068(f);
		int j = 0;
		if (this.field_3851.method_8591(this.field_3891)) {
			j = this.field_3851.method_8313(this.field_3891, 0);
		}

		return i == 0 ? j : i;
	}

	@Environment(EnvType.CLIENT)
	public static class class_728 implements class_707<class_2388> {
		public class_703 method_3109(class_2388 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_2680 lv = arg.method_10278();
			return !lv.method_11588() && lv.method_11614() != class_2246.field_10008 ? new class_727(arg2, d, e, f, g, h, i, lv).method_3106() : null;
		}
	}
}
