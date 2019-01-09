package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_651 extends class_703 {
	protected class_651(class_1937 arg, double d, double e, double f, class_1935 arg2) {
		super(arg, d, e, f, 0.0, 0.0, 0.0);
		this.method_3078(class_310.method_1551().method_1480().method_4012().method_3307(arg2));
		this.field_3861 = 1.0F;
		this.field_3842 = 1.0F;
		this.field_3859 = 1.0F;
		this.field_3852 = 0.0;
		this.field_3869 = 0.0;
		this.field_3850 = 0.0;
		this.field_3844 = 0.0F;
		this.field_3847 = 80;
		this.field_3862 = false;
	}

	@Override
	public int method_3079() {
		return 1;
	}

	@Override
	public void method_3074(class_287 arg, class_1297 arg2, float f, float g, float h, float i, float j, float k) {
		float l = this.field_3855.method_4594();
		float m = this.field_3855.method_4577();
		float n = this.field_3855.method_4593();
		float o = this.field_3855.method_4575();
		float p = 0.5F;
		float q = (float)(class_3532.method_16436((double)f, this.field_3858, this.field_3874) - field_3873);
		float r = (float)(class_3532.method_16436((double)f, this.field_3838, this.field_3854) - field_3853);
		float s = (float)(class_3532.method_16436((double)f, this.field_3856, this.field_3871) - field_3870);
		int t = this.method_3068(f);
		int u = t >> 16 & 65535;
		int v = t & 65535;
		arg.method_1315((double)(q - g * 0.5F - j * 0.5F), (double)(r - h * 0.5F), (double)(s - i * 0.5F - k * 0.5F))
			.method_1312((double)m, (double)o)
			.method_1336(this.field_3861, this.field_3842, this.field_3859, 1.0F)
			.method_1313(u, v)
			.method_1344();
		arg.method_1315((double)(q - g * 0.5F + j * 0.5F), (double)(r + h * 0.5F), (double)(s - i * 0.5F + k * 0.5F))
			.method_1312((double)m, (double)n)
			.method_1336(this.field_3861, this.field_3842, this.field_3859, 1.0F)
			.method_1313(u, v)
			.method_1344();
		arg.method_1315((double)(q + g * 0.5F + j * 0.5F), (double)(r + h * 0.5F), (double)(s + i * 0.5F + k * 0.5F))
			.method_1312((double)l, (double)n)
			.method_1336(this.field_3861, this.field_3842, this.field_3859, 1.0F)
			.method_1313(u, v)
			.method_1344();
		arg.method_1315((double)(q + g * 0.5F - j * 0.5F), (double)(r - h * 0.5F), (double)(s + i * 0.5F - k * 0.5F))
			.method_1312((double)l, (double)o)
			.method_1336(this.field_3861, this.field_3842, this.field_3859, 1.0F)
			.method_1313(u, v)
			.method_1344();
	}

	@Environment(EnvType.CLIENT)
	public static class class_652 implements class_707<class_2400> {
		public class_703 method_3010(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_651(arg2, d, e, f, class_2246.field_10499.method_8389());
		}
	}
}
