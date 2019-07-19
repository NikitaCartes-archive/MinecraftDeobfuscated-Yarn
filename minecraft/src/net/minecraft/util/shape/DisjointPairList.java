package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class DisjointPairList extends AbstractDoubleList implements PairList {
	private final DoubleList first;
	private final DoubleList second;
	private final boolean field_1380;

	public DisjointPairList(DoubleList first, DoubleList second, boolean inverted) {
		this.first = first;
		this.second = second;
		this.field_1380 = inverted;
	}

	public int size() {
		return this.first.size() + this.second.size();
	}

	@Override
	public boolean forEachPair(PairList.SectionPairPredicate predicate) {
		return this.field_1380 ? this.method_1067((i, j, k) -> predicate.merge(j, i, k)) : this.method_1067(predicate);
	}

	private boolean method_1067(PairList.SectionPairPredicate sectionPairPredicate) {
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
	public double getDouble(int position) {
		return position < this.first.size() ? this.first.getDouble(position) : this.second.getDouble(position - this.first.size());
	}

	@Override
	public DoubleList getPairs() {
		return this;
	}
}
