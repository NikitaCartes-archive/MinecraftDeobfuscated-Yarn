package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_3940 extends class_703 {
	protected float field_17867 = 0.1F * (this.field_3840.nextFloat() * 0.5F + 0.5F) * 2.0F;

	protected class_3940(class_1937 arg, double d, double e, double f) {
		super(arg, d, e, f);
	}

	protected class_3940(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(arg, d, e, f, g, h, i);
	}

	@Override
	public void method_3074(class_287 arg, class_4184 arg2, float f, float g, float h, float i, float j, float k) {
		float l = this.method_18132(f);
		float m = this.method_18133();
		float n = this.method_18134();
		float o = this.method_18135();
		float p = this.method_18136();
		float q = (float)(class_3532.method_16436((double)f, this.field_3858, this.field_3874) - field_3873);
		float r = (float)(class_3532.method_16436((double)f, this.field_3838, this.field_3854) - field_3853);
		float s = (float)(class_3532.method_16436((double)f, this.field_3856, this.field_3871) - field_3870);
		int t = this.method_3068(f);
		int u = t >> 16 & 65535;
		int v = t & 65535;
		class_243[] lvs = new class_243[]{
			new class_243((double)(-g * l - j * l), (double)(-h * l), (double)(-i * l - k * l)),
			new class_243((double)(-g * l + j * l), (double)(h * l), (double)(-i * l + k * l)),
			new class_243((double)(g * l + j * l), (double)(h * l), (double)(i * l + k * l)),
			new class_243((double)(g * l - j * l), (double)(-h * l), (double)(i * l - k * l))
		};
		if (this.field_3839 != 0.0F) {
			float w = class_3532.method_16439(f, this.field_3857, this.field_3839);
			float x = class_3532.method_15362(w * 0.5F);
			float y = (float)((double)class_3532.method_15374(w * 0.5F) * arg2.method_19335().field_1352);
			float z = (float)((double)class_3532.method_15374(w * 0.5F) * arg2.method_19335().field_1351);
			float aa = (float)((double)class_3532.method_15374(w * 0.5F) * arg2.method_19335().field_1350);
			class_243 lv = new class_243((double)y, (double)z, (double)aa);

			for (int ab = 0; ab < 4; ab++) {
				lvs[ab] = lv.method_1021(2.0 * lvs[ab].method_1026(lv))
					.method_1019(lvs[ab].method_1021((double)(x * x) - lv.method_1026(lv)))
					.method_1019(lv.method_1036(lvs[ab]).method_1021((double)(2.0F * x)));
			}
		}

		arg.method_1315((double)q + lvs[0].field_1352, (double)r + lvs[0].field_1351, (double)s + lvs[0].field_1350)
			.method_1312((double)n, (double)p)
			.method_1336(this.field_3861, this.field_3842, this.field_3859, this.field_3841)
			.method_1313(u, v)
			.method_1344();
		arg.method_1315((double)q + lvs[1].field_1352, (double)r + lvs[1].field_1351, (double)s + lvs[1].field_1350)
			.method_1312((double)n, (double)o)
			.method_1336(this.field_3861, this.field_3842, this.field_3859, this.field_3841)
			.method_1313(u, v)
			.method_1344();
		arg.method_1315((double)q + lvs[2].field_1352, (double)r + lvs[2].field_1351, (double)s + lvs[2].field_1350)
			.method_1312((double)m, (double)o)
			.method_1336(this.field_3861, this.field_3842, this.field_3859, this.field_3841)
			.method_1313(u, v)
			.method_1344();
		arg.method_1315((double)q + lvs[3].field_1352, (double)r + lvs[3].field_1351, (double)s + lvs[3].field_1350)
			.method_1312((double)m, (double)p)
			.method_1336(this.field_3861, this.field_3842, this.field_3859, this.field_3841)
			.method_1313(u, v)
			.method_1344();
	}

	public float method_18132(float f) {
		return this.field_17867;
	}

	@Override
	public class_703 method_3087(float f) {
		this.field_17867 *= f;
		return super.method_3087(f);
	}

	protected abstract float method_18133();

	protected abstract float method_18134();

	protected abstract float method_18135();

	protected abstract float method_18136();
}
