/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.biome.source.BiomeCoords;

public enum DirectBiomeAccessType implements BiomeAccessType
{
    INSTANCE;


    @Override
    public Biome getBiome(long seed, int x, int y, int z, BiomeAccess.Storage storage) {
        return storage.getBiomeForNoiseGen(BiomeCoords.fromBlock(x), BiomeCoords.fromBlock(y), BiomeCoords.fromBlock(z));
    }
}

