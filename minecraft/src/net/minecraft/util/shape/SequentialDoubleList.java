package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

public class SequentialDoubleList extends AbstractDoubleList {
	private final int count;
	private final int offset;

	SequentialDoubleList(int i, int j) {
		this.count = i;
		this.offset = j;
	}

	@Override
	public double getDouble(int i) {
		return (double)(this.offset + i);
	}

	public int size() {
		return this.count + 1;
	}
}
