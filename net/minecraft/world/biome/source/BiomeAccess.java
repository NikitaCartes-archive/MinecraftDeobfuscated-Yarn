/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.biome.source.BiomeSource;

public class BiomeAccess {
    private final Storage storage;
    private final long seed;
    private final BiomeAccessType type;

    public BiomeAccess(Storage storage, long l, BiomeAccessType biomeAccessType) {
        this.storage = storage;
        this.seed = l;
        this.type = biomeAccessType;
    }

    public BiomeAccess withSource(BiomeSource biomeSource) {
        return new BiomeAccess(biomeSource, this.seed, this.type);
    }

    public Biome getBiome(BlockPos blockPos) {
        return this.type.getBiome(this.seed, blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.storage);
    }

    public static interface Storage {
        public Biome getBiomeForNoiseGen(int var1, int var2, int var3);
    }
}

