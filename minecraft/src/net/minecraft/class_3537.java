package net.minecraft;

import java.util.Random;

public class class_3537 implements class_3757 {
	private final class_3756[] field_15744;

	public class_3537(Random random, int i) {
		this.field_15744 = new class_3756[i];

		for (int j = 0; j < i; j++) {
			this.field_15744[j] = new class_3756(random);
		}
	}

	public double method_15416(double d, double e, double f) {
		return this.method_16453(d, e, f, 0.0, 0.0, false);
	}

	public double method_16453(double d, double e, double f, double g, double h, boolean bl) {
		double i = 0.0;
		double j = 1.0;

		for (class_3756 lv : this.field_15744) {
			i += lv.method_16447(method_16452(d * j), bl ? -lv.field_16589 : method_16452(e * j), method_16452(f * j), g * j, h * j) / j;
			j /= 2.0;
		}

		return i;
	}

	public class_3756 method_16668(int i) {
		return this.field_15744[i];
	}

	public static double method_16452(double d) {
		return d - (double)class_3532.method_15372(d / 3.3554432E7 + 0.5) * 3.3554432E7;
	}

	@Override
	public double method_16454(double d, double e, double f, double g) {
		return this.method_16453(d, e, 0.0, f, g, false);
	}
}
