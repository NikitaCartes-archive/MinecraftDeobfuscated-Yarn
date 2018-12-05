package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

public class FractionalDoubleList extends AbstractDoubleList {
	private final int count;

	public FractionalDoubleList(int i) {
		this.count = i;
	}

	@Override
	public double getDouble(int i) {
		return (double)i / (double)this.count;
	}

	public int size() {
		return this.count + 1;
	}
}
