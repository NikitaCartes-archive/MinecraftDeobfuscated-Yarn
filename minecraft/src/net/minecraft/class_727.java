package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_727 extends class_703 {
	private final class_2680 field_3892;
	private class_2338 field_3891;

	protected class_727(class_1937 arg, double d, double e, double f, double g, double h, double i, class_2680 arg2) {
		super(arg, d, e, f, g, h, i);
		this.field_3892 = arg2;
		this.method_3078(class_310.method_1551().method_1541().method_3351().method_3339(arg2));
		this.field_3844 = 1.0F;
		this.field_3861 = 0.6F;
		this.field_3842 = 0.6F;
		this.field_3859 = 0.6F;
		this.field_3863 /= 2.0F;
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
	public int method_3079() {
		return 1;
	}

	@Override
	public void method_3074(class_287 arg, class_1297 arg2, float f, float g, float h, float i, float j, float k) {
		float l = 0.1F * this.field_3863;
		float m = this.field_3855.method_4580((double)(this.field_3865 / 4.0F * 16.0F));
		float n = this.field_3855.method_4580((double)((this.field_3865 + 1.0F) / 4.0F * 16.0F));
		float o = this.field_3855.method_4570((double)(this.field_3846 / 4.0F * 16.0F));
		float p = this.field_3855.method_4570((double)((this.field_3846 + 1.0F) / 4.0F * 16.0F));
		float q = (float)(class_3532.method_16436((double)f, this.field_3858, this.field_3874) - field_3873);
		float r = (float)(class_3532.method_16436((double)f, this.field_3838, this.field_3854) - field_3853);
		float s = (float)(class_3532.method_16436((double)f, this.field_3856, this.field_3871) - field_3870);
		int t = this.method_3068(f);
		int u = t >> 16 & 65535;
		int v = t & 65535;
		arg.method_1315((double)(q - g * l - j * l), (double)(r - h * l), (double)(s - i * l - k * l))
			.method_1312((double)m, (double)p)
			.method_1336(this.field_3861, this.field_3842, this.field_3859, 1.0F)
			.method_1313(u, v)
			.method_1344();
		arg.method_1315((double)(q - g * l + j * l), (double)(r + h * l), (double)(s - i * l + k * l))
			.method_1312((double)m, (double)o)
			.method_1336(this.field_3861, this.field_3842, this.field_3859, 1.0F)
			.method_1313(u, v)
			.method_1344();
		arg.method_1315((double)(q + g * l + j * l), (double)(r + h * l), (double)(s + i * l + k * l))
			.method_1312((double)n, (double)o)
			.method_1336(this.field_3861, this.field_3842, this.field_3859, 1.0F)
			.method_1313(u, v)
			.method_1344();
		arg.method_1315((double)(q + g * l - j * l), (double)(r - h * l), (double)(s + i * l - k * l))
			.method_1312((double)n, (double)p)
			.method_1336(this.field_3861, this.field_3842, this.field_3859, 1.0F)
			.method_1313(u, v)
			.method_1344();
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
