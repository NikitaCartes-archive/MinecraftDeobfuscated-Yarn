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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import org.jetbrains.annotations.Nullable;

public class FixedBiomeSource
extends BiomeSource {
    public static final Codec<FixedBiomeSource> field_24717 = ((MapCodec)Registry.BIOME.fieldOf("biome")).xmap(FixedBiomeSource::new, fixedBiomeSource -> fixedBiomeSource.biome).stable().codec();
    private final Biome biome;

    public FixedBiomeSource(Biome biome) {
        super(ImmutableList.of(biome));
        this.biome = biome;
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
        return this.biome;
    }

    @Override
    @Nullable
    public BlockPos method_24385(int i, int j, int k, int l, int m, List<Biome> list, Random random, boolean bl) {
        if (list.contains(this.biome)) {
            if (bl) {
                return new BlockPos(i, j, k);
            }
            return new BlockPos(i - l + random.nextInt(l * 2 + 1), j, k - l + random.nextInt(l * 2 + 1));
        }
        return null;
    }

    @Override
    public Set<Biome> getBiomesInArea(int x, int y, int z, int radius) {
        return Sets.newHashSet(this.biome);
    }
}

