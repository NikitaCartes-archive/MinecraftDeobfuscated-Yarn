/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import java.util.List;
import java.util.Random;
import net.minecraft.util.SystemUtil;

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

    public static <T extends Entry> T getRandom(Random random, List<T> list, int i) {
        if (i <= 0) {
            throw SystemUtil.throwOrPause(new IllegalArgumentException());
        }
        int j = random.nextInt(i);
        return WeightedPicker.getAt(list, j);
    }

    public static <T extends Entry> T getAt(List<T> list, int i) {
        int k = list.size();
        for (int j = 0; j < k; ++j) {
            Entry entry = (Entry)list.get(j);
            if ((i -= entry.weight) >= 0) continue;
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

