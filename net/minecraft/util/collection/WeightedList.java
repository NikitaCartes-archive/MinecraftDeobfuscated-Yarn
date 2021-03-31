/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
    protected final List<Entry<U>> entries;
    private final Random random = new Random();

    public WeightedList() {
        this.entries = Lists.newArrayList();
    }

    private WeightedList(List<Entry<U>> list) {
        this.entries = Lists.newArrayList(list);
    }

    public static <U> Codec<WeightedList<U>> createCodec(Codec<U> codec) {
        return Entry.createCodec(codec).listOf().xmap(WeightedList::new, weightedList -> weightedList.entries);
    }

    public WeightedList<U> add(U data, int weight) {
        this.entries.add(new Entry(data, weight));
        return this;
    }

    public WeightedList<U> shuffle() {
        this.entries.forEach(entry -> ((Entry)entry).setShuffledOrder(this.random.nextFloat()));
        this.entries.sort(Comparator.comparingDouble(entry -> ((Entry)entry).getShuffledOrder()));
        return this;
    }

    public Stream<U> stream() {
        return this.entries.stream().map(Entry::getElement);
    }

    public String toString() {
        return "ShufflingList[" + this.entries + "]";
    }

    public static class Entry<T> {
        private final T data;
        private final int weight;
        private double shuffledOrder;

        private Entry(T data, int weight) {
            this.weight = weight;
            this.data = data;
        }

        private double getShuffledOrder() {
            return this.shuffledOrder;
        }

        private void setShuffledOrder(float random) {
            this.shuffledOrder = -Math.pow(random, 1.0f / (float)this.weight);
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

        public static <E> Codec<Entry<E>> createCodec(final Codec<E> codec) {
            return new Codec<Entry<E>>(){

                @Override
                public <T> DataResult<Pair<Entry<E>, T>> decode(DynamicOps<T> ops, T data2) {
                    Dynamic dynamic = new Dynamic(ops, data2);
                    return dynamic.get("data").flatMap(codec::parse).map((? super R data) -> new Entry(data, dynamic.get("weight").asInt(1))).map((? super R entry) -> Pair.of(entry, ops.empty()));
                }

                @Override
                public <T> DataResult<T> encode(Entry<E> entry, DynamicOps<T> dynamicOps, T object) {
                    return dynamicOps.mapBuilder().add("weight", dynamicOps.createInt(entry.weight)).add("data", codec.encodeStart(dynamicOps, entry.data)).build(object);
                }

                @Override
                public /* synthetic */ DataResult encode(Object entries, DynamicOps ops, Object data) {
                    return this.encode((Entry)entries, ops, data);
                }
            };
        }
    }
}

