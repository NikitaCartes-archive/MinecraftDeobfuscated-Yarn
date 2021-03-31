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
    private static final int field_31700 = 50;

    public DefaultSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    public void generate(Random random, Chunk chunk, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, long m, TernarySurfaceConfig ternarySurfaceConfig) {
        this.generate(random, chunk, biome, i, j, k, d, blockState, blockState2, ternarySurfaceConfig.getTopMaterial(), ternarySurfaceConfig.getUnderMaterial(), ternarySurfaceConfig.getUnderwaterMaterial(), l);
    }

    protected void generate(Random random, Chunk chunk, Biome biome, int x, int z, int height, double noise, BlockState defaultBlock, BlockState fluidBlock, BlockState topBlock, BlockState underBlock, BlockState underwaterBlock, int seaLevel) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int i = (int)(noise / 3.0 + 3.0 + random.nextDouble() * 0.25);
        if (i == 0) {
            boolean bl = false;
            for (int j = height; j >= 50; --j) {
                mutable.set(x, j, z);
                BlockState blockState = chunk.getBlockState(mutable);
                if (blockState.isAir()) {
                    bl = false;
                    continue;
                }
                if (!blockState.isOf(defaultBlock.getBlock())) continue;
                if (!bl) {
                    BlockState blockState2 = j >= seaLevel ? Blocks.AIR.getDefaultState() : (j == seaLevel - 1 ? (biome.getTemperature(mutable) < 0.15f ? Blocks.ICE.getDefaultState() : fluidBlock) : (j >= seaLevel - (7 + i) ? defaultBlock : underwaterBlock));
                    chunk.setBlockState(mutable, blockState2, false);
                }
                bl = true;
            }
        } else {
            BlockState blockState3 = underBlock;
            int j = -1;
            for (int k = height; k >= 50; --k) {
                mutable.set(x, k, z);
                BlockState blockState2 = chunk.getBlockState(mutable);
                if (blockState2.isAir()) {
                    j = -1;
                    continue;
                }
                if (!blockState2.isOf(defaultBlock.getBlock())) continue;
                if (j == -1) {
                    BlockState blockState4;
                    j = i;
                    if (k >= seaLevel + 2) {
                        blockState4 = topBlock;
                    } else if (k >= seaLevel - 1) {
                        blockState3 = underBlock;
                        blockState4 = topBlock;
                    } else if (k >= seaLevel - 4) {
                        blockState3 = underBlock;
                        blockState4 = underBlock;
                    } else if (k >= seaLevel - (7 + i)) {
                        blockState4 = blockState3;
                    } else {
                        blockState3 = defaultBlock;
                        blockState4 = underwaterBlock;
                    }
                    chunk.setBlockState(mutable, blockState4, false);
                    continue;
                }
                if (j <= 0) continue;
                chunk.setBlockState(mutable, blockState3, false);
                if (--j != 0 || !blockState3.isOf(Blocks.SAND) || i <= 1) continue;
                j = random.nextInt(4) + Math.max(0, k - seaLevel);
                blockState3 = blockState3.isOf(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
            }
        }
    }
}

