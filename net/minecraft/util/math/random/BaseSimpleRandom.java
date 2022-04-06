/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math.random;

import net.minecraft.util.math.random.AbstractRandom;

public interface BaseSimpleRandom
extends AbstractRandom {
    public static final float FLOAT_MULTIPLIER = 5.9604645E-8f;
    public static final double DOUBLE_MULTIPLIER = (double)1.110223E-16f;

    public int next(int var1);

    @Override
    default public int nextInt() {
        return this.next(32);
    }

    @Override
    default public int nextInt(int bound) {
        int j;
        int i;
        if (bound <= 0) {
            throw new IllegalArgumentException("Bound must be positive");
        }
        if ((bound & bound - 1) == 0) {
            return (int)((long)bound * (long)this.next(31) >> 31);
        }
        while ((i = this.next(31)) - (j = i % bound) + (bound - 1) < 0) {
        }
        return j;
    }

    @Override
    default public long nextLong() {
        int i = this.next(32);
        int j = this.next(32);
        long l = (long)i << 32;
        return l + (long)j;
    }

    @Override
    default public boolean nextBoolean() {
        return this.next(1) != 0;
    }

    @Override
    default public float nextFloat() {
        return (float)this.next(24) * 5.9604645E-8f;
    }

    @Override
    default public double nextDouble() {
        int i = this.next(26);
        int j = this.next(27);
        long l = ((long)i << 27) + (long)j;
        return (double)l * (double)1.110223E-16f;
    }
}

