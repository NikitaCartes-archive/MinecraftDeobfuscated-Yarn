/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.LayerRandomnessSource;
import net.minecraft.world.biome.layer.SouthEastSamplingLayer;

public enum AddColdClimatesLayer implements SouthEastSamplingLayer
{
    INSTANCE;


    @Override
    public int sample(LayerRandomnessSource layerRandomnessSource, int i) {
        if (BiomeLayers.isShallowOcean(i)) {
            return i;
        }
        int j = layerRandomnessSource.nextInt(6);
        if (j == 0) {
            return 4;
        }
        if (j == 1) {
            return 3;
        }
        return 1;
    }
}

