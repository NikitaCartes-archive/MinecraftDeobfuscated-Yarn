package net.minecraft;

public class class_2914 extends class_3754<class_2916> {
	private final class_2338 field_13268;

	public class_2914(class_1936 arg, class_1966 arg2, class_2916 arg3) {
		super(arg, arg2, 8, 4, 128, arg3, true);
		this.field_13268 = arg3.method_12652();
	}

	@Override
	protected void method_16405(double[] ds, int i, int j) {
		double d = 1368.824;
		double e = 684.412;
		double f = 17.110300000000002;
		double g = 4.277575000000001;
		int k = 64;
		int l = -3000;
		this.method_16413(ds, i, j, 1368.824, 684.412, 17.110300000000002, 4.277575000000001, 64, -3000);
	}

	@Override
	protected double[] method_12090(int i, int j) {
		return new double[]{(double)this.field_12761.method_8757(i, j), 0.0};
	}

	@Override
	protected double method_16404(double d, double e, int i) {
		return 8.0 - d;
	}

	@Override
	protected double method_16409() {
		return (double)((int)super.method_16409() / 2);
	}

	@Override
	protected double method_16410() {
		return 8.0;
	}

	@Override
	public int method_12100() {
		return 50;
	}

	@Override
	public int method_16398() {
		return 0;
	}
}
