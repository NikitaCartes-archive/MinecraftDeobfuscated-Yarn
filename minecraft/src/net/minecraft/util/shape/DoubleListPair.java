package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;

interface DoubleListPair {
	DoubleList getMergedList();

	boolean forAllOverlappingSections(DoubleListPair.SectionPairPredicate predicate);

	public interface SectionPairPredicate {
		boolean merge(int i, int j, int k);
	}
}
