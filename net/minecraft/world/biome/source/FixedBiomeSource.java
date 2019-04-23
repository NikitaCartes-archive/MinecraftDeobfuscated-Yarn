/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

public class FixedBiomeSource
extends BiomeSource {
    private final Biome biome;

    public FixedBiomeSource(FixedBiomeSourceConfig fixedBiomeSourceConfig) {
        this.biome = fixedBiomeSourceConfig.getBiome();
    }

    @Override
    public Biome getBiome(int i, int j) {
        return this.biome;
    }

    @Override
    public Biome[] sampleBiomes(int i, int j, int k, int l, boolean bl) {
        Object[] biomes = new Biome[k * l];
        Arrays.fill(biomes, 0, k * l, this.biome);
        return biomes;
    }

    @Override
    @Nullable
    public BlockPos locateBiome(int i, int j, int k, List<Biome> list, Random random) {
        if (list.contains(this.biome)) {
            return new BlockPos(i - k + random.nextInt(k * 2 + 1), 0, j - k + random.nextInt(k * 2 + 1));
        }
        return null;
    }

    @Override
    public boolean hasStructureFeature(StructureFeature<?> structureFeature) {
        return this.structureFeatures.computeIfAbsent(structureFeature, this.biome::hasStructureFeature);
    }

    @Override
    public Set<BlockState> getTopMaterials() {
        if (this.topMaterials.isEmpty()) {
            this.topMaterials.add(this.biome.getSurfaceConfig().getTopMaterial());
        }
        return this.topMaterials;
    }

    @Override
    public Set<Biome> getBiomesInArea(int i, int j, int k) {
        return Sets.newHashSet(this.biome);
    }
}

