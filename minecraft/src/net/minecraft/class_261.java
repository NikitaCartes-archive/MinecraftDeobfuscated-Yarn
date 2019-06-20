package net.minecraft;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class class_261 extends AbstractDoubleList {
	private final DoubleList field_1387;
	private final double field_1386;

	public class_261(DoubleList doubleList, double d) {
		this.field_1387 = doubleList;
		this.field_1386 = d;
	}

	@Override
	public double getDouble(int i) {
		return this.field_1387.getDouble(i) + this.field_1386;
	}

	public int size() {
		return this.field_1387.size();
	}
}
