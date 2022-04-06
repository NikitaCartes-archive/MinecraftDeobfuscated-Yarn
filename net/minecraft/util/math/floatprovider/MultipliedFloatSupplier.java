/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math.floatprovider;

import java.util.Arrays;
import net.minecraft.util.math.floatprovider.FloatSupplier;
import net.minecraft.util.math.random.AbstractRandom;

public class MultipliedFloatSupplier
implements FloatSupplier {
    private final FloatSupplier[] multipliers;

    public MultipliedFloatSupplier(FloatSupplier ... multipliers) {
        this.multipliers = multipliers;
    }

    @Override
    public float get(AbstractRandom random) {
        float f = 1.0f;
        for (int i = 0; i < this.multipliers.length; ++i) {
            f *= this.multipliers[i].get(random);
        }
        return f;
    }

    public String toString() {
        return "MultipliedFloats" + Arrays.toString(this.multipliers);
    }
}

