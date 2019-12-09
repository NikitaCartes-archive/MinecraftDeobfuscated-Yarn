/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.SpreadableBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.FlowerFeature;

public class GrassBlock
extends SpreadableBlock
implements Fertilizable {
    public GrassBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return world.getBlockState(pos.up()).isAir();
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        BlockPos blockPos = pos.up();
        BlockState blockState = Blocks.GRASS.getDefaultState();
        block0: for (int i = 0; i < 128; ++i) {
            BlockState blockState3;
            BlockPos blockPos2 = blockPos;
            for (int j = 0; j < i / 16; ++j) {
                if (world.getBlockState((blockPos2 = blockPos2.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1)).down()).getBlock() != this || world.getBlockState(blockPos2).isFullCube(world, blockPos2)) continue block0;
            }
            BlockState blockState2 = world.getBlockState(blockPos2);
            if (blockState2.getBlock() == blockState.getBlock() && random.nextInt(10) == 0) {
                ((Fertilizable)((Object)blockState.getBlock())).grow(world, random, blockPos2, blockState2);
            }
            if (!blockState2.isAir()) continue;
            if (random.nextInt(8) == 0) {
                List<ConfiguredFeature<?, ?>> list = world.getBiome(blockPos2).getFlowerFeatures();
                if (list.isEmpty()) continue;
                ConfiguredFeature<?, ?> configuredFeature = ((DecoratedFeatureConfig)list.get((int)0).config).feature;
                blockState3 = ((FlowerFeature)configuredFeature.feature).getFlowerToPlace(random, blockPos2, configuredFeature.config);
            } else {
                blockState3 = blockState;
            }
            if (!blockState3.canPlaceAt(world, blockPos2)) continue;
            world.setBlockState(blockPos2, blockState3, 3);
        }
    }
}

