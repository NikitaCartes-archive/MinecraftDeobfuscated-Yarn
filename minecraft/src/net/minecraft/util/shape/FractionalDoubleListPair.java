package net.minecraft.util.shape;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public final class FractionalDoubleListPair implements DoubleListPair {
	private final FractionalDoubleList mergedList;
	private final int firstSectionCount;
	private final int secondSectionCount;
	private final int gcd;

	FractionalDoubleListPair(int i, int j) {
		this.mergedList = new FractionalDoubleList((int)VoxelShapes.lcm(i, j));
		this.firstSectionCount = i;
		this.secondSectionCount = j;
		this.gcd = IntMath.gcd(i, j);
	}

	@Override
	public boolean forAllOverlappingSections(DoubleListPair.SectionPairPredicate sectionPairPredicate) {
		int i = this.firstSectionCount / this.gcd;
		int j = this.secondSectionCount / this.gcd;

		for (int k = 0; k <= this.mergedList.size(); k++) {
			if (!sectionPairPredicate.merge(k / j, k / i, k)) {
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
