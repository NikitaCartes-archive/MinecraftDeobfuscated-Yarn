/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import org.jetbrains.annotations.Nullable;

public class FixedBiomeSource
extends BiomeSource {
    public static final Codec<FixedBiomeSource> field_24717 = ((MapCodec)Biome.REGISTRY_CODEC.fieldOf("biome")).xmap(FixedBiomeSource::new, fixedBiomeSource -> fixedBiomeSource.biome).stable().codec();
    private final Supplier<Biome> biome;

    public FixedBiomeSource(Biome biome) {
        this(() -> biome);
    }

    public FixedBiomeSource(Supplier<Biome> supplier) {
        super(ImmutableList.of(supplier.get()));
        this.biome = supplier;
    }

    @Override
    protected Codec<? extends BiomeSource> method_28442() {
        return field_24717;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public BiomeSource withSeed(long seed) {
        return this;
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.biome.get();
    }

    @Override
    @Nullable
    public BlockPos locateBiome(int x, int y, int z, int radius, int i, List<Biome> biomes, Random random, boolean bl) {
        if (biomes.contains(this.biome.get())) {
            if (bl) {
                return new BlockPos(x, y, z);
            }
            return new BlockPos(x - radius + random.nextInt(radius * 2 + 1), y, z - radius + random.nextInt(radius * 2 + 1));
        }
        return null;
    }

    @Override
    public Set<Biome> getBiomesInArea(int x, int y, int z, int radius) {
        return Sets.newHashSet(this.biome.get());
    }
}

