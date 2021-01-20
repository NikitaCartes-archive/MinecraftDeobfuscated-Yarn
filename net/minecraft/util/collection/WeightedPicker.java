/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.collection;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WeightedPicker {
    private static final Logger LOGGER = LogManager.getLogger();

    public static int getWeightSum(List<? extends Entry> list) {
        long l = 0L;
        int j = list.size();
        for (int i = 0; i < j; ++i) {
            Entry entry = list.get(i);
            l += (long)entry.weight;
        }
        if (l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Sum of weights must be <= 2147483647");
        }
        return (int)l;
    }

    public static <T extends Entry> Optional<T> getRandom(Random random, List<T> list, int weightSum) {
        if (weightSum < 0) {
            throw Util.throwOrPause(new IllegalArgumentException("Negative total weight in getRandomItem"));
        }
        if (weightSum == 0) {
            return Optional.empty();
        }
        int i = random.nextInt(weightSum);
        return WeightedPicker.getAt(list, i);
    }

    public static <T extends Entry> Optional<T> getAt(List<T> list, int weightMark) {
        int j = list.size();
        for (int i = 0; i < j; ++i) {
            Entry entry = (Entry)list.get(i);
            if ((weightMark -= entry.weight) >= 0) continue;
            return Optional.of(entry);
        }
        return Optional.empty();
    }

    public static <T extends Entry> Optional<T> getRandom(Random random, List<T> list) {
        return WeightedPicker.getRandom(random, list, WeightedPicker.getWeightSum(list));
    }

    public static class Entry {
        protected final int weight;

        public Entry(int weight) {
            if (weight < 0) {
                throw Util.throwOrPause(new IllegalArgumentException("Weight should be >= 0"));
            }
            if (weight == 0 && SharedConstants.isDevelopment) {
                LOGGER.warn("Found 0 weight, make sure this is intentional!");
            }
            this.weight = weight;
        }
    }
}

