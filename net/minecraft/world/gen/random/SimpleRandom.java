/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.random;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.BaseSimpleRandom;

public class SimpleRandom
implements BaseSimpleRandom {
    private static final int INT_BITS = 48;
    private static final long SEED_MASK = 0xFFFFFFFFFFFFL;
    private static final long MULTIPLIER = 25214903917L;
    private static final long INCREMENT = 11L;
    private long seed;
    private double nextNextGaussian;
    private boolean hasNextGaussian;

    public SimpleRandom(long seed) {
        this.setSeed(seed);
    }

    @Override
    public AbstractRandom derive() {
        return new SimpleRandom(this.nextLong());
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
        double e;
        double d;
        double f;
        if (this.hasNextGaussian) {
            this.hasNextGaussian = false;
            return this.nextNextGaussian;
        }
        do {
            d = 2.0 * this.nextDouble() - 1.0;
            e = 2.0 * this.nextDouble() - 1.0;
        } while ((f = MathHelper.square(d) + MathHelper.square(e)) >= 1.0 || f == 0.0);
        double g = Math.sqrt(-2.0 * Math.log(f) / f);
        this.nextNextGaussian = e * g;
        this.hasNextGaussian = true;
        return d * g;
    }
}

