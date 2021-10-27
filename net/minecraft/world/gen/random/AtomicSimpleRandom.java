/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.random;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.thread.LockHelper;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.BaseSimpleRandom;
import net.minecraft.world.gen.random.GaussianGenerator;

public class AtomicSimpleRandom
implements BaseSimpleRandom {
    private static final int INT_BITS = 48;
    private static final long SEED_MASK = 0xFFFFFFFFFFFFL;
    private static final long MULTIPLIER = 25214903917L;
    private static final long INCREMENT = 11L;
    private final AtomicLong seed = new AtomicLong();
    private final GaussianGenerator gaussianGenerator = new GaussianGenerator(this);

    public AtomicSimpleRandom(long seed) {
        this.setSeed(seed);
    }

    @Override
    public AbstractRandom derive() {
        return new AtomicSimpleRandom(this.nextLong());
    }

    @Override
    public net.minecraft.world.gen.random.RandomDeriver createRandomDeriver() {
        return new RandomDeriver(this.nextLong());
    }

    @Override
    public void setSeed(long l) {
        if (!this.seed.compareAndSet(this.seed.get(), (l ^ 0x5DEECE66DL) & 0xFFFFFFFFFFFFL)) {
            throw LockHelper.crash("LegacyRandomSource", null);
        }
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

    public static class RandomDeriver
    implements net.minecraft.world.gen.random.RandomDeriver {
        private final long seed;

        public RandomDeriver(long seed) {
            this.seed = seed;
        }

        @Override
        public AbstractRandom createRandom(int x, int y, int z) {
            long l = MathHelper.hashCode(x, y, z);
            long m = l ^ this.seed;
            return new AtomicSimpleRandom(m);
        }

        @Override
        public AbstractRandom createRandom(String string) {
            int i = string.hashCode();
            return new AtomicSimpleRandom((long)i ^ this.seed);
        }

        @Override
        @VisibleForTesting
        public void addDebugInfo(StringBuilder info) {
            info.append("LegacyPositionalRandomFactory{").append(this.seed).append("}");
        }
    }
}

