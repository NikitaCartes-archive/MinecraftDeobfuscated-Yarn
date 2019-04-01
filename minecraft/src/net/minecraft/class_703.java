package net.minecraft;

import java.util.Random;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_703 {
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
	protected boolean field_3862 = true;
	protected boolean field_3843;
	protected float field_3849 = 0.6F;
	protected float field_3867 = 1.8F;
	protected final Random field_3840 = new Random();
	protected int field_3866;
	protected int field_3847;
	protected float field_3844;
	protected float field_3861 = 1.0F;
	protected float field_3842 = 1.0F;
	protected float field_3859 = 1.0F;
	protected float field_3841 = 1.0F;
	protected float field_3839;
	protected float field_3857;
	public static double field_3873;
	public static double field_3853;
	public static double field_3870;

	protected class_703(class_1937 arg, double d, double e, double f) {
		this.field_3851 = arg;
		this.method_3080(0.2F, 0.2F);
		this.method_3063(d, e, f);
		this.field_3858 = d;
		this.field_3838 = e;
		this.field_3856 = f;
		this.field_3847 = (int)(4.0F / (this.field_3840.nextFloat() * 0.9F + 0.1F));
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
		return this;
	}

	public void method_3084(float f, float g, float h) {
		this.field_3861 = f;
		this.field_3842 = g;
		this.field_3859 = h;
	}

	protected void method_3083(float f) {
		this.field_3841 = f;
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
		} else {
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
	}

	public abstract void method_3074(class_287 arg, class_4184 arg2, float f, float g, float h, float i, float j, float k);

	public abstract class_3999 method_18122();

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
		if (this.field_3862 && d != 0.0 || e != 0.0 || f != 0.0) {
			class_243 lv = class_1297.method_17833(
				new class_243(d, e, f), this.method_3064(), this.field_3851, class_3726.method_16194(), new class_3538<>(Stream.empty())
			);
			d = lv.field_1352;
			e = lv.field_1351;
			f = lv.field_1350;
		}

		if (d != 0.0 || e != 0.0 || f != 0.0) {
			this.method_3067(this.method_3064().method_989(d, e, f));
			this.method_3072();
		}

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

	protected int method_3068(float f) {
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
