/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.MergingLayer;
import net.minecraft.world.biome.layer.util.IdentityCoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;

public enum AddRiversLayer implements MergingLayer,
IdentityCoordinateTransformer
{
    INSTANCE;

    private static final int FROZEN_RIVER_ID;
    private static final int SNOWY_TUNDRA_ID;
    private static final int MUSHROOM_FIELDS_ID;
    private static final int MUSHROOM_FIELD_SHORE_ID;
    private static final int RIVER_ID;

    @Override
    public int sample(LayerRandomnessSource context, LayerSampler sampler1, LayerSampler sampler2, int x, int z) {
        int i = sampler1.sample(this.transformX(x), this.transformZ(z));
        int j = sampler2.sample(this.transformX(x), this.transformZ(z));
        if (BiomeLayers.isOcean(i)) {
            return i;
        }
        if (j == RIVER_ID) {
            if (i == SNOWY_TUNDRA_ID) {
                return FROZEN_RIVER_ID;
            }
            if (i == MUSHROOM_FIELDS_ID || i == MUSHROOM_FIELD_SHORE_ID) {
                return MUSHROOM_FIELD_SHORE_ID;
            }
            return j & 0xFF;
        }
        return i;
    }

    static {
        FROZEN_RIVER_ID = BuiltinRegistries.BIOME.getRawId(Biomes.FROZEN_RIVER);
        SNOWY_TUNDRA_ID = BuiltinRegistries.BIOME.getRawId(Biomes.SNOWY_TUNDRA);
        MUSHROOM_FIELDS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.MUSHROOM_FIELDS);
        MUSHROOM_FIELD_SHORE_ID = BuiltinRegistries.BIOME.getRawId(Biomes.MUSHROOM_FIELD_SHORE);
        RIVER_ID = BuiltinRegistries.BIOME.getRawId(Biomes.RIVER);
    }
}

