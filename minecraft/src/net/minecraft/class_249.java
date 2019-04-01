package net.minecraft;

import it.unimi.dsi.fastutil.doubles.DoubleList;

final class class_249 extends class_265 {
	class_249(class_251 arg) {
		super(arg);
	}

	@Override
	protected DoubleList method_1109(class_2350.class_2351 arg) {
		return new class_246(this.field_1401.method_1051(arg));
	}

	@Override
	protected int method_1100(class_2350.class_2351 arg, double d) {
		int i = this.field_1401.method_1051(arg);
		return class_3532.method_15340(class_3532.method_15357(d * (double)i), -1, i);
	}
}
