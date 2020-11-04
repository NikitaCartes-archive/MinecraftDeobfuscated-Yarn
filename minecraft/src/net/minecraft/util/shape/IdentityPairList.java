package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;

public class IdentityPairList implements PairList {
	private final DoubleList merged;

	public IdentityPairList(DoubleList values) {
		this.merged = values;
	}

	@Override
	public boolean forEachPair(PairList.Consumer predicate) {
		int i = this.merged.size() - 1;

		for (int j = 0; j < i; j++) {
			if (!predicate.merge(j, j, j)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int size() {
		return this.merged.size();
	}

	@Override
	public DoubleList getPairs() {
		return this.merged;
	}
}
