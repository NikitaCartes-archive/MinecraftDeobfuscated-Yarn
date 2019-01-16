package net.minecraft.util;

import java.util.List;
import java.util.Random;

public class WeightedPicker {
	public static int getWeightSum(List<? extends WeightedPicker.Entry> list) {
		int i = 0;
		int j = 0;

		for (int k = list.size(); j < k; j++) {
			WeightedPicker.Entry entry = (WeightedPicker.Entry)list.get(j);
			i += entry.weight;
		}

		return i;
	}

	public static <T extends WeightedPicker.Entry> T getRandom(Random random, List<T> list, int i) {
		if (i <= 0) {
			throw new IllegalArgumentException();
		} else {
			int j = random.nextInt(i);
			return getAt(list, j);
		}
	}

	public static <T extends WeightedPicker.Entry> T getAt(List<T> list, int i) {
		int j = 0;

		for (int k = list.size(); j < k; j++) {
			T entry = (T)list.get(j);
			i -= entry.weight;
			if (i < 0) {
				return entry;
			}
		}

		return null;
	}

	public static <T extends WeightedPicker.Entry> T getRandom(Random random, List<T> list) {
		return getRandom(random, list, getWeightSum(list));
	}

	public static class Entry {
		protected final int weight;

		public Entry(int i) {
			this.weight = i;
		}
	}
}
