/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddIslandLayer implements CrossSamplingLayer
{
    INSTANCE;


    @Override
    public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
        if (BiomeLayers.isShallowOcean(center) && BiomeLayers.isShallowOcean(n) && BiomeLayers.isShallowOcean(e) && BiomeLayers.isShallowOcean(w) && BiomeLayers.isShallowOcean(s) && context.nextInt(2) == 0) {
            return 1;
        }
        return center;
    }
}

