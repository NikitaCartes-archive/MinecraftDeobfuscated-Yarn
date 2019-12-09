/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.type.SouthEastSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddBambooJungleLayer implements SouthEastSamplingLayer
{
    INSTANCE;

    private static final int JUNGLE_ID;
    private static final int BAMBOO_JUNGLE_ID;

    @Override
    public int sample(LayerRandomnessSource context, int se) {
        if (context.nextInt(10) == 0 && se == JUNGLE_ID) {
            return BAMBOO_JUNGLE_ID;
        }
        return se;
    }

    static {
        JUNGLE_ID = Registry.BIOME.getRawId(Biomes.JUNGLE);
        BAMBOO_JUNGLE_ID = Registry.BIOME.getRawId(Biomes.BAMBOO_JUNGLE);
    }
}

