package net.minecraft.util.collection;

import java.util.List;
import java.util.Optional;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

public class Weighting {
	private Weighting() {
	}

	public static int getWeightSum(List<? extends Weighted> pool) {
		long l = 0L;

		for (Weighted weighted : pool) {
			l += (long)weighted.getWeight().getValue();
		}

		if (l > 2147483647L) {
			throw new IllegalArgumentException("Sum of weights must be <= 2147483647");
		} else {
			return (int)l;
		}
	}

	public static <T extends Weighted> Optional<T> getRandom(Random random, List<T> pool, int totalWeight) {
		if (totalWeight < 0) {
			throw (IllegalArgumentException)Util.throwOrPause((T)(new IllegalArgumentException("Negative total weight in getRandomItem")));
		} else if (totalWeight == 0) {
			return Optional.empty();
		} else {
			int i = random.nextInt(totalWeight);
			return getAt(pool, i);
		}
	}

	public static <T extends Weighted> Optional<T> getAt(List<T> pool, int totalWeight) {
		for (T weighted : pool) {
			totalWeight -= weighted.getWeight().getValue();
			if (totalWeight < 0) {
				return Optional.of(weighted);
			}
		}

		return Optional.empty();
	}

	public static <T extends Weighted> Optional<T> getRandom(Random random, List<T> pool) {
		return getRandom(random, pool, getWeightSum(pool));
	}
}
