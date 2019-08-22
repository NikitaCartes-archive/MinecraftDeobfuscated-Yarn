/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.DiagonalCrossSamplingLayer;
import net.minecraft.world.biome.layer.LayerRandomnessSource;

public enum AddMushroomIslandLayer implements DiagonalCrossSamplingLayer
{
    INSTANCE;

    private static final int MUSHROOM_FIELDS_ID;

    @Override
    public int sample(LayerRandomnessSource layerRandomnessSource, int i, int j, int k, int l, int m) {
        if (BiomeLayers.isShallowOcean(m) && BiomeLayers.isShallowOcean(l) && BiomeLayers.isShallowOcean(i) && BiomeLayers.isShallowOcean(k) && BiomeLayers.isShallowOcean(j) && layerRandomnessSource.nextInt(100) == 0) {
            return MUSHROOM_FIELDS_ID;
        }
        return m;
    }

    static {
        MUSHROOM_FIELDS_ID = Registry.BIOME.getRawId(Biomes.MUSHROOM_FIELDS);
    }
}

