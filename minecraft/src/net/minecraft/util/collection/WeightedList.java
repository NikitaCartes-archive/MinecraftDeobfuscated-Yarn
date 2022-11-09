package net.minecraft.util.collection;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.util.math.random.Random;

public class WeightedList<U> implements Iterable<U> {
	protected final List<WeightedList.Entry<U>> entries;
	private final Random random = Random.create();

	public WeightedList() {
		this.entries = Lists.<WeightedList.Entry<U>>newArrayList();
	}

	private WeightedList(List<WeightedList.Entry<U>> list) {
		this.entries = Lists.<WeightedList.Entry<U>>newArrayList(list);
	}

	public static <U> Codec<WeightedList<U>> createCodec(Codec<U> codec) {
		return WeightedList.Entry.createCodec(codec).listOf().xmap(WeightedList::new, weightedList -> weightedList.entries);
	}

	public WeightedList<U> add(U data, int weight) {
		this.entries.add(new WeightedList.Entry<>(data, weight));
		return this;
	}

	public WeightedList<U> shuffle() {
		this.entries.forEach(entry -> entry.setShuffledOrder(this.random.nextFloat()));
		this.entries.sort(Comparator.comparingDouble(WeightedList.Entry::getShuffledOrder));
		return this;
	}

	public Stream<U> stream() {
		return this.entries.stream().map(WeightedList.Entry::getElement);
	}

	public Iterator<U> iterator() {
		return Iterators.transform(this.entries.iterator(), WeightedList.Entry::getElement);
	}

	public String toString() {
		return "ShufflingList[" + this.entries + "]";
	}

	public static class Entry<T> {
		final T data;
		final int weight;
		private double shuffledOrder;

		Entry(T data, int weight) {
			this.weight = weight;
			this.data = data;
		}

		private double getShuffledOrder() {
			return this.shuffledOrder;
		}

		void setShuffledOrder(float random) {
			this.shuffledOrder = -Math.pow((double)random, (double)(1.0F / (float)this.weight));
		}

		public T getElement() {
			return this.data;
		}

		public int getWeight() {
			return this.weight;
		}

		public String toString() {
			return this.weight + ":" + this.data;
		}

		public static <E> Codec<WeightedList.Entry<E>> createCodec(Codec<E> codec) {
			return new Codec<WeightedList.Entry<E>>() {
				@Override
				public <T> DataResult<Pair<WeightedList.Entry<E>, T>> decode(DynamicOps<T> ops, T data) {
					Dynamic<T> dynamic = new Dynamic<>(ops, data);
					return dynamic.get("data")
						.flatMap(codec::parse)
						.map(datax -> new WeightedList.Entry<>(datax, dynamic.get("weight").asInt(1)))
						.map(entry -> Pair.of(entry, ops.empty()));
				}

				public <T> DataResult<T> encode(WeightedList.Entry<E> entry, DynamicOps<T> dynamicOps, T object) {
					return dynamicOps.mapBuilder().add("weight", dynamicOps.createInt(entry.weight)).add("data", codec.encodeStart(dynamicOps, entry.data)).build(object);
				}
			};
		}
	}
}
