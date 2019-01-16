package net.minecraft.util;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class OffsetDoubleList extends AbstractDoubleList {
	private final DoubleList oldList;
	private final double offset;

	public OffsetDoubleList(DoubleList doubleList, double d) {
		this.oldList = doubleList;
		this.offset = d;
	}

	@Override
	public double getDouble(int i) {
		return this.oldList.getDouble(i) + this.offset;
	}

	public int size() {
		return this.oldList.size();
	}
}
