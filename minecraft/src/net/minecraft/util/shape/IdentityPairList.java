package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;

public class IdentityPairList implements PairList {
	private final DoubleList merged;

	public IdentityPairList(DoubleList doubleList) {
		this.merged = doubleList;
	}

	@Override
	public boolean forEachPair(PairList.SectionPairPredicate predicate) {
		for (int i = 0; i <= this.merged.size(); i++) {
			if (!predicate.merge(i, i, i)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public DoubleList getPairs() {
		return this.merged;
	}
}
