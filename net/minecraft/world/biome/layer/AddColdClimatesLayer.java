/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.BiomeIds;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.SouthEastSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddColdClimatesLayer implements SouthEastSamplingLayer
{
    INSTANCE;


    @Override
    public int sample(LayerRandomnessSource context, int se) {
        if (BiomeLayers.isShallowOcean(se)) {
            return se;
        }
        int i = context.nextInt(6);
        if (i == 0) {
            return BiomeIds.FOREST;
        }
        if (i == 1) {
            return BiomeIds.MOUNTAINS;
        }
        return BiomeIds.PLAINS;
    }
}

