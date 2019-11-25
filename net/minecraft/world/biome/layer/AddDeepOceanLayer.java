/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.BiomeLayers;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddDeepOceanLayer implements CrossSamplingLayer
{
    INSTANCE;


    @Override
    public int sample(LayerRandomnessSource layerRandomnessSource, int i, int j, int k, int l, int m) {
        if (BiomeLayers.isShallowOcean(m)) {
            int n = 0;
            if (BiomeLayers.isShallowOcean(i)) {
                ++n;
            }
            if (BiomeLayers.isShallowOcean(j)) {
                ++n;
            }
            if (BiomeLayers.isShallowOcean(l)) {
                ++n;
            }
            if (BiomeLayers.isShallowOcean(k)) {
                ++n;
            }
            if (n > 3) {
                if (m == BiomeLayers.WARM_OCEAN_ID) {
                    return BiomeLayers.DEEP_WARM_OCEAN_ID;
                }
                if (m == BiomeLayers.LUKEWARM_OCEAN_ID) {
                    return BiomeLayers.DEEP_LUKEWARM_OCEAN_ID;
                }
                if (m == BiomeLayers.OCEAN_ID) {
                    return BiomeLayers.DEEP_OCEAN_ID;
                }
                if (m == BiomeLayers.COLD_OCEAN_ID) {
                    return BiomeLayers.DEEP_COLD_OCEAN_ID;
                }
                if (m == BiomeLayers.FROZEN_OCEAN_ID) {
                    return BiomeLayers.DEEP_FROZEN_OCEAN_ID;
                }
                return BiomeLayers.DEEP_OCEAN_ID;
            }
        }
        return m;
    }
}

