/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.InitLayer;
import net.minecraft.world.biome.layer.LayerRandomnessSource;

public enum ContinentLayer implements InitLayer
{
    INSTANCE;


    @Override
    public int sample(LayerRandomnessSource layerRandomnessSource, int i, int j) {
        if (i == 0 && j == 0) {
            return 1;
        }
        return layerRandomnessSource.nextInt(10) == 0 ? 1 : BiomeLayers.OCEAN_ID;
    }
}

