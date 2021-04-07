/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class SwampSurfaceBuilder
extends SurfaceBuilder<TernarySurfaceConfig> {
    public SwampSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    public void generate(Random random, Chunk chunk, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, int m, long n, TernarySurfaceConfig ternarySurfaceConfig) {
        double e = Biome.FOLIAGE_NOISE.sample((double)i * 0.25, (double)j * 0.25, false);
        if (e > 0.0) {
            int o = i & 0xF;
            int p = j & 0xF;
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (int q = k; q >= m; --q) {
                mutable.set(o, q, p);
                if (chunk.getBlockState(mutable).isAir()) continue;
                if (q != 62 || chunk.getBlockState(mutable).isOf(blockState2.getBlock())) break;
                chunk.setBlockState(mutable, blockState2, false);
                break;
            }
        }
        SurfaceBuilder.DEFAULT.generate(random, chunk, biome, i, j, k, d, blockState, blockState2, l, m, n, ternarySurfaceConfig);
    }
}

