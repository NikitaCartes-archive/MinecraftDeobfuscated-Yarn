/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class MountainSurfaceBuilder
extends SurfaceBuilder<TernarySurfaceConfig> {
    public MountainSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    public void generate(Random random, Chunk chunk, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, int m, long n, TernarySurfaceConfig ternarySurfaceConfig) {
        if (d > 1.0) {
            SurfaceBuilder.DEFAULT.generate(random, chunk, biome, i, j, k, d, blockState, blockState2, l, m, n, SurfaceBuilder.STONE_CONFIG);
        } else {
            SurfaceBuilder.DEFAULT.generate(random, chunk, biome, i, j, k, d, blockState, blockState2, l, m, n, SurfaceBuilder.GRASS_CONFIG);
        }
    }
}

