/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.sapling;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import org.jetbrains.annotations.Nullable;

public abstract class LargeTreeSaplingGenerator
extends SaplingGenerator {
    @Override
    public boolean generate(ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random random) {
        for (int i = 0; i >= -1; --i) {
            for (int j = 0; j >= -1; --j) {
                if (!LargeTreeSaplingGenerator.canGenerateLargeTree(state, world, pos, i, j)) continue;
                return this.generateLargeTree(world, chunkGenerator, pos, state, random, i, j);
            }
        }
        return super.generate(world, chunkGenerator, pos, state, random);
    }

    @Nullable
    protected abstract ConfiguredFeature<TreeFeatureConfig, ?> createLargeTreeFeature(Random var1);

    public boolean generateLargeTree(ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random random, int x, int z) {
        ConfiguredFeature<TreeFeatureConfig, ?> configuredFeature = this.createLargeTreeFeature(random);
        if (configuredFeature == null) {
            return false;
        }
        ((TreeFeatureConfig)configuredFeature.config).ignoreFluidCheck();
        BlockState blockState = Blocks.AIR.getDefaultState();
        world.setBlockState(pos.add(x, 0, z), blockState, 4);
        world.setBlockState(pos.add(x + 1, 0, z), blockState, 4);
        world.setBlockState(pos.add(x, 0, z + 1), blockState, 4);
        world.setBlockState(pos.add(x + 1, 0, z + 1), blockState, 4);
        if (configuredFeature.generate(world, chunkGenerator, random, pos.add(x, 0, z))) {
            return true;
        }
        world.setBlockState(pos.add(x, 0, z), state, 4);
        world.setBlockState(pos.add(x + 1, 0, z), state, 4);
        world.setBlockState(pos.add(x, 0, z + 1), state, 4);
        world.setBlockState(pos.add(x + 1, 0, z + 1), state, 4);
        return false;
    }

    public static boolean canGenerateLargeTree(BlockState state, BlockView world, BlockPos pos, int x, int z) {
        Block block = state.getBlock();
        return block == world.getBlockState(pos.add(x, 0, z)).getBlock() && block == world.getBlockState(pos.add(x + 1, 0, z)).getBlock() && block == world.getBlockState(pos.add(x, 0, z + 1)).getBlock() && block == world.getBlockState(pos.add(x + 1, 0, z + 1)).getBlock();
    }
}

