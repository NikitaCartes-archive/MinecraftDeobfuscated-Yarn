package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class WeightedList<U> {
	private final List<WeightedList<U>.Entry<? extends U>> entries = Lists.<WeightedList<U>.Entry<? extends U>>newArrayList();
	private final Random random;

	public WeightedList() {
		this(new Random());
	}

	public WeightedList(Random random) {
		this.random = random;
	}

	public void add(U object, int i) {
		this.entries.add(new WeightedList.Entry(object, i));
	}

	public void shuffle() {
		this.entries.forEach(entry -> entry.setShuffledOrder(this.random.nextFloat()));
		this.entries.sort(Comparator.comparingDouble(WeightedList.Entry::getShuffledOrder));
	}

	public Stream<? extends U> stream() {
		return this.entries.stream().map(WeightedList.Entry::getElement);
	}

	public String toString() {
		return "WeightedList[" + this.entries + "]";
	}

	class Entry<T> {
		private final T field_18400;
		private final int weight;
		private double shuffledOrder;

		private Entry(T object, int i) {
			this.weight = i;
			this.field_18400 = object;
		}

		public double getShuffledOrder() {
			return this.shuffledOrder;
		}

		public void setShuffledOrder(float f) {
			this.shuffledOrder = -Math.pow((double)f, (double)(1.0F / (float)this.weight));
		}

		public T getElement() {
			return this.field_18400;
		}

		public String toString() {
			return "" + this.weight + ":" + this.field_18400;
		}
	}
}
