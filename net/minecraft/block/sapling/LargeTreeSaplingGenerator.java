/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.sapling;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.MegaTreeFeatureConfig;
import org.jetbrains.annotations.Nullable;

public abstract class LargeTreeSaplingGenerator
extends SaplingGenerator {
    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, BlockPos blockPos, BlockState blockState, Random random) {
        for (int i = 0; i >= -1; --i) {
            for (int j = 0; j >= -1; --j) {
                if (!LargeTreeSaplingGenerator.canGenerateLargeTree(blockState, iWorld, blockPos, i, j)) continue;
                return this.generateLargeTree(iWorld, chunkGenerator, blockPos, blockState, random, i, j);
            }
        }
        return super.generate(iWorld, chunkGenerator, blockPos, blockState, random);
    }

    @Nullable
    protected abstract ConfiguredFeature<MegaTreeFeatureConfig, ?> createLargeTreeFeature(Random var1);

    public boolean generateLargeTree(IWorld iWorld, ChunkGenerator<?> chunkGenerator, BlockPos blockPos, BlockState blockState, Random random, int i, int j) {
        ConfiguredFeature<MegaTreeFeatureConfig, ?> configuredFeature = this.createLargeTreeFeature(random);
        if (configuredFeature == null) {
            return false;
        }
        BlockState blockState2 = Blocks.AIR.getDefaultState();
        iWorld.setBlockState(blockPos.add(i, 0, j), blockState2, 4);
        iWorld.setBlockState(blockPos.add(i + 1, 0, j), blockState2, 4);
        iWorld.setBlockState(blockPos.add(i, 0, j + 1), blockState2, 4);
        iWorld.setBlockState(blockPos.add(i + 1, 0, j + 1), blockState2, 4);
        if (configuredFeature.generate(iWorld, chunkGenerator, random, blockPos.add(i, 0, j))) {
            return true;
        }
        iWorld.setBlockState(blockPos.add(i, 0, j), blockState, 4);
        iWorld.setBlockState(blockPos.add(i + 1, 0, j), blockState, 4);
        iWorld.setBlockState(blockPos.add(i, 0, j + 1), blockState, 4);
        iWorld.setBlockState(blockPos.add(i + 1, 0, j + 1), blockState, 4);
        return false;
    }

    public static boolean canGenerateLargeTree(BlockState state, BlockView world, BlockPos pos, int x, int z) {
        Block block = state.getBlock();
        return block == world.getBlockState(pos.add(x, 0, z)).getBlock() && block == world.getBlockState(pos.add(x + 1, 0, z)).getBlock() && block == world.getBlockState(pos.add(x, 0, z + 1)).getBlock() && block == world.getBlockState(pos.add(x + 1, 0, z + 1)).getBlock();
    }
}

