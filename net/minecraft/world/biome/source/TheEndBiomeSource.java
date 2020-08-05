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
import net.minecraft.class_5505;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;

public class TheEndBiomeSource
extends BiomeSource {
    public static final Codec<TheEndBiomeSource> field_24730 = RecordCodecBuilder.create(instance -> instance.group(class_5505.method_31148(Registry.BIOME_KEY).forGetter(theEndBiomeSource -> theEndBiomeSource.field_26699), ((MapCodec)Codec.LONG.fieldOf("seed")).stable().forGetter(theEndBiomeSource -> theEndBiomeSource.field_24731)).apply((Applicative<TheEndBiomeSource, ?>)instance, instance.stable(TheEndBiomeSource::new)));
    private final SimplexNoiseSampler noise;
    private final Registry<Biome> field_26699;
    private final long field_24731;
    private final Biome field_26700;
    private final Biome field_26701;
    private final Biome field_26702;
    private final Biome field_26703;
    private final Biome field_26704;

    public TheEndBiomeSource(Registry<Biome> registry, long l) {
        this(registry, l, registry.method_31140(Biomes.THE_END), registry.method_31140(Biomes.END_HIGHLANDS), registry.method_31140(Biomes.END_MIDLANDS), registry.method_31140(Biomes.SMALL_END_ISLANDS), registry.method_31140(Biomes.END_BARRENS));
    }

    private TheEndBiomeSource(Registry<Biome> registry, long l, Biome biome, Biome biome2, Biome biome3, Biome biome4, Biome biome5) {
        super(ImmutableList.of(biome, biome2, biome3, biome4, biome5));
        this.field_26699 = registry;
        this.field_24731 = l;
        this.field_26700 = biome;
        this.field_26701 = biome2;
        this.field_26702 = biome3;
        this.field_26703 = biome4;
        this.field_26704 = biome5;
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
        return new TheEndBiomeSource(this.field_26699, seed, this.field_26700, this.field_26701, this.field_26702, this.field_26703, this.field_26704);
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        int i = biomeX >> 2;
        int j = biomeZ >> 2;
        if ((long)i * (long)i + (long)j * (long)j <= 4096L) {
            return this.field_26700;
        }
        float f = TheEndBiomeSource.getNoiseAt(this.noise, i * 2 + 1, j * 2 + 1);
        if (f > 40.0f) {
            return this.field_26701;
        }
        if (f >= 0.0f) {
            return this.field_26702;
        }
        if (f < -20.0f) {
            return this.field_26703;
        }
        return this.field_26704;
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

