/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.BiomeIds;
import net.minecraft.world.biome.layer.type.SouthEastSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddSunflowerPlainsLayer implements SouthEastSamplingLayer
{
    INSTANCE;


    @Override
    public int sample(LayerRandomnessSource context, int se) {
        if (context.nextInt(57) == 0 && se == BiomeIds.PLAINS) {
            return BiomeIds.SUNFLOWER_PLAINS;
        }
        return se;
    }
}

