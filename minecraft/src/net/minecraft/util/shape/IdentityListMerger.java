package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;

public class IdentityListMerger implements DoubleListPair {
	private final DoubleList merged;

	public IdentityListMerger(DoubleList doubleList) {
		this.merged = doubleList;
	}

	@Override
	public boolean forAllOverlappingSections(DoubleListPair.SectionPairPredicate sectionPairPredicate) {
		for (int i = 0; i <= this.merged.size(); i++) {
			if (!sectionPairPredicate.merge(i, i, i)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public DoubleList getMergedList() {
		return this.merged;
	}
}
