package net.minecraft.util.collection;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class WeightedList<U> {
	protected final List<WeightedList.Entry<U>> entries;
	private final Random random = new Random();

	public WeightedList() {
		this(Lists.<WeightedList.Entry<U>>newArrayList());
	}

	private WeightedList(List<WeightedList.Entry<U>> list) {
		this.entries = list;
	}

	public static <U> Codec<WeightedList<U>> method_28338(Codec<U> codec) {
		return WeightedList.Entry.method_28341(codec).listOf().xmap(WeightedList::new, weightedList -> weightedList.entries);
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

	public boolean method_28339() {
		return this.entries.isEmpty();
	}

	public Stream<U> stream() {
		return this.entries.stream().map(WeightedList.Entry::getElement);
	}

	public U pickRandom(Random random) {
		return (U)this.shuffle(random).stream().findFirst().orElseThrow(RuntimeException::new);
	}

	public String toString() {
		return "WeightedList[" + this.entries + "]";
	}

	public static class Entry<T> {
		private final T item;
		private final int weight;
		private double shuffledOrder;

		private Entry(T object, int i) {
			this.weight = i;
			this.item = object;
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

		public String toString() {
			return "" + this.weight + ":" + this.item;
		}

		public static <E> Codec<WeightedList.Entry<E>> method_28341(Codec<E> codec) {
			return new Codec<WeightedList.Entry<E>>() {
				@Override
				public <T> DataResult<Pair<WeightedList.Entry<E>, T>> decode(DynamicOps<T> dynamicOps, T object) {
					Dynamic<T> dynamic = new Dynamic<>(dynamicOps, object);
					return dynamic.get("data")
						.flatMap(codec::parse)
						.map(objectx -> new WeightedList.Entry(objectx, dynamic.get("weight").asInt(1)))
						.map(entry -> Pair.of(entry, dynamicOps.empty()));
				}

				public <T> DataResult<T> encode(WeightedList.Entry<E> entry, DynamicOps<T> dynamicOps, T object) {
					return dynamicOps.mapBuilder().add("weight", dynamicOps.createInt(entry.weight)).add("data", codec.encodeStart(dynamicOps, entry.item)).build(object);
				}
			};
		}
	}
}
