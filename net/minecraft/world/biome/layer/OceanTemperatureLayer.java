/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.InitLayer;
import net.minecraft.world.biome.layer.LayerRandomnessSource;

public enum OceanTemperatureLayer implements InitLayer
{
    INSTANCE;


    @Override
    public int sample(LayerRandomnessSource layerRandomnessSource, int i, int j) {
        PerlinNoiseSampler perlinNoiseSampler = layerRandomnessSource.getNoiseSampler();
        double d = perlinNoiseSampler.sample((double)i / 8.0, (double)j / 8.0, 0.0, 0.0, 0.0);
        if (d > 0.4) {
            return BiomeLayers.WARM_OCEAN_ID;
        }
        if (d > 0.2) {
            return BiomeLayers.LUKEWARM_OCEAN_ID;
        }
        if (d < -0.4) {
            return BiomeLayers.FROZEN_OCEAN_ID;
        }
        if (d < -0.2) {
            return BiomeLayers.COLD_OCEAN_ID;
        }
        return BiomeLayers.OCEAN_ID;
    }
}

