/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import com.google.common.hash.Hashing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5742;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.biome.source.BiomeSource;

public class BiomeAccess {
    private static final int field_28106 = class_5742.method_33100(8);
    private final Storage storage;
    private final long seed;
    private final BiomeAccessType type;

    public BiomeAccess(Storage storage, long seed, BiomeAccessType type) {
        this.storage = storage;
        this.seed = seed;
        this.type = type;
    }

    public static long hashSeed(long seed) {
        return Hashing.sha256().hashLong(seed).asLong();
    }

    public BiomeAccess withSource(BiomeSource source) {
        return new BiomeAccess(source, this.seed, this.type);
    }

    public Biome getBiome(BlockPos pos) {
        return this.type.getBiome(this.seed, pos.getX(), pos.getY(), pos.getZ(), this.storage);
    }

    @Environment(value=EnvType.CLIENT)
    public Biome getBiomeForNoiseGen(double x, double y, double z) {
        int i = class_5742.method_33100(MathHelper.floor(x));
        int j = class_5742.method_33100(MathHelper.floor(y));
        int k = class_5742.method_33100(MathHelper.floor(z));
        return this.getBiomeForNoiseGen(i, j, k);
    }

    @Environment(value=EnvType.CLIENT)
    public Biome getBiomeForNoiseGen(BlockPos pos) {
        int i = class_5742.method_33100(pos.getX());
        int j = class_5742.method_33100(pos.getY());
        int k = class_5742.method_33100(pos.getZ());
        return this.getBiomeForNoiseGen(i, j, k);
    }

    @Environment(value=EnvType.CLIENT)
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.storage.getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
    }

    public Biome method_31608(int i, int j) {
        return this.storage.method_31609(i, j);
    }

    public static interface Storage {
        public Biome getBiomeForNoiseGen(int var1, int var2, int var3);

        default public Biome method_31609(int i, int j) {
            return this.getBiomeForNoiseGen(class_5742.method_33102(i) + field_28106, 0, class_5742.method_33102(j) + field_28106);
        }
    }
}

