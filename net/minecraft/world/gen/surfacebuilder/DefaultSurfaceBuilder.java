/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.surfacebuilder;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class DefaultSurfaceBuilder
extends SurfaceBuilder<TernarySurfaceConfig> {
    public DefaultSurfaceBuilder(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
        super(function);
    }

    @Override
    public void generate(Random random, Chunk chunk, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, long m, TernarySurfaceConfig ternarySurfaceConfig) {
        this.generate(random, chunk, biome, i, j, k, d, blockState, blockState2, ternarySurfaceConfig.getTopMaterial(), ternarySurfaceConfig.getUnderMaterial(), ternarySurfaceConfig.getUnderwaterMaterial(), l);
    }

    protected void generate(Random random, Chunk chunk, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, BlockState blockState3, BlockState blockState4, BlockState blockState5, int l) {
        BlockState blockState6 = blockState3;
        BlockState blockState7 = blockState4;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int m = -1;
        int n = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        int o = i & 0xF;
        int p = j & 0xF;
        for (int q = k; q >= 0; --q) {
            mutable.set(o, q, p);
            BlockState blockState8 = chunk.getBlockState(mutable);
            if (blockState8.isAir()) {
                m = -1;
                continue;
            }
            if (blockState8.getBlock() != blockState.getBlock()) continue;
            if (m == -1) {
                if (n <= 0) {
                    blockState6 = Blocks.AIR.getDefaultState();
                    blockState7 = blockState;
                } else if (q >= l - 4 && q <= l + 1) {
                    blockState6 = blockState3;
                    blockState7 = blockState4;
                }
                if (q < l && (blockState6 == null || blockState6.isAir())) {
                    blockState6 = biome.getTemperature(mutable.set(i, q, j)) < 0.15f ? Blocks.ICE.getDefaultState() : blockState2;
                    mutable.set(o, q, p);
                }
                m = n;
                if (q >= l - 1) {
                    chunk.setBlockState(mutable, blockState6, false);
                    continue;
                }
                if (q < l - 7 - n) {
                    blockState6 = Blocks.AIR.getDefaultState();
                    blockState7 = blockState;
                    chunk.setBlockState(mutable, blockState5, false);
                    continue;
                }
                chunk.setBlockState(mutable, blockState7, false);
                continue;
            }
            if (m <= 0) continue;
            chunk.setBlockState(mutable, blockState7, false);
            if (--m != 0 || blockState7.getBlock() != Blocks.SAND || n <= 1) continue;
            m = random.nextInt(4) + Math.max(0, q - 63);
            blockState7 = blockState7.getBlock() == Blocks.RED_SAND ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
        }
    }
}

