/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.CrossSamplingLayer;
import net.minecraft.world.biome.layer.LayerRandomnessSource;

public enum AddIslandLayer implements CrossSamplingLayer
{
    INSTANCE;


    @Override
    public int sample(LayerRandomnessSource layerRandomnessSource, int i, int j, int k, int l, int m) {
        if (BiomeLayers.isShallowOcean(m) && BiomeLayers.isShallowOcean(i) && BiomeLayers.isShallowOcean(j) && BiomeLayers.isShallowOcean(l) && BiomeLayers.isShallowOcean(k) && layerRandomnessSource.nextInt(2) == 0) {
            return 1;
        }
        return m;
    }
}

