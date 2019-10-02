/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAccess;
import net.minecraft.world.biome.BiomeAccessType;
import net.minecraft.world.biome.VoronoiBiomeAccessType;

public enum HorizontalVoronoiBiomeAccessType implements BiomeAccessType
{
    INSTANCE;


    @Override
    public Biome getBiome(long l, int i, int j, int k, BiomeAccess.Storage storage) {
        return VoronoiBiomeAccessType.INSTANCE.getBiome(l, i, 0, k, storage);
    }
}

