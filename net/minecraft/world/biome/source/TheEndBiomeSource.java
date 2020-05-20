/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
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
    public static final Codec<TheEndBiomeSource> field_24730 = ((MapCodec)Codec.LONG.fieldOf("seed")).xmap(TheEndBiomeSource::new, theEndBiomeSource -> theEndBiomeSource.field_24731).stable().codec();
    private final SimplexNoiseSampler noise;
    private static final List<Biome> BIOMES = ImmutableList.of(Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS);
    private final long field_24731;

    public TheEndBiomeSource(long l) {
        super(BIOMES);
        this.field_24731 = l;
        ChunkRandom chunkRandom = new ChunkRandom(l);
        chunkRandom.consume(17292);
        this.noise = new SimplexNoiseSampler(chunkRandom);
    }

    @Override
    protected Codec<? extends BiomeSource> method_28442() {
        return field_24730;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public BiomeSource withSeed(long seed) {
        return new TheEndBiomeSource(seed);
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        int i = biomeX >> 2;
        int j = biomeZ >> 2;
        if ((long)i * (long)i + (long)j * (long)j <= 4096L) {
            return Biomes.THE_END;
        }
        float f = TheEndBiomeSource.getNoiseAt(this.noise, i * 2 + 1, j * 2 + 1);
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

    public boolean method_28479(long l) {
        return this.field_24731 == l;
    }

    public static float getNoiseAt(SimplexNoiseSampler simplexNoiseSampler, int i, int j) {
        int k = i / 2;
        int l = j / 2;
        int m = i % 2;
        int n = j % 2;
        float f = 100.0f - MathHelper.sqrt(i * i + j * j) * 8.0f;
        f = MathHelper.clamp(f, -100.0f, 80.0f);
        for (int o = -12; o <= 12; ++o) {
            for (int p = -12; p <= 12; ++p) {
                long q = k + o;
                long r = l + p;
                if (q * q + r * r <= 4096L || !(simplexNoiseSampler.sample(q, r) < (double)-0.9f)) continue;
                float g = (MathHelper.abs(q) * 3439.0f + MathHelper.abs(r) * 147.0f) % 13.0f + 9.0f;
                float h = m - o * 2;
                float s = n - p * 2;
                float t = 100.0f - MathHelper.sqrt(h * h + s * s) * g;
                t = MathHelper.clamp(t, -100.0f, 80.0f);
                f = Math.max(f, t);
            }
        }
        return f;
    }
}

