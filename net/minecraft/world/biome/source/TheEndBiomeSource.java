/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.densityfunction.DensityFunction;

public class TheEndBiomeSource
extends BiomeSource {
    public static final Codec<TheEndBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter(theEndBiomeSource -> null)).apply((Applicative<TheEndBiomeSource, ?>)instance, instance.stable(TheEndBiomeSource::new)));
    private final RegistryEntry<Biome> centerBiome;
    private final RegistryEntry<Biome> highlandsBiome;
    private final RegistryEntry<Biome> midlandsBiome;
    private final RegistryEntry<Biome> smallIslandsBiome;
    private final RegistryEntry<Biome> barrensBiome;

    public TheEndBiomeSource(Registry<Biome> biomeRegistry) {
        this(biomeRegistry.getOrCreateEntry(BiomeKeys.THE_END), biomeRegistry.getOrCreateEntry(BiomeKeys.END_HIGHLANDS), biomeRegistry.getOrCreateEntry(BiomeKeys.END_MIDLANDS), biomeRegistry.getOrCreateEntry(BiomeKeys.SMALL_END_ISLANDS), biomeRegistry.getOrCreateEntry(BiomeKeys.END_BARRENS));
    }

    private TheEndBiomeSource(RegistryEntry<Biome> centerBiome, RegistryEntry<Biome> highlandsBiome, RegistryEntry<Biome> midlandsBiome, RegistryEntry<Biome> smallIslandsBiome, RegistryEntry<Biome> barrensBiome) {
        super(ImmutableList.of(centerBiome, highlandsBiome, midlandsBiome, smallIslandsBiome, barrensBiome));
        this.centerBiome = centerBiome;
        this.highlandsBiome = highlandsBiome;
        this.midlandsBiome = midlandsBiome;
        this.smallIslandsBiome = smallIslandsBiome;
        this.barrensBiome = barrensBiome;
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        int m;
        int i = BiomeCoords.toBlock(x);
        int j = BiomeCoords.toBlock(y);
        int k = BiomeCoords.toBlock(z);
        int l = ChunkSectionPos.getSectionCoord(i);
        if ((long)l * (long)l + (long)(m = ChunkSectionPos.getSectionCoord(k)) * (long)m <= 4096L) {
            return this.centerBiome;
        }
        int n = (ChunkSectionPos.getSectionCoord(i) * 2 + 1) * 8;
        int o = (ChunkSectionPos.getSectionCoord(k) * 2 + 1) * 8;
        double d = noise.erosion().sample(new DensityFunction.UnblendedNoisePos(n, j, o));
        if (d > 0.25) {
            return this.highlandsBiome;
        }
        if (d >= -0.0625) {
            return this.midlandsBiome;
        }
        if (d < -0.21875) {
            return this.smallIslandsBiome;
        }
        return this.barrensBiome;
    }
}

