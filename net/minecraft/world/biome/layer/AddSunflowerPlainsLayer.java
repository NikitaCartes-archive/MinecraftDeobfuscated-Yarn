/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.type.SouthEastSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddSunflowerPlainsLayer implements SouthEastSamplingLayer
{
    INSTANCE;

    private static final int PLAINS_ID;
    private static final int SUNFLOWER_PLAINS;

    @Override
    public int sample(LayerRandomnessSource layerRandomnessSource, int i) {
        if (layerRandomnessSource.nextInt(57) == 0 && i == PLAINS_ID) {
            return SUNFLOWER_PLAINS;
        }
        return i;
    }

    static {
        PLAINS_ID = Registry.BIOME.getRawId(Biomes.PLAINS);
        SUNFLOWER_PLAINS = Registry.BIOME.getRawId(Biomes.SUNFLOWER_PLAINS);
    }
}

