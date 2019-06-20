package net.minecraft;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

public class class_246 extends AbstractDoubleList {
	private final int field_1365;

	class_246(int i) {
		this.field_1365 = i;
	}

	@Override
	public double getDouble(int i) {
		return (double)i / (double)this.field_1365;
	}

	public int size() {
		return this.field_1365 + 1;
	}
}
