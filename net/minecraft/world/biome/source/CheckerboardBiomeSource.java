/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.CheckerboardBiomeSourceConfig;

public class CheckerboardBiomeSource
extends BiomeSource {
    private final Biome[] biomeArray;
    private final int gridSize;

    public CheckerboardBiomeSource(CheckerboardBiomeSourceConfig checkerboardBiomeSourceConfig) {
        super(ImmutableSet.copyOf(checkerboardBiomeSourceConfig.getBiomes()));
        this.biomeArray = checkerboardBiomeSourceConfig.getBiomes();
        this.gridSize = checkerboardBiomeSourceConfig.getSize() + 2;
    }

    @Override
    public Biome getStoredBiome(int i, int j, int k) {
        return this.biomeArray[Math.abs(((i >> this.gridSize) + (k >> this.gridSize)) % this.biomeArray.length)];
    }
}

