/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.BlockColumn;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class SwampSurfaceBuilder
extends SurfaceBuilder<TernarySurfaceConfig> {
    public SwampSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    public void generate(Random random, BlockColumn blockColumn, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, int m, long n, TernarySurfaceConfig ternarySurfaceConfig) {
        double e = Biome.FOLIAGE_NOISE.sample((double)i * 0.25, (double)j * 0.25, false);
        if (e > 0.0) {
            for (int o = k; o >= m; --o) {
                if (blockColumn.getState(o).isAir()) continue;
                if (o != 62 || blockColumn.getState(o).isOf(blockState2.getBlock())) break;
                blockColumn.setState(o, blockState2);
                break;
            }
        }
        SurfaceBuilder.DEFAULT.generate(random, blockColumn, biome, i, j, k, d, blockState, blockState2, l, m, n, ternarySurfaceConfig);
    }
}

