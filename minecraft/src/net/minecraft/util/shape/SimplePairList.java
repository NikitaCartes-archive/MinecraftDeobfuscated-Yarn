package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleLists;

public class SimplePairList implements PairList {
	private static final DoubleList field_27346 = DoubleLists.unmodifiable(DoubleArrayList.wrap(new double[]{0.0}));
	private final double[] valueIndices;
	private final int[] minValues;
	private final int[] maxValues;
	private final int field_27347;

	public SimplePairList(DoubleList first, DoubleList second, boolean includeFirstOnly, boolean includeSecondOnly) {
		double d = Double.NaN;
		int i = first.size();
		int j = second.size();
		int k = i + j;
		this.valueIndices = new double[k];
		this.minValues = new int[k];
		this.maxValues = new int[k];
		boolean bl = !includeFirstOnly;
		boolean bl2 = !includeSecondOnly;
		int l = 0;
		int m = 0;
		int n = 0;

		while (true) {
			boolean bl3 = m >= i;
			boolean bl4 = n >= j;
			if (bl3 && bl4) {
				this.field_27347 = Math.max(1, l);
				return;
			}

			boolean bl5 = !bl3 && (bl4 || first.getDouble(m) < second.getDouble(n) + 1.0E-7);
			if (bl5) {
				m++;
				if (bl && (n == 0 || bl4)) {
					continue;
				}
			} else {
				n++;
				if (bl2 && (m == 0 || bl3)) {
					continue;
				}
			}

			int o = m - 1;
			int p = n - 1;
			double e = bl5 ? first.getDouble(o) : second.getDouble(p);
			if (!(d >= e - 1.0E-7)) {
				this.minValues[l] = o;
				this.maxValues[l] = p;
				this.valueIndices[l] = e;
				l++;
				d = e;
			} else {
				this.minValues[l - 1] = o;
				this.maxValues[l - 1] = p;
			}
		}
	}

	@Override
	public boolean forEachPair(PairList.Consumer predicate) {
		int i = this.field_27347 - 1;

		for (int j = 0; j < i; j++) {
			if (!predicate.merge(this.minValues[j], this.maxValues[j], j)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int size() {
		return this.field_27347;
	}

	@Override
	public DoubleList getPairs() {
		return (DoubleList)(this.field_27347 <= 1 ? field_27346 : DoubleArrayList.wrap(this.valueIndices, this.field_27347));
	}
}
