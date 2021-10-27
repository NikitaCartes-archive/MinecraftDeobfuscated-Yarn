/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.random;

import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.BaseSimpleRandom;
import net.minecraft.world.gen.random.GaussianGenerator;
import net.minecraft.world.gen.random.RandomDeriver;

public class SimpleRandom
implements BaseSimpleRandom {
    private static final int INT_BITS = 48;
    private static final long SEED_MASK = 0xFFFFFFFFFFFFL;
    private static final long MULTIPLIER = 25214903917L;
    private static final long INCREMENT = 11L;
    private long seed;
    private final GaussianGenerator gaussianGenerator = new GaussianGenerator(this);

    public SimpleRandom(long seed) {
        this.setSeed(seed);
    }

    @Override
    public AbstractRandom derive() {
        return new SimpleRandom(this.nextLong());
    }

    @Override
    public RandomDeriver createRandomDeriver() {
        return new AtomicSimpleRandom.RandomDeriver(this.nextLong());
    }

    @Override
    public void setSeed(long l) {
        this.seed = (l ^ 0x5DEECE66DL) & 0xFFFFFFFFFFFFL;
    }

    @Override
    public int next(int bits) {
        long l;
        this.seed = l = this.seed * 25214903917L + 11L & 0xFFFFFFFFFFFFL;
        return (int)(l >> 48 - bits);
    }

    @Override
    public double nextGaussian() {
        return this.gaussianGenerator.next();
    }
}

