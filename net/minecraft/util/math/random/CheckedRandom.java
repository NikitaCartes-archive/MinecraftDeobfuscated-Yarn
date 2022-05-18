/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math.random;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.BaseRandom;
import net.minecraft.util.math.random.GaussianGenerator;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;
import net.minecraft.util.thread.LockHelper;

/**
 * A checked random that fails fast when it detects concurrent usage.
 */
public class CheckedRandom
implements BaseRandom {
    private static final int INT_BITS = 48;
    private static final long SEED_MASK = 0xFFFFFFFFFFFFL;
    private static final long MULTIPLIER = 25214903917L;
    private static final long INCREMENT = 11L;
    private final AtomicLong seed = new AtomicLong();
    private final GaussianGenerator gaussianGenerator = new GaussianGenerator(this);

    public CheckedRandom(long seed) {
        this.setSeed(seed);
    }

    @Override
    public Random split() {
        return new CheckedRandom(this.nextLong());
    }

    @Override
    public RandomSplitter nextSplitter() {
        return new Splitter(this.nextLong());
    }

    @Override
    public void setSeed(long seed) {
        if (!this.seed.compareAndSet(this.seed.get(), (seed ^ 0x5DEECE66DL) & 0xFFFFFFFFFFFFL)) {
            throw LockHelper.crash("LegacyRandomSource", null);
        }
        this.gaussianGenerator.reset();
    }

    @Override
    public int next(int bits) {
        long m;
        long l = this.seed.get();
        if (!this.seed.compareAndSet(l, m = l * 25214903917L + 11L & 0xFFFFFFFFFFFFL)) {
            throw LockHelper.crash("LegacyRandomSource", null);
        }
        return (int)(m >> 48 - bits);
    }

    @Override
    public double nextGaussian() {
        return this.gaussianGenerator.next();
    }

    public static class Splitter
    implements RandomSplitter {
        private final long seed;

        public Splitter(long seed) {
            this.seed = seed;
        }

        @Override
        public Random split(int x, int y, int z) {
            long l = MathHelper.hashCode(x, y, z);
            long m = l ^ this.seed;
            return new CheckedRandom(m);
        }

        @Override
        public Random split(String seed) {
            int i = seed.hashCode();
            return new CheckedRandom((long)i ^ this.seed);
        }

        @Override
        @VisibleForTesting
        public void addDebugInfo(StringBuilder info) {
            info.append("LegacyPositionalRandomFactory{").append(this.seed).append("}");
        }
    }
}

