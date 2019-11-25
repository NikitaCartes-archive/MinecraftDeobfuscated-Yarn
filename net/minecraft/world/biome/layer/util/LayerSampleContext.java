/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer.util;

import net.minecraft.world.biome.layer.util.LayerOperator;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;

public interface LayerSampleContext<R extends LayerSampler>
extends LayerRandomnessSource {
    public void initSeed(long var1, long var3);

    public R createSampler(LayerOperator var1);

    default public R createSampler(LayerOperator layerOperator, R layerSampler) {
        return this.createSampler(layerOperator);
    }

    default public R createSampler(LayerOperator layerOperator, R layerSampler, R layerSampler2) {
        return this.createSampler(layerOperator);
    }

    default public int choose(int i, int j) {
        return this.nextInt(2) == 0 ? i : j;
    }

    default public int choose(int i, int j, int k, int l) {
        int m = this.nextInt(4);
        if (m == 0) {
            return i;
        }
        if (m == 1) {
            return j;
        }
        if (m == 2) {
            return k;
        }
        return l;
    }
}

