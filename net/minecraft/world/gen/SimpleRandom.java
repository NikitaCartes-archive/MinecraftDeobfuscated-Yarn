/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.thread.LockHelper;
import net.minecraft.world.gen.WorldGenRandom;

public class SimpleRandom
implements WorldGenRandom {
    private final AtomicLong seed = new AtomicLong();
    private boolean field_28767 = false;

    public SimpleRandom(long seed) {
        this.setSeed(seed);
    }

    public void setSeed(long seed) {
        if (!this.seed.compareAndSet(this.seed.get(), (seed ^ 0x5DEECE66DL) & 0xFFFFFFFFFFFFL)) {
            throw LockHelper.crash("SimpleRandomSource", null);
        }
    }

    private int next(int bits) {
        long m;
        long l = this.seed.get();
        if (!this.seed.compareAndSet(l, m = l * 25214903917L + 11L & 0xFFFFFFFFFFFFL)) {
            throw LockHelper.crash("SimpleRandomSource", null);
        }
        return (int)(m >> 48 - bits);
    }

    @Override
    public int nextInt() {
        return this.next(32);
    }

    @Override
    public int nextInt(int i) {
        int k;
        int j;
        if (i <= 0) {
            throw new IllegalArgumentException("Bound must be positive");
        }
        if ((i & i - 1) == 0) {
            return (int)((long)i * (long)this.next(31) >> 31);
        }
        while ((j = this.next(31)) - (k = j % i) + (i - 1) < 0) {
        }
        return k;
    }

    @Override
    public long nextLong() {
        return ((long)this.nextInt() << 32) + (long)this.nextInt();
    }

    @Override
    public double nextDouble() {
        return (double)(((long)this.next(26) << 27) + (long)this.next(27)) * (double)1.110223E-16f;
    }
}

