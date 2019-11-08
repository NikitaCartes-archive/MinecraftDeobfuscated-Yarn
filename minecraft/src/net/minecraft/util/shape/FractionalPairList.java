package net.minecraft.util.shape;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public final class FractionalPairList implements PairList {
	private final FractionalDoubleList mergedList;
	private final int firstSectionCount;
	private final int secondSectionCount;
	private final int gcd;

	FractionalPairList(int firstSectionCount, int secondSectionCount) {
		this.mergedList = new FractionalDoubleList((int)VoxelShapes.lcm(firstSectionCount, secondSectionCount));
		this.firstSectionCount = firstSectionCount;
		this.secondSectionCount = secondSectionCount;
		this.gcd = IntMath.gcd(firstSectionCount, secondSectionCount);
	}

	@Override
	public boolean forEachPair(PairList.Consumer predicate) {
		int i = this.firstSectionCount / this.gcd;
		int j = this.secondSectionCount / this.gcd;

		for (int k = 0; k <= this.mergedList.size(); k++) {
			if (!predicate.merge(k / j, k / i, k)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public DoubleList getPairs() {
		return this.mergedList;
	}
}
