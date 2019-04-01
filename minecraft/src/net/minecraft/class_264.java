package net.minecraft;

import it.unimi.dsi.fastutil.doubles.DoubleList;

public final class class_264 extends class_265 {
	private final int field_1400;
	private final int field_1399;
	private final int field_1398;

	public class_264(class_251 arg, int i, int j, int k) {
		super(arg);
		this.field_1400 = i;
		this.field_1399 = j;
		this.field_1398 = k;
	}

	@Override
	protected DoubleList method_1109(class_2350.class_2351 arg) {
		return new class_258(this.field_1401.method_1051(arg), arg.method_10173(this.field_1400, this.field_1399, this.field_1398));
	}
}
