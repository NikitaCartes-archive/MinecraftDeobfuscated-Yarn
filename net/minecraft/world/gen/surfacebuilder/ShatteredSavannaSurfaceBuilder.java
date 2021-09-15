/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.class_6557;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class ShatteredSavannaSurfaceBuilder
extends SurfaceBuilder<TernarySurfaceConfig> {
    public ShatteredSavannaSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    public void generate(Random random, class_6557 arg, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, int m, long n, TernarySurfaceConfig ternarySurfaceConfig) {
        if (d > 1.75) {
            SurfaceBuilder.DEFAULT.generate(random, arg, biome, i, j, k, d, blockState, blockState2, l, m, n, SurfaceBuilder.STONE_CONFIG);
        } else if (d > -0.5) {
            SurfaceBuilder.DEFAULT.generate(random, arg, biome, i, j, k, d, blockState, blockState2, l, m, n, SurfaceBuilder.COARSE_DIRT_CONFIG);
        } else {
            SurfaceBuilder.DEFAULT.generate(random, arg, biome, i, j, k, d, blockState, blockState2, l, m, n, SurfaceBuilder.GRASS_CONFIG);
        }
    }
}

