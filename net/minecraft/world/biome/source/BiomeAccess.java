/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.biome.source.BiomeSource;

public class BiomeAccess {
    private final Storage storage;
    private final long seed;
    private final BiomeAccessType type;

    public BiomeAccess(Storage storage, long seed, BiomeAccessType type) {
        this.storage = storage;
        this.seed = seed;
        this.type = type;
    }

    public BiomeAccess withSource(BiomeSource source) {
        return new BiomeAccess(source, this.seed, this.type);
    }

    public Biome getBiome(BlockPos pos) {
        return this.type.getBiome(this.seed, pos.getX(), pos.getY(), pos.getZ(), this.storage);
    }

    @Environment(value=EnvType.CLIENT)
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.storage.getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
    }

    public static interface Storage {
        public Biome getBiomeForNoiseGen(int var1, int var2, int var3);
    }
}

