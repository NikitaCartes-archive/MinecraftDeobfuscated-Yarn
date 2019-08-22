package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class DisjointDoubleListPair extends AbstractDoubleList implements DoubleListPair {
	private final DoubleList first;
	private final DoubleList second;
	private final boolean field_1380;

	public DisjointDoubleListPair(DoubleList doubleList, DoubleList doubleList2, boolean bl) {
		this.first = doubleList;
		this.second = doubleList2;
		this.field_1380 = bl;
	}

	public int size() {
		return this.first.size() + this.second.size();
	}

	@Override
	public boolean forAllOverlappingSections(DoubleListPair.SectionPairPredicate sectionPairPredicate) {
		return this.field_1380 ? this.method_1067((i, j, k) -> sectionPairPredicate.merge(j, i, k)) : this.method_1067(sectionPairPredicate);
	}

	private boolean method_1067(DoubleListPair.SectionPairPredicate sectionPairPredicate) {
		int i = this.first.size() - 1;

		for (int j = 0; j < i; j++) {
			if (!sectionPairPredicate.merge(j, -1, j)) {
				return false;
			}
		}

		if (!sectionPairPredicate.merge(i, -1, i)) {
			return false;
		} else {
			for (int jx = 0; jx < this.second.size(); jx++) {
				if (!sectionPairPredicate.merge(i, jx, i + 1 + jx)) {
					return false;
				}
			}

			return true;
		}
	}

	@Override
	public double getDouble(int i) {
		return i < this.first.size() ? this.first.getDouble(i) : this.second.getDouble(i - this.first.size());
	}

	@Override
	public DoubleList getMergedList() {
		return this;
	}
}
