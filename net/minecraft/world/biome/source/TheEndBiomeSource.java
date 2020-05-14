/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;

public class TheEndBiomeSource
extends BiomeSource {
    private final SimplexNoiseSampler noise;
    private static final Set<Biome> BIOMES = ImmutableSet.of(Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS);

    public TheEndBiomeSource(long l) {
        super(BIOMES);
        ChunkRandom chunkRandom = new ChunkRandom(l);
        chunkRandom.consume(17292);
        this.noise = new SimplexNoiseSampler(chunkRandom);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public BiomeSource create(long seed) {
        return new TheEndBiomeSource(seed);
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        int i = biomeX >> 2;
        int j = biomeZ >> 2;
        if ((long)i * (long)i + (long)j * (long)j <= 4096L) {
            return Biomes.THE_END;
        }
        float f = this.getNoiseAt(i * 2 + 1, j * 2 + 1);
        if (f > 40.0f) {
            return Biomes.END_HIGHLANDS;
        }
        if (f >= 0.0f) {
            return Biomes.END_MIDLANDS;
        }
        if (f < -20.0f) {
            return Biomes.SMALL_END_ISLANDS;
        }
        return Biomes.END_BARRENS;
    }

    @Override
    public float getNoiseAt(int x, int z) {
        int i = x / 2;
        int j = z / 2;
        int k = x % 2;
        int l = z % 2;
        float f = 100.0f - MathHelper.sqrt(x * x + z * z) * 8.0f;
        f = MathHelper.clamp(f, -100.0f, 80.0f);
        for (int m = -12; m <= 12; ++m) {
            for (int n = -12; n <= 12; ++n) {
                long o = i + m;
                long p = j + n;
                if (o * o + p * p <= 4096L || !(this.noise.sample(o, p) < (double)-0.9f)) continue;
                float g = (MathHelper.abs(o) * 3439.0f + MathHelper.abs(p) * 147.0f) % 13.0f + 9.0f;
                float h = k - m * 2;
                float q = l - n * 2;
                float r = 100.0f - MathHelper.sqrt(h * h + q * q) * g;
                r = MathHelper.clamp(r, -100.0f, 80.0f);
                f = Math.max(f, r);
            }
        }
        return f;
    }
}

