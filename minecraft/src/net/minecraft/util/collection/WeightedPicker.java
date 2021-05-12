package net.minecraft.util.collection;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WeightedPicker {
	static final Logger LOGGER = LogManager.getLogger();

	public static int getWeightSum(List<? extends WeightedPicker.Entry> list) {
		long l = 0L;

		for (WeightedPicker.Entry entry : list) {
			l += (long)entry.weight;
		}

		if (l > 2147483647L) {
			throw new IllegalArgumentException("Sum of weights must be <= 2147483647");
		} else {
			return (int)l;
		}
	}

	public static <T extends WeightedPicker.Entry> Optional<T> getRandom(Random random, List<T> list, int weightSum) {
		if (weightSum < 0) {
			throw (IllegalArgumentException)Util.throwOrPause((T)(new IllegalArgumentException("Negative total weight in getRandomItem")));
		} else if (weightSum == 0) {
			return Optional.empty();
		} else {
			int i = random.nextInt(weightSum);
			return getAt(list, i);
		}
	}

	public static <T extends WeightedPicker.Entry> Optional<T> getAt(List<T> list, int weightMark) {
		for (T entry : list) {
			weightMark -= entry.weight;
			if (weightMark < 0) {
				return Optional.of(entry);
			}
		}

		return Optional.empty();
	}

	public static <T extends WeightedPicker.Entry> Optional<T> getRandom(Random random, List<T> list) {
		return getRandom(random, list, getWeightSum(list));
	}

	public static class Entry {
		protected final int weight;

		public Entry(int weight) {
			if (weight < 0) {
				throw (IllegalArgumentException)Util.throwOrPause(new IllegalArgumentException("Weight should be >= 0"));
			} else {
				if (weight == 0 && SharedConstants.isDevelopment) {
					WeightedPicker.LOGGER.warn("Found 0 weight, make sure this is intentional!");
				}

				this.weight = weight;
			}
		}
	}
}
