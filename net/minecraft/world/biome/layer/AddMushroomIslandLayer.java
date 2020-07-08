/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.DiagonalCrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddMushroomIslandLayer implements DiagonalCrossSamplingLayer
{
    INSTANCE;

    private static final int MUSHROOM_FIELDS_ID;

    @Override
    public int sample(LayerRandomnessSource context, int sw, int se, int ne, int nw, int center) {
        if (BiomeLayers.isShallowOcean(center) && BiomeLayers.isShallowOcean(nw) && BiomeLayers.isShallowOcean(sw) && BiomeLayers.isShallowOcean(ne) && BiomeLayers.isShallowOcean(se) && context.nextInt(100) == 0) {
            return MUSHROOM_FIELDS_ID;
        }
        return center;
    }

    static {
        MUSHROOM_FIELDS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.MUSHROOM_FIELDS);
    }
}

