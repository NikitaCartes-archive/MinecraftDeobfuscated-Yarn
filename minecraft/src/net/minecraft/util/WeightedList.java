package net.minecraft.util;

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

	public <T> T method_23330(DynamicOps<T> dynamicOps, Function<U, Dynamic<T>> function) {
		return dynamicOps.createList(
			this.method_23338()
				.map(
					entry -> dynamicOps.createMap(
							ImmutableMap.<T, T>builder()
								.put(dynamicOps.createString("data"), (T)((Dynamic)function.apply(entry.getElement())).getValue())
								.put(dynamicOps.createString("weight"), dynamicOps.createInt(entry.method_23341()))
								.build()
						)
				)
		);
	}

	public WeightedList<U> add(U object, int i) {
		this.entries.add(new WeightedList.Entry(object, i));
		return this;
	}

	public WeightedList<U> shuffle() {
		return this.method_23333(this.random);
	}

	public WeightedList<U> method_23333(Random random) {
		this.entries.forEach(entry -> entry.setShuffledOrder(random.nextFloat()));
		this.entries.sort(Comparator.comparingDouble(object -> ((WeightedList.Entry)object).getShuffledOrder()));
		return this;
	}

	public Stream<? extends U> stream() {
		return this.entries.stream().map(WeightedList.Entry::getElement);
	}

	public Stream<WeightedList<U>.Entry<? extends U>> method_23338() {
		return this.entries.stream();
	}

	public U method_23337(Random random) {
		return (U)this.method_23333(random).stream().findFirst().orElseThrow(RuntimeException::new);
	}

	public String toString() {
		return "WeightedList[" + this.entries + "]";
	}

	public class Entry<T> {
		private final T field_18400;
		private final int weight;
		private double shuffledOrder;

		private Entry(T object, int i) {
			this.weight = i;
			this.field_18400 = object;
		}

		private double getShuffledOrder() {
			return this.shuffledOrder;
		}

		private void setShuffledOrder(float f) {
			this.shuffledOrder = -Math.pow((double)f, (double)(1.0F / (float)this.weight));
		}

		public T getElement() {
			return this.field_18400;
		}

		public int method_23341() {
			return this.weight;
		}

		public String toString() {
			return "" + this.weight + ":" + this.field_18400;
		}
	}
}
