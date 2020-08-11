/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;

public class TheEndBiomeSource
extends BiomeSource {
    public static final Codec<TheEndBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter(theEndBiomeSource -> theEndBiomeSource.biomeRegistry), ((MapCodec)Codec.LONG.fieldOf("seed")).stable().forGetter(theEndBiomeSource -> theEndBiomeSource.seed)).apply((Applicative<TheEndBiomeSource, ?>)instance, instance.stable(TheEndBiomeSource::new)));
    private final SimplexNoiseSampler noise;
    private final Registry<Biome> biomeRegistry;
    private final long seed;
    private final Biome centerBiome;
    private final Biome highlandsBiome;
    private final Biome midlandsBiome;
    private final Biome smallIslandsBiome;
    private final Biome barrensBiome;

    public TheEndBiomeSource(Registry<Biome> biomeRegistry, long seed) {
        this(biomeRegistry, seed, biomeRegistry.getOrThrow(BiomeKeys.THE_END), biomeRegistry.getOrThrow(BiomeKeys.END_HIGHLANDS), biomeRegistry.getOrThrow(BiomeKeys.END_MIDLANDS), biomeRegistry.getOrThrow(BiomeKeys.SMALL_END_ISLANDS), biomeRegistry.getOrThrow(BiomeKeys.END_BARRENS));
    }

    private TheEndBiomeSource(Registry<Biome> biomeRegistry, long seed, Biome centerBiome, Biome highlandsBiome, Biome midlandsBiome, Biome smallIslandsBiome, Biome barrensBiome) {
        super(ImmutableList.of(centerBiome, highlandsBiome, midlandsBiome, smallIslandsBiome, barrensBiome));
        this.biomeRegistry = biomeRegistry;
        this.seed = seed;
        this.centerBiome = centerBiome;
        this.highlandsBiome = highlandsBiome;
        this.midlandsBiome = midlandsBiome;
        this.smallIslandsBiome = smallIslandsBiome;
        this.barrensBiome = barrensBiome;
        ChunkRandom chunkRandom = new ChunkRandom(seed);
        chunkRandom.consume(17292);
        this.noise = new SimplexNoiseSampler(chunkRandom);
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public BiomeSource withSeed(long seed) {
        return new TheEndBiomeSource(this.biomeRegistry, seed, this.centerBiome, this.highlandsBiome, this.midlandsBiome, this.smallIslandsBiome, this.barrensBiome);
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        int i = biomeX >> 2;
        int j = biomeZ >> 2;
        if ((long)i * (long)i + (long)j * (long)j <= 4096L) {
            return this.centerBiome;
        }
        float f = TheEndBiomeSource.getNoiseAt(this.noise, i * 2 + 1, j * 2 + 1);
        if (f > 40.0f) {
            return this.highlandsBiome;
        }
        if (f >= 0.0f) {
            return this.midlandsBiome;
        }
        if (f < -20.0f) {
            return this.smallIslandsBiome;
        }
        return this.barrensBiome;
    }

    public boolean isSeedEqual(long seed) {
        return this.seed == seed;
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

