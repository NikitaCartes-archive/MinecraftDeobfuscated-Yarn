package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public final class SimpleDoubleListPair implements DoubleListPair {
	private final DoubleArrayList mergedList;
	private final IntArrayList field_1376;
	private final IntArrayList field_1378;

	protected SimpleDoubleListPair(DoubleList first, DoubleList second, boolean includeFirstOnly, boolean includeSecondOnly) {
		int i = 0;
		int j = 0;
		double d = Double.NaN;
		int k = first.size();
		int l = second.size();
		int m = k + l;
		this.mergedList = new DoubleArrayList(m);
		this.field_1376 = new IntArrayList(m);
		this.field_1378 = new IntArrayList(m);

		while (true) {
			boolean bl = i < k;
			boolean bl2 = j < l;
			if (!bl && !bl2) {
				if (this.mergedList.isEmpty()) {
					this.mergedList.add(Math.min(first.getDouble(k - 1), second.getDouble(l - 1)));
				}

				return;
			}

			boolean bl3 = bl && (!bl2 || first.getDouble(i) < second.getDouble(j) + 1.0E-7);
			double e = bl3 ? first.getDouble(i++) : second.getDouble(j++);
			if ((i != 0 && bl || bl3 || includeSecondOnly) && (j != 0 && bl2 || !bl3 || includeFirstOnly)) {
				if (!(d >= e - 1.0E-7)) {
					this.field_1376.add(i - 1);
					this.field_1378.add(j - 1);
					this.mergedList.add(e);
					d = e;
				} else if (!this.mergedList.isEmpty()) {
					this.field_1376.set(this.field_1376.size() - 1, i - 1);
					this.field_1378.set(this.field_1378.size() - 1, j - 1);
				}
			}
		}
	}

	@Override
	public boolean forAllOverlappingSections(DoubleListPair.SectionPairPredicate predicate) {
		for (int i = 0; i < this.mergedList.size() - 1; i++) {
			if (!predicate.merge(this.field_1376.getInt(i), this.field_1378.getInt(i), i)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public DoubleList getMergedList() {
		return this.mergedList;
	}
}
