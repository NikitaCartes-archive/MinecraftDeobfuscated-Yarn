/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum SmoothenShorelineLayer implements CrossSamplingLayer
{
    INSTANCE;


    @Override
    public int sample(LayerRandomnessSource layerRandomnessSource, int i, int j, int k, int l, int m) {
        boolean bl2;
        boolean bl = j == l;
        boolean bl3 = bl2 = i == k;
        if (bl == bl2) {
            if (bl) {
                return layerRandomnessSource.nextInt(2) == 0 ? l : i;
            }
            return m;
        }
        return bl ? l : i;
    }
}

