package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;

interface PairList {
	DoubleList getPairs();

	boolean forEachPair(PairList.SectionPairPredicate predicate);

	public interface SectionPairPredicate {
		boolean merge(int i, int j, int k);
	}
}
