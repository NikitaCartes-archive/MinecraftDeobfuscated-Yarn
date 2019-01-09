package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_703 {
	private static final class_238 field_3860 = new class_238(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	protected final class_1937 field_3851;
	protected double field_3858;
	protected double field_3838;
	protected double field_3856;
	protected double field_3874;
	protected double field_3854;
	protected double field_3871;
	protected double field_3852;
	protected double field_3869;
	protected double field_3850;
	private class_238 field_3872 = field_3860;
	protected boolean field_3845;
	protected boolean field_3862;
	protected boolean field_3843;
	protected float field_3849 = 0.6F;
	protected float field_3867 = 1.8F;
	protected final Random field_3840 = new Random();
	protected int field_3868;
	protected int field_3848;
	protected final float field_3865;
	protected final float field_3846;
	protected int field_3866;
	protected int field_3847;
	protected float field_3863;
	protected float field_3844;
	protected float field_3861;
	protected float field_3842;
	protected float field_3859;
	protected float field_3841 = 1.0F;
	protected class_1058 field_3855;
	protected float field_3839;
	protected float field_3857;
	public static double field_3873;
	public static double field_3853;
	public static double field_3870;
	public static class_243 field_3864;

	protected class_703(class_1937 arg, double d, double e, double f) {
		this.field_3851 = arg;
		this.method_3080(0.2F, 0.2F);
		this.method_3063(d, e, f);
		this.field_3858 = d;
		this.field_3838 = e;
		this.field_3856 = f;
		this.field_3861 = 1.0F;
		this.field_3842 = 1.0F;
		this.field_3859 = 1.0F;
		this.field_3865 = this.field_3840.nextFloat() * 3.0F;
		this.field_3846 = this.field_3840.nextFloat() * 3.0F;
		this.field_3863 = (this.field_3840.nextFloat() * 0.5F + 0.5F) * 2.0F;
		this.field_3847 = (int)(4.0F / (this.field_3840.nextFloat() * 0.9F + 0.1F));
		this.field_3866 = 0;
		this.field_3862 = true;
	}

	public class_703(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		this(arg, d, e, f);
		this.field_3852 = g + (Math.random() * 2.0 - 1.0) * 0.4F;
		this.field_3869 = h + (Math.random() * 2.0 - 1.0) * 0.4F;
		this.field_3850 = i + (Math.random() * 2.0 - 1.0) * 0.4F;
		float j = (float)(Math.random() + Math.random() + 1.0) * 0.15F;
		float k = class_3532.method_15368(this.field_3852 * this.field_3852 + this.field_3869 * this.field_3869 + this.field_3850 * this.field_3850);
		this.field_3852 = this.field_3852 / (double)k * (double)j * 0.4F;
		this.field_3869 = this.field_3869 / (double)k * (double)j * 0.4F + 0.1F;
		this.field_3850 = this.field_3850 / (double)k * (double)j * 0.4F;
	}

	public class_703 method_3075(float f) {
		this.field_3852 *= (double)f;
		this.field_3869 = (this.field_3869 - 0.1F) * (double)f + 0.1F;
		this.field_3850 *= (double)f;
		return this;
	}

	public class_703 method_3087(float f) {
		this.method_3080(0.2F * f, 0.2F * f);
		this.field_3863 *= f;
		return this;
	}

	public void method_3084(float f, float g, float h) {
		this.field_3861 = f;
		this.field_3842 = g;
		this.field_3859 = h;
	}

	public void method_3083(float f) {
		this.field_3841 = f;
	}

	public boolean method_3071() {
		return false;
	}

	public float method_3081() {
		return this.field_3861;
	}

	public float method_3065() {
		return this.field_3842;
	}

	public float method_3066() {
		return this.field_3859;
	}

	public void method_3077(int i) {
		this.field_3847 = i;
	}

	public int method_3082() {
		return this.field_3847;
	}

	public void method_3070() {
		this.field_3858 = this.field_3874;
		this.field_3838 = this.field_3854;
		this.field_3856 = this.field_3871;
		if (this.field_3866++ >= this.field_3847) {
			this.method_3085();
		}

		this.field_3869 = this.field_3869 - 0.04 * (double)this.field_3844;
		this.method_3069(this.field_3852, this.field_3869, this.field_3850);
		this.field_3852 *= 0.98F;
		this.field_3869 *= 0.98F;
		this.field_3850 *= 0.98F;
		if (this.field_3845) {
			this.field_3852 *= 0.7F;
			this.field_3850 *= 0.7F;
		}
	}

	public void method_3074(class_287 arg, class_1297 arg2, float f, float g, float h, float i, float j, float k) {
		float l = (float)this.field_3868 / 32.0F;
		float m = l + 0.03121875F;
		float n = (float)this.field_3848 / 32.0F;
		float o = n + 0.03121875F;
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
		if (this.field_3839 != 0.0F) {
			float w = class_3532.method_16439(f, this.field_3857, this.field_3839);
			float x = class_3532.method_15362(w * 0.5F);
			float y = class_3532.method_15374(w * 0.5F) * (float)field_3864.field_1352;
			float z = class_3532.method_15374(w * 0.5F) * (float)field_3864.field_1351;
			float aa = class_3532.method_15374(w * 0.5F) * (float)field_3864.field_1350;
			class_243 lv = new class_243((double)y, (double)z, (double)aa);

			for (int ab = 0; ab < 4; ab++) {
				lvs[ab] = lv.method_1021(2.0 * lvs[ab].method_1026(lv))
					.method_1019(lvs[ab].method_1021((double)(x * x) - lv.method_1026(lv)))
					.method_1019(lv.method_1036(lvs[ab]).method_1021((double)(2.0F * x)));
			}
		}

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

	public int method_3079() {
		return 0;
	}

	public void method_3078(class_1058 arg) {
		int i = this.method_3079();
		if (i == 1) {
			this.field_3855 = arg;
		} else {
			throw new RuntimeException("Invalid call to Particle.setTex, use coordinate methods");
		}
	}

	public void method_3076(int i) {
		if (this.method_3079() != 0) {
			throw new RuntimeException("Invalid call to Particle.setMiscTex");
		} else {
			this.field_3868 = i % 16;
			this.field_3848 = i / 16;
		}
	}

	public void method_3073() {
		this.field_3868++;
	}

	public String toString() {
		return this.getClass().getSimpleName()
			+ ", Pos ("
			+ this.field_3874
			+ ","
			+ this.field_3854
			+ ","
			+ this.field_3871
			+ "), RGBA ("
			+ this.field_3861
			+ ","
			+ this.field_3842
			+ ","
			+ this.field_3859
			+ ","
			+ this.field_3841
			+ "), Age "
			+ this.field_3866;
	}

	public void method_3085() {
		this.field_3843 = true;
	}

	protected void method_3080(float f, float g) {
		if (f != this.field_3849 || g != this.field_3867) {
			this.field_3849 = f;
			this.field_3867 = g;
			class_238 lv = this.method_3064();
			double d = (lv.field_1323 + lv.field_1320 - (double)f) / 2.0;
			double e = (lv.field_1321 + lv.field_1324 - (double)f) / 2.0;
			this.method_3067(new class_238(d, lv.field_1322, e, d + (double)this.field_3849, lv.field_1322 + (double)this.field_3867, e + (double)this.field_3849));
		}
	}

	public void method_3063(double d, double e, double f) {
		this.field_3874 = d;
		this.field_3854 = e;
		this.field_3871 = f;
		float g = this.field_3849 / 2.0F;
		float h = this.field_3867;
		this.method_3067(new class_238(d - (double)g, e, f - (double)g, d + (double)g, e + (double)h, f + (double)g));
	}

	public void method_3069(double d, double e, double f) {
		double g = d;
		double h = e;
		double i = f;
		if (this.field_3862 && (d != 0.0 || e != 0.0 || f != 0.0)) {
			class_3538<class_265> lv = new class_3538<>(this.field_3851.method_8609(null, this.method_3064(), d, e, f));
			e = class_259.method_1085(class_2350.class_2351.field_11052, this.method_3064(), lv.method_15418(), e);
			this.method_3067(this.method_3064().method_989(0.0, e, 0.0));
			d = class_259.method_1085(class_2350.class_2351.field_11048, this.method_3064(), lv.method_15418(), d);
			if (d != 0.0) {
				this.method_3067(this.method_3064().method_989(d, 0.0, 0.0));
			}

			f = class_259.method_1085(class_2350.class_2351.field_11051, this.method_3064(), lv.method_15418(), f);
			if (f != 0.0) {
				this.method_3067(this.method_3064().method_989(0.0, 0.0, f));
			}
		} else {
			this.method_3067(this.method_3064().method_989(d, e, f));
		}

		this.method_3072();
		this.field_3845 = h != e && h < 0.0;
		if (g != d) {
			this.field_3852 = 0.0;
		}

		if (i != f) {
			this.field_3850 = 0.0;
		}
	}

	protected void method_3072() {
		class_238 lv = this.method_3064();
		this.field_3874 = (lv.field_1323 + lv.field_1320) / 2.0;
		this.field_3854 = lv.field_1322;
		this.field_3871 = (lv.field_1321 + lv.field_1324) / 2.0;
	}

	public int method_3068(float f) {
		class_2338 lv = new class_2338(this.field_3874, this.field_3854, this.field_3871);
		return this.field_3851.method_8591(lv) ? this.field_3851.method_8313(lv, 0) : 0;
	}

	public boolean method_3086() {
		return !this.field_3843;
	}

	public class_238 method_3064() {
		return this.field_3872;
	}

	public void method_3067(class_238 arg) {
		this.field_3872 = arg;
	}
}
