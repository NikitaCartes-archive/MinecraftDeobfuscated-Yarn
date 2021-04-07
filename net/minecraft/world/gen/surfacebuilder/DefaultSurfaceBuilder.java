/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class DefaultSurfaceBuilder
extends SurfaceBuilder<TernarySurfaceConfig> {
    public DefaultSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    public void generate(Random random, Chunk chunk, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, int m, long n, TernarySurfaceConfig ternarySurfaceConfig) {
        this.generate(random, chunk, biome, i, j, k, d, blockState, blockState2, ternarySurfaceConfig.getTopMaterial(), ternarySurfaceConfig.getUnderMaterial(), ternarySurfaceConfig.getUnderwaterMaterial(), l, m);
    }

    protected void generate(Random random, Chunk chunk, Biome biome, int x, int z, int height, double noise, BlockState defaultBlock, BlockState fluidBlock, BlockState topBlock, BlockState underBlock, BlockState underwaterBlock, int seaLevel, int i) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int j = (int)(noise / 3.0 + 3.0 + random.nextDouble() * 0.25);
        if (j == 0) {
            boolean bl = false;
            for (int k = height; k >= i; --k) {
                mutable.set(x, k, z);
                BlockState blockState = chunk.getBlockState(mutable);
                if (blockState.isAir()) {
                    bl = false;
                    continue;
                }
                if (!blockState.isOf(defaultBlock.getBlock())) continue;
                if (!bl) {
                    BlockState blockState2 = k >= seaLevel ? Blocks.AIR.getDefaultState() : (k == seaLevel - 1 ? (biome.getTemperature(mutable) < 0.15f ? Blocks.ICE.getDefaultState() : fluidBlock) : (k >= seaLevel - (7 + j) ? defaultBlock : underwaterBlock));
                    chunk.setBlockState(mutable, blockState2, false);
                }
                bl = true;
            }
        } else {
            BlockState blockState3 = underBlock;
            int k = -1;
            for (int l = height; l >= i; --l) {
                mutable.set(x, l, z);
                BlockState blockState2 = chunk.getBlockState(mutable);
                if (blockState2.isAir()) {
                    k = -1;
                    continue;
                }
                if (!blockState2.isOf(defaultBlock.getBlock())) continue;
                if (k == -1) {
                    BlockState blockState4;
                    k = j;
                    if (l >= seaLevel + 2) {
                        blockState4 = topBlock;
                    } else if (l >= seaLevel - 1) {
                        blockState3 = underBlock;
                        blockState4 = topBlock;
                    } else if (l >= seaLevel - 4) {
                        blockState3 = underBlock;
                        blockState4 = underBlock;
                    } else if (l >= seaLevel - (7 + j)) {
                        blockState4 = blockState3;
                    } else {
                        blockState3 = defaultBlock;
                        blockState4 = underwaterBlock;
                    }
                    chunk.setBlockState(mutable, blockState4, false);
                    continue;
                }
                if (k <= 0) continue;
                chunk.setBlockState(mutable, blockState3, false);
                if (--k != 0 || !blockState3.isOf(Blocks.SAND) || j <= 1) continue;
                k = random.nextInt(4) + Math.max(0, l - seaLevel);
                blockState3 = blockState3.isOf(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
            }
        }
    }
}

