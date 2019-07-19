/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.CheckerboardBiomeSourceConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

public class CheckerboardBiomeSource
extends BiomeSource {
    private final Biome[] biomeArray;
    private final int gridSize;

    public CheckerboardBiomeSource(CheckerboardBiomeSourceConfig checkerboardBiomeSourceConfig) {
        this.biomeArray = checkerboardBiomeSourceConfig.getBiomes();
        this.gridSize = checkerboardBiomeSourceConfig.getSize() + 4;
    }

    @Override
    public Biome getBiome(int i, int j) {
        return this.biomeArray[Math.abs(((i >> this.gridSize) + (j >> this.gridSize)) % this.biomeArray.length)];
    }

    @Override
    public Biome[] sampleBiomes(int i, int j, int k, int l, boolean bl) {
        Biome[] biomes = new Biome[k * l];
        for (int m = 0; m < l; ++m) {
            for (int n = 0; n < k; ++n) {
                Biome biome;
                int o = Math.abs(((i + m >> this.gridSize) + (j + n >> this.gridSize)) % this.biomeArray.length);
                biomes[m * k + n] = biome = this.biomeArray[o];
            }
        }
        return biomes;
    }

    @Override
    @Nullable
    public BlockPos locateBiome(int i, int j, int k, List<Biome> list, Random random) {
        return null;
    }

    @Override
    public boolean hasStructureFeature(StructureFeature<?> structureFeature2) {
        return this.structureFeatures.computeIfAbsent(structureFeature2, structureFeature -> {
            for (Biome biome : this.biomeArray) {
                if (!biome.hasStructureFeature(structureFeature)) continue;
                return true;
            }
            return false;
        });
    }

    @Override
    public Set<BlockState> getTopMaterials() {
        if (this.topMaterials.isEmpty()) {
            for (Biome biome : this.biomeArray) {
                this.topMaterials.add(biome.getSurfaceConfig().getTopMaterial());
            }
        }
        return this.topMaterials;
    }

    @Override
    public Set<Biome> getBiomesInArea(int i, int j, int k) {
        return Sets.newHashSet(this.biomeArray);
    }
}

