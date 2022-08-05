/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math.random;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicLong;

public final class RandomSeed {
    public static final long GOLDEN_RATIO_64 = -7046029254386353131L;
    public static final long SILVER_RATIO_64 = 7640891576956012809L;
    private static final AtomicLong SEED_UNIQUIFIER = new AtomicLong(8682522807148012L);

    @VisibleForTesting
    public static long mixStafford13(long seed) {
        seed = (seed ^ seed >>> 30) * -4658895280553007687L;
        seed = (seed ^ seed >>> 27) * -7723592293110705685L;
        return seed ^ seed >>> 31;
    }

    public static XoroshiroSeed createXoroshiroSeed(long seed) {
        long l = seed ^ 0x6A09E667F3BCC909L;
        long m = l + -7046029254386353131L;
        return new XoroshiroSeed(RandomSeed.mixStafford13(l), RandomSeed.mixStafford13(m));
    }

    /**
     * {@return the seed calculated using {@link SEED_UNIQUIFIER} and the system time}
     */
    public static long getSeed() {
        return SEED_UNIQUIFIER.updateAndGet(seedUniquifier -> seedUniquifier * 1181783497276652981L) ^ System.nanoTime();
    }

    public record XoroshiroSeed(long seedLo, long seedHi) {
    }
}

