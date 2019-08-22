package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

public class FractionalDoubleList extends AbstractDoubleList {
	private final int sectionCount;

	FractionalDoubleList(int i) {
		this.sectionCount = i;
	}

	@Override
	public double getDouble(int i) {
		return (double)i / (double)this.sectionCount;
	}

	public int size() {
		return this.sectionCount + 1;
	}
}
