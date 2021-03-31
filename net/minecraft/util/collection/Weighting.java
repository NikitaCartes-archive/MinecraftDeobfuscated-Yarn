/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.collection;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Weighted;

public class Weighting {
    private Weighting() {
    }

    public static int getWeightSum(List<? extends Weighted> pool) {
        long l = 0L;
        for (Weighted weighted : pool) {
            l += (long)weighted.getWeight().getValue();
        }
        if (l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Sum of weights must be <= 2147483647");
        }
        return (int)l;
    }

    public static <T extends Weighted> Optional<T> getRandom(Random random, List<T> pool, int totalWeight) {
        if (totalWeight < 0) {
            throw Util.throwOrPause(new IllegalArgumentException("Negative total weight in getRandomItem"));
        }
        if (totalWeight == 0) {
            return Optional.empty();
        }
        int i = random.nextInt(totalWeight);
        return Weighting.getAt(pool, i);
    }

    public static <T extends Weighted> Optional<T> getAt(List<T> pool, int totalWeight) {
        for (Weighted weighted : pool) {
            if ((totalWeight -= weighted.getWeight().getValue()) >= 0) continue;
            return Optional.of(weighted);
        }
        return Optional.empty();
    }

    public static <T extends Weighted> Optional<T> getRandom(Random random, List<T> pool) {
        return Weighting.getRandom(random, pool, Weighting.getWeightSum(pool));
    }
}

