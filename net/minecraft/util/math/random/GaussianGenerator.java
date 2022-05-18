/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math.random;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class GaussianGenerator {
    public final Random baseRandom;
    private double nextNextGaussian;
    private boolean hasNextGaussian;

    public GaussianGenerator(Random baseRandom) {
        this.baseRandom = baseRandom;
    }

    public void reset() {
        this.hasNextGaussian = false;
    }

    public double next() {
        double e;
        double d;
        double f;
        if (this.hasNextGaussian) {
            this.hasNextGaussian = false;
            return this.nextNextGaussian;
        }
        do {
            d = 2.0 * this.baseRandom.nextDouble() - 1.0;
            e = 2.0 * this.baseRandom.nextDouble() - 1.0;
        } while ((f = MathHelper.square(d) + MathHelper.square(e)) >= 1.0 || f == 0.0);
        double g = Math.sqrt(-2.0 * Math.log(f) / f);
        this.nextNextGaussian = e * g;
        this.hasNextGaussian = true;
        return d * g;
    }
}

