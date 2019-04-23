/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class WeightedList<U> {
    private final List<Entry<? extends U>> entries = Lists.newArrayList();
    private final Random random;

    public WeightedList() {
        this(new Random());
    }

    public WeightedList(Random random) {
        this.random = random;
    }

    public void add(U object, int i) {
        this.entries.add(new Entry(object, i));
    }

    public void shuffle() {
        this.entries.forEach(entry -> entry.setShuffledOrder(this.random.nextFloat()));
        this.entries.sort(Comparator.comparingDouble(Entry::getShuffledOrder));
    }

    public Stream<? extends U> stream() {
        return this.entries.stream().map(Entry::getElement);
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
            this.shuffledOrder = -Math.pow(f, 1.0f / (float)this.weight);
        }

        public T getElement() {
            return this.field_18400;
        }

        public String toString() {
            return "" + this.weight + ":" + this.field_18400;
        }
    }
}

