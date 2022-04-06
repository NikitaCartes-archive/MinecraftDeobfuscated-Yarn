/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math.random;

import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.math.random.BaseSimpleRandom;
import net.minecraft.util.math.random.RandomDeriver;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.GaussianGenerator;

@Deprecated
public class BlockingSimpleRandom
implements BaseSimpleRandom {
    private static final int INT_BITS = 48;
    private static final long SEED_MASK = 0xFFFFFFFFFFFFL;
    private static final long MULTIPLIER = 25214903917L;
    private static final long INCREMENT = 11L;
    private final AtomicLong seed = new AtomicLong();
    private final GaussianGenerator gaussianGenerator = new GaussianGenerator(this);

    public BlockingSimpleRandom(long seed) {
        this.setSeed(seed);
    }

    @Override
    public AbstractRandom derive() {
        return new BlockingSimpleRandom(this.nextLong());
    }

    @Override
    public RandomDeriver createRandomDeriver() {
        return new AtomicSimpleRandom.RandomDeriver(this.nextLong());
    }

    @Override
    public void setSeed(long seed) {
        this.seed.set((seed ^ 0x5DEECE66DL) & 0xFFFFFFFFFFFFL);
    }

    @Override
    public int next(int bits) {
        long m;
        long l;
        while (!this.seed.compareAndSet(l = this.seed.get(), m = l * 25214903917L + 11L & 0xFFFFFFFFFFFFL)) {
        }
        return (int)(m >>> 48 - bits);
    }

    @Override
    public double nextGaussian() {
        return this.gaussianGenerator.next();
    }
}

