package net.minecraft;

import java.util.List;

public class class_2912 extends class_3754<class_2906> {
	private static final float[] field_13254 = class_156.method_654(new float[25], fs -> {
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				float f = 10.0F / class_3532.method_15355((float)(i * i + j * j) + 0.2F);
				fs[i + 2 + (j + 2) * 5] = f;
			}
		}
	});
	private final class_3537 field_16583;
	private final boolean field_16582;
	private final class_2910 field_13245 = new class_2910();
	private final class_3769 field_16650 = new class_3769();
	private final class_4274 field_19181 = new class_4274();

	public class_2912(class_1936 arg, class_1966 arg2, class_2906 arg3) {
		super(arg, arg2, 4, 8, 256, arg3, true);
		this.field_16577.method_12660(2620);
		this.field_16583 = new class_3537(this.field_16577, 16);
		this.field_16582 = arg.method_8401().method_153() == class_1942.field_9267;
	}

	@Override
	public void method_12107(class_3233 arg) {
		int i = arg.method_14336();
		int j = arg.method_14339();
		class_1959 lv = arg.method_8392(i, j).method_12036()[0];
		class_2919 lv2 = new class_2919();
		lv2.method_12661(arg.method_8412(), i << 4, j << 4);
		class_1948.method_8661(arg, lv, i, j, lv2);
	}

	@Override
	protected void method_16405(double[] ds, int i, int j) {
		double d = 684.412F;
		double e = 684.412F;
		double f = 8.555149841308594;
		double g = 4.277574920654297;
		int k = -10;
		int l = 3;
		this.method_16413(ds, i, j, 684.412F, 684.412F, 8.555149841308594, 4.277574920654297, 3, -10);
	}

	@Override
	protected double method_16404(double d, double e, int i) {
		double f = 8.5;
		double g = ((double)i - (8.5 + d * 8.5 / 8.0 * 4.0)) * 12.0 * 128.0 / 256.0 / e;
		if (g < 0.0) {
			g *= 4.0;
		}

		return g;
	}

	@Override
	protected double[] method_12090(int i, int j) {
		double[] ds = new double[2];
		float f = 0.0F;
		float g = 0.0F;
		float h = 0.0F;
		int k = 2;
		float l = this.field_12761.method_16360(i, j).method_8695();

		for (int m = -2; m <= 2; m++) {
			for (int n = -2; n <= 2; n++) {
				class_1959 lv = this.field_12761.method_16360(i + m, j + n);
				float o = lv.method_8695();
				float p = lv.method_8686();
				if (this.field_16582 && o > 0.0F) {
					o = 1.0F + o * 2.0F;
					p = 1.0F + p * 4.0F;
				}

				float q = field_13254[m + 2 + (n + 2) * 5] / (o + 2.0F);
				if (lv.method_8695() > l) {
					q /= 2.0F;
				}

				f += p * q;
				g += o * q;
				h += q;
			}
		}

		f /= h;
		g /= h;
		f = f * 0.9F + 0.1F;
		g = (g * 4.0F - 1.0F) / 8.0F;
		ds[0] = (double)g + this.method_16414(i, j);
		ds[1] = (double)f;
		return ds;
	}

	private double method_16414(int i, int j) {
		double d = this.field_16583.method_16453((double)(i * 200), 10.0, (double)(j * 200), 1.0, 0.0, true) / 8000.0;
		if (d < 0.0) {
			d = -d * 0.3;
		}

		d = d * 3.0 - 2.0;
		if (d < 0.0) {
			d /= 28.0;
		} else {
			if (d > 1.0) {
				d = 1.0;
			}

			d /= 40.0;
		}

		return d;
	}

	@Override
	public List<class_1959.class_1964> method_12113(class_1311 arg, class_2338 arg2) {
		if (class_3031.field_13520.method_14029(this.field_12760, arg2)) {
			if (arg == class_1311.field_6302) {
				return class_3031.field_13520.method_13149();
			}

			if (arg == class_1311.field_6294) {
				return class_3031.field_13520.method_16140();
			}
		} else if (arg == class_1311.field_6302) {
			if (class_3031.field_16655.method_14023(this.field_12760, arg2)) {
				return class_3031.field_16655.method_13149();
			}

			if (class_3031.field_13588.method_14023(this.field_12760, arg2)) {
				return class_3031.field_13588.method_13149();
			}
		}

		return super.method_12113(arg, arg2);
	}

	@Override
	public void method_12099(class_3218 arg, boolean bl, boolean bl2) {
		this.field_13245.method_12639(arg, bl, bl2);
		this.field_16650.method_16574(arg, bl, bl2);
		this.field_19181.method_20261(arg, bl, bl2);
	}

	@Override
	public int method_12100() {
		return this.field_12760.method_8615() + 1;
	}

	@Override
	public int method_16398() {
		return 63;
	}
}
