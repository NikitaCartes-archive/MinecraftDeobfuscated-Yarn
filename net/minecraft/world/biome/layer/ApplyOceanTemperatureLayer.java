/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.MergingLayer;
import net.minecraft.world.biome.layer.util.BiomeLayers;
import net.minecraft.world.biome.layer.util.IdentityCoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;

public enum ApplyOceanTemperatureLayer implements MergingLayer,
IdentityCoordinateTransformer
{
    INSTANCE;


    @Override
    public int sample(LayerRandomnessSource layerRandomnessSource, LayerSampler layerSampler, LayerSampler layerSampler2, int i, int j) {
        int k = layerSampler.sample(this.transformX(i), this.transformZ(j));
        int l = layerSampler2.sample(this.transformX(i), this.transformZ(j));
        if (!BiomeLayers.isOcean(k)) {
            return k;
        }
        int m = 8;
        int n = 4;
        for (int o = -8; o <= 8; o += 4) {
            for (int p = -8; p <= 8; p += 4) {
                int q = layerSampler.sample(this.transformX(i + o), this.transformZ(j + p));
                if (BiomeLayers.isOcean(q)) continue;
                if (l == BiomeLayers.WARM_OCEAN_ID) {
                    return BiomeLayers.LUKEWARM_OCEAN_ID;
                }
                if (l != BiomeLayers.FROZEN_OCEAN_ID) continue;
                return BiomeLayers.COLD_OCEAN_ID;
            }
        }
        if (k == BiomeLayers.DEEP_OCEAN_ID) {
            if (l == BiomeLayers.LUKEWARM_OCEAN_ID) {
                return BiomeLayers.DEEP_LUKEWARM_OCEAN_ID;
            }
            if (l == BiomeLayers.OCEAN_ID) {
                return BiomeLayers.DEEP_OCEAN_ID;
            }
            if (l == BiomeLayers.COLD_OCEAN_ID) {
                return BiomeLayers.DEEP_COLD_OCEAN_ID;
            }
            if (l == BiomeLayers.FROZEN_OCEAN_ID) {
                return BiomeLayers.DEEP_FROZEN_OCEAN_ID;
            }
        }
        return l;
    }
}

