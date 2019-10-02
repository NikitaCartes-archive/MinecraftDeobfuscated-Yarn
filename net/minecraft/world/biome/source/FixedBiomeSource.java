/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;
import org.jetbrains.annotations.Nullable;

public class FixedBiomeSource
extends BiomeSource {
    private final Biome biome;

    public FixedBiomeSource(FixedBiomeSourceConfig fixedBiomeSourceConfig) {
        super(ImmutableSet.of(fixedBiomeSourceConfig.getBiome()));
        this.biome = fixedBiomeSourceConfig.getBiome();
    }

    @Override
    public Biome getStoredBiome(int i, int j, int k) {
        return this.biome;
    }

    @Override
    @Nullable
    public BlockPos locateBiome(int i, int j, int k, int l, List<Biome> list, Random random) {
        if (list.contains(this.biome)) {
            return new BlockPos(i - l + random.nextInt(l * 2 + 1), j, k - l + random.nextInt(l * 2 + 1));
        }
        return null;
    }

    @Override
    public Set<Biome> getBiomesInArea(int i, int j, int k, int l) {
        return Sets.newHashSet(this.biome);
    }
}

