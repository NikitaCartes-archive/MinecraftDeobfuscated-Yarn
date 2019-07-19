package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

public class FractionalDoubleList extends AbstractDoubleList {
	private final int sectionCount;

	FractionalDoubleList(int sectionCount) {
		this.sectionCount = sectionCount;
	}

	@Override
	public double getDouble(int position) {
		return (double)position / (double)this.sectionCount;
	}

	public int size() {
		return this.sectionCount + 1;
	}
}
