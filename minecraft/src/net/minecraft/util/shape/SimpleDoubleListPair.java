package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public final class SimpleDoubleListPair implements DoubleListPair {
	private final DoubleArrayList mergedList;
	private final IntArrayList field_1376;
	private final IntArrayList field_1378;

	protected SimpleDoubleListPair(DoubleList doubleList, DoubleList doubleList2, boolean bl, boolean bl2) {
		int i = 0;
		int j = 0;
		double d = Double.NaN;
		int k = doubleList.size();
		int l = doubleList2.size();
		int m = k + l;
		this.mergedList = new DoubleArrayList(m);
		this.field_1376 = new IntArrayList(m);
		this.field_1378 = new IntArrayList(m);

		while (true) {
			boolean bl3 = i < k;
			boolean bl4 = j < l;
			if (!bl3 && !bl4) {
				if (this.mergedList.isEmpty()) {
					this.mergedList.add(Math.min(doubleList.getDouble(k - 1), doubleList2.getDouble(l - 1)));
				}

				return;
			}

			boolean bl5 = bl3 && (!bl4 || doubleList.getDouble(i) < doubleList2.getDouble(j) + 1.0E-7);
			double e = bl5 ? doubleList.getDouble(i++) : doubleList2.getDouble(j++);
			if ((i != 0 && bl3 || bl5 || bl2) && (j != 0 && bl4 || !bl5 || bl)) {
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
	public boolean forAllOverlappingSections(DoubleListPair.SectionPairPredicate sectionPairPredicate) {
		for (int i = 0; i < this.mergedList.size() - 1; i++) {
			if (!sectionPairPredicate.merge(this.field_1376.getInt(i), this.field_1378.getInt(i), i)) {
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
