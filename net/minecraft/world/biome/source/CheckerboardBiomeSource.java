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

    public CheckerboardBiomeSource(CheckerboardBiomeSourceConfig config) {
        super(ImmutableSet.copyOf(config.getBiomes()));
        this.biomeArray = config.getBiomes();
        this.gridSize = config.getSize() + 2;
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.biomeArray[Math.abs(((biomeX >> this.gridSize) + (biomeZ >> this.gridSize)) % this.biomeArray.length)];
    }
}

