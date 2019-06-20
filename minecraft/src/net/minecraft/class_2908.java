package net.minecraft;

import java.util.List;

public class class_2908 extends class_3754<class_2900> {
	private final double[] field_16569 = this.method_16403();

	public class_2908(class_1937 arg, class_1966 arg2, class_2900 arg3) {
		super(arg, arg2, 4, 8, 128, arg3, false);
	}

	@Override
	protected void method_16405(double[] ds, int i, int j) {
		double d = 684.412;
		double e = 2053.236;
		double f = 8.555150000000001;
		double g = 34.2206;
		int k = -10;
		int l = 3;
		this.method_16413(ds, i, j, 684.412, 2053.236, 8.555150000000001, 34.2206, 3, -10);
	}

	@Override
	protected double[] method_12090(int i, int j) {
		return new double[]{0.0, 0.0};
	}

	@Override
	protected double method_16404(double d, double e, int i) {
		return this.field_16569[i];
	}

	private double[] method_16403() {
		double[] ds = new double[this.method_16408()];

		for (int i = 0; i < this.method_16408(); i++) {
			ds[i] = Math.cos((double)i * Math.PI * 6.0 / (double)this.method_16408()) * 2.0;
			double d = (double)i;
			if (i > this.method_16408() / 2) {
				d = (double)(this.method_16408() - 1 - i);
			}

			if (d < 4.0) {
				d = 4.0 - d;
				ds[i] -= d * d * d * 10.0;
			}
		}

		return ds;
	}

	@Override
	public List<class_1959.class_1964> method_12113(class_1311 arg, class_2338 arg2) {
		if (arg == class_1311.field_6302) {
			if (class_3031.field_13569.method_14024(this.field_12760, arg2)) {
				return class_3031.field_13569.method_13149();
			}

			if (class_3031.field_13569.method_14023(this.field_12760, arg2)
				&& this.field_12760.method_8320(arg2.method_10074()).method_11614() == class_2246.field_10266) {
				return class_3031.field_13569.method_13149();
			}
		}

		return super.method_12113(arg, arg2);
	}

	@Override
	public int method_12100() {
		return this.field_12760.method_8615() + 1;
	}

	@Override
	public int method_12104() {
		return 128;
	}

	@Override
	public int method_16398() {
		return 32;
	}
}
