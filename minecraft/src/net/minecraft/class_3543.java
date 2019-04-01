package net.minecraft;

import java.util.Random;

public class class_3543 implements class_3757 {
	private final class_3541[] field_15770;
	private final int field_15769;

	public class_3543(Random random, int i) {
		this.field_15769 = i;
		this.field_15770 = new class_3541[i];

		for (int j = 0; j < i; j++) {
			this.field_15770[j] = new class_3541(random);
		}
	}

	public double method_15437(double d, double e) {
		return this.method_16451(d, e, false);
	}

	public double method_16451(double d, double e, boolean bl) {
		double f = 0.0;
		double g = 1.0;

		for (int i = 0; i < this.field_15769; i++) {
			f += this.field_15770[i].method_15433(d * g + (bl ? this.field_15770[i].field_15763 : 0.0), e * g + (bl ? this.field_15770[i].field_15762 : 0.0)) / g;
			g /= 2.0;
		}

		return f;
	}

	@Override
	public double method_16454(double d, double e, double f, double g) {
		return this.method_16451(d, e, true) * 0.55;
	}
}
