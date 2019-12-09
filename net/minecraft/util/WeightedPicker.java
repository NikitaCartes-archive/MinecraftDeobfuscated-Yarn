/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import java.util.List;
import java.util.Random;
import net.minecraft.util.Util;

public class WeightedPicker {
    public static int getWeightSum(List<? extends Entry> list) {
        int i = 0;
        int k = list.size();
        for (int j = 0; j < k; ++j) {
            Entry entry = list.get(j);
            i += entry.weight;
        }
        return i;
    }

    public static <T extends Entry> T getRandom(Random random, List<T> list, int weightSum) {
        if (weightSum <= 0) {
            throw Util.throwOrPause(new IllegalArgumentException());
        }
        int i = random.nextInt(weightSum);
        return WeightedPicker.getAt(list, i);
    }

    public static <T extends Entry> T getAt(List<T> list, int pos) {
        int j = list.size();
        for (int i = 0; i < j; ++i) {
            Entry entry = (Entry)list.get(i);
            if ((pos -= entry.weight) >= 0) continue;
            return (T)entry;
        }
        return null;
    }

    public static <T extends Entry> T getRandom(Random random, List<T> list) {
        return WeightedPicker.getRandom(random, list, WeightedPicker.getWeightSum(list));
    }

    public static class Entry {
        protected final int weight;

        public Entry(int i) {
            this.weight = i;
        }
    }
}

