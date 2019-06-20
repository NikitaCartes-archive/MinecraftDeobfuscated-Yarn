package net.minecraft;

import it.unimi.dsi.fastutil.doubles.DoubleList;

public class class_263 extends class_265 {
	private final class_265 field_1397;
	private final class_2350.class_2351 field_1396;
	private static final DoubleList field_1395 = new class_246(1);

	public class_263(class_265 arg, class_2350.class_2351 arg2, int i) {
		super(method_1088(arg.field_1401, arg2, i));
		this.field_1397 = arg;
		this.field_1396 = arg2;
	}

	private static class_251 method_1088(class_251 arg, class_2350.class_2351 arg2, int i) {
		return new class_262(
			arg,
			arg2.method_10173(i, 0, 0),
			arg2.method_10173(0, i, 0),
			arg2.method_10173(0, 0, i),
			arg2.method_10173(i + 1, arg.field_1374, arg.field_1374),
			arg2.method_10173(arg.field_1373, i + 1, arg.field_1373),
			arg2.method_10173(arg.field_1372, arg.field_1372, i + 1)
		);
	}

	@Override
	protected DoubleList method_1109(class_2350.class_2351 arg) {
		return arg == this.field_1396 ? field_1395 : this.field_1397.method_1109(arg);
	}
}
