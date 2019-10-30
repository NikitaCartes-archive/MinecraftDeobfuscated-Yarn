/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
    protected final List<Entry<? extends U>> entries = Lists.newArrayList();
    private final Random random;

    public WeightedList(Random random) {
        this.random = random;
    }

    public WeightedList() {
        this(new Random());
    }

    public <T> WeightedList(Dynamic<T> dynamic2, Function<Dynamic<T>, U> function) {
        this();
        dynamic2.asStream().forEach(dynamic -> dynamic.get("data").map(dynamic2 -> {
            Object object = function.apply((Dynamic)dynamic2);
            int i = dynamic.get("weight").asInt(1);
            return this.add(object, i);
        }));
    }

    public <T> T serialize(DynamicOps<T> dynamicOps, Function<U, Dynamic<T>> function) {
        return (T)dynamicOps.createList(this.streamEntries().map(entry -> dynamicOps.createMap(ImmutableMap.builder().put(dynamicOps.createString("data"), ((Dynamic)function.apply(entry.getElement())).getValue()).put(dynamicOps.createString("weight"), dynamicOps.createInt(entry.getWeight())).build())));
    }

    public WeightedList<U> add(U object, int i) {
        this.entries.add(new Entry(object, i));
        return this;
    }

    public WeightedList<U> shuffle() {
        return this.shuffle(this.random);
    }

    public WeightedList<U> shuffle(Random random) {
        this.entries.forEach(entry -> ((Entry)entry).setShuffledOrder(random.nextFloat()));
        this.entries.sort(Comparator.comparingDouble(object -> ((Entry)object).getShuffledOrder()));
        return this;
    }

    public Stream<? extends U> stream() {
        return this.entries.stream().map(Entry::getElement);
    }

    public Stream<Entry<? extends U>> streamEntries() {
        return this.entries.stream();
    }

    public U pickRandom(Random random) {
        return this.shuffle(random).stream().findFirst().orElseThrow(RuntimeException::new);
    }

    public String toString() {
        return "WeightedList[" + this.entries + "]";
    }

    public class Entry<T> {
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

        private void setShuffledOrder(float f) {
            this.shuffledOrder = -Math.pow(f, 1.0f / (float)this.weight);
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

