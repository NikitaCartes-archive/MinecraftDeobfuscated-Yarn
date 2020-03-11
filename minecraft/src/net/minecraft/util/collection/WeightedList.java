package net.minecraft.util.collection;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class WeightedList<U> {
	protected final List<WeightedList<U>.Entry<? extends U>> entries = Lists.<WeightedList<U>.Entry<? extends U>>newArrayList();
	private final Random random;

	public WeightedList(Random random) {
		this.random = random;
	}

	public WeightedList() {
		this(new Random());
	}

	public <T> WeightedList(Dynamic<T> dynamic, Function<Dynamic<T>, U> function) {
		this();
		dynamic.asStream().forEach(dynamicx -> dynamicx.get("data").map(dynamic2 -> {
				U object = (U)function.apply(dynamic2);
				int i = dynamicx.get("weight").asInt(1);
				return this.add(object, i);
			}));
	}

	public <T> T serialize(DynamicOps<T> ops, Function<U, Dynamic<T>> entrySerializer) {
		return ops.createList(
			this.streamEntries()
				.map(
					entry -> ops.createMap(
							ImmutableMap.<T, T>builder()
								.put(ops.createString("data"), (T)((Dynamic)entrySerializer.apply(entry.getElement())).getValue())
								.put(ops.createString("weight"), ops.createInt(entry.getWeight()))
								.build()
						)
				)
		);
	}

	public WeightedList<U> add(U item, int weight) {
		this.entries.add(new WeightedList.Entry(item, weight));
		return this;
	}

	public WeightedList<U> shuffle() {
		return this.shuffle(this.random);
	}

	public WeightedList<U> shuffle(Random random) {
		this.entries.forEach(entry -> entry.setShuffledOrder(random.nextFloat()));
		this.entries.sort(Comparator.comparingDouble(object -> ((WeightedList.Entry)object).getShuffledOrder()));
		return this;
	}

	public Stream<? extends U> stream() {
		return this.entries.stream().map(WeightedList.Entry::getElement);
	}

	public Stream<WeightedList<U>.Entry<? extends U>> streamEntries() {
		return this.entries.stream();
	}

	public U pickRandom(Random random) {
		return (U)this.shuffle(random).stream().findFirst().orElseThrow(RuntimeException::new);
	}

	public String toString() {
		return "WeightedList[" + this.entries + "]";
	}

	public class Entry<T> {
		private final T item;
		private final int weight;
		private double shuffledOrder;

		private Entry(T item, int weight) {
			this.weight = weight;
			this.item = item;
		}

		private double getShuffledOrder() {
			return this.shuffledOrder;
		}

		private void setShuffledOrder(float random) {
			this.shuffledOrder = -Math.pow((double)random, (double)(1.0F / (float)this.weight));
		}

		public T getElement() {
			return this.item;
		}

		public int getWeight() {
			return this.weight;
		}

		public String toString() {
			return "" + this.weight + ":" + this.item;
		}
	}
}
