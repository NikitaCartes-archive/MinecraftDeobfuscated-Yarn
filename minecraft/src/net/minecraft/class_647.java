package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_647 extends class_703 {
	protected class_647(class_1937 arg, double d, double e, double f, double g, double h, double i, class_1799 arg2) {
		this(arg, d, e, f, arg2);
		this.field_3852 *= 0.1F;
		this.field_3869 *= 0.1F;
		this.field_3850 *= 0.1F;
		this.field_3852 += g;
		this.field_3869 += h;
		this.field_3850 += i;
	}

	protected class_647(class_1937 arg, double d, double e, double f, class_1799 arg2) {
		super(arg, d, e, f, 0.0, 0.0, 0.0);
		this.method_3078(class_310.method_1551().method_1480().method_4019(arg2, arg, null).method_4711());
		this.field_3861 = 1.0F;
		this.field_3842 = 1.0F;
		this.field_3859 = 1.0F;
		this.field_3844 = 1.0F;
		this.field_3863 /= 2.0F;
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
