package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3940 extends class_703 {
	public class_3940(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(arg, d, e, f, g, h, i);
	}

	@Override
	public void method_3074(class_287 arg, class_1297 arg2, float f, float g, float h, float i, float j, float k) {
		float l = (float)this.field_3868 / 32.0F;
		float m = l + 0.0624375F;
		float n = (float)this.field_3848 / 32.0F;
		float o = n + 0.0624375F;
		float p = 0.1F * this.field_3863;
		if (this.field_3855 != null) {
			l = this.field_3855.method_4594();
			m = this.field_3855.method_4577();
			n = this.field_3855.method_4593();
			o = this.field_3855.method_4575();
		}

		float q = (float)(class_3532.method_16436((double)f, this.field_3858, this.field_3874) - field_3873);
		float r = (float)(class_3532.method_16436((double)f, this.field_3838, this.field_3854) - field_3853);
		float s = (float)(class_3532.method_16436((double)f, this.field_3856, this.field_3871) - field_3870);
		int t = this.method_3068(f);
		int u = t >> 16 & 65535;
		int v = t & 65535;
		class_243[] lvs = new class_243[]{
			new class_243((double)(-g * p - j * p), (double)(-h * p), (double)(-i * p - k * p)),
			new class_243((double)(-g * p + j * p), (double)(h * p), (double)(-i * p + k * p)),
			new class_243((double)(g * p + j * p), (double)(h * p), (double)(i * p + k * p)),
			new class_243((double)(g * p - j * p), (double)(-h * p), (double)(i * p - k * p))
		};
		arg.method_1315((double)q + lvs[0].field_1352, (double)r + lvs[0].field_1351, (double)s + lvs[0].field_1350)
			.method_1312((double)m, (double)o)
			.method_1336(this.field_3861, this.field_3842, this.field_3859, this.field_3841)
			.method_1313(u, v)
			.method_1344();
		arg.method_1315((double)q + lvs[1].field_1352, (double)r + lvs[1].field_1351, (double)s + lvs[1].field_1350)
			.method_1312((double)m, (double)n)
			.method_1336(this.field_3861, this.field_3842, this.field_3859, this.field_3841)
			.method_1313(u, v)
			.method_1344();
		arg.method_1315((double)q + lvs[2].field_1352, (double)r + lvs[2].field_1351, (double)s + lvs[2].field_1350)
			.method_1312((double)l, (double)n)
			.method_1336(this.field_3861, this.field_3842, this.field_3859, this.field_3841)
			.method_1313(u, v)
			.method_1344();
		arg.method_1315((double)q + lvs[3].field_1352, (double)r + lvs[3].field_1351, (double)s + lvs[3].field_1350)
			.method_1312((double)l, (double)o)
			.method_1336(this.field_3861, this.field_3842, this.field_3859, this.field_3841)
			.method_1313(u, v)
			.method_1344();
	}

	@Override
	public void method_3076(int i) {
		if (this.method_3079() != 0) {
			throw new RuntimeException("Invalid call to Particle.setMiscTex");
		} else {
			this.field_3868 = 2 * i % 16;
			this.field_3848 = i / 16;
		}
	}
}
