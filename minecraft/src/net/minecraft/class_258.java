package net.minecraft;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

public class class_258 extends AbstractDoubleList {
	private final int field_1383;
	private final int field_1382;

	class_258(int i, int j) {
		this.field_1383 = i;
		this.field_1382 = j;
	}

	@Override
	public double getDouble(int i) {
		return (double)(this.field_1382 + i);
	}

	public int size() {
		return this.field_1383 + 1;
	}
}
