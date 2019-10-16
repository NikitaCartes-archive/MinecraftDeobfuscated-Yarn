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
    public boolean isFertilizable(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
        return blockView.getBlockState(blockPos.up()).isAir();
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public void grow(ServerWorld serverWorld, Random random, BlockPos blockPos, BlockState blockState) {
        BlockPos blockPos2 = blockPos.up();
        BlockState blockState2 = Blocks.GRASS.getDefaultState();
        block0: for (int i = 0; i < 128; ++i) {
            BlockState blockState4;
            BlockPos blockPos3 = blockPos2;
            for (int j = 0; j < i / 16; ++j) {
                if (serverWorld.getBlockState((blockPos3 = blockPos3.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1)).method_10074()).getBlock() != this || serverWorld.getBlockState(blockPos3).method_21743(serverWorld, blockPos3)) continue block0;
            }
            BlockState blockState3 = serverWorld.getBlockState(blockPos3);
            if (blockState3.getBlock() == blockState2.getBlock() && random.nextInt(10) == 0) {
                ((Fertilizable)((Object)blockState2.getBlock())).grow(serverWorld, random, blockPos3, blockState3);
            }
            if (!blockState3.isAir()) continue;
            if (random.nextInt(8) == 0) {
                List<ConfiguredFeature<?, ?>> list = serverWorld.getBiome(blockPos3).getFlowerFeatures();
                if (list.isEmpty()) continue;
                ConfiguredFeature<?, ?> configuredFeature = ((DecoratedFeatureConfig)list.get((int)0).config).feature;
                blockState4 = ((FlowerFeature)configuredFeature.feature).getFlowerToPlace(random, blockPos3, configuredFeature.config);
            } else {
                blockState4 = blockState2;
            }
            if (!blockState4.canPlaceAt(serverWorld, blockPos3)) continue;
            serverWorld.setBlockState(blockPos3, blockState4, 3);
        }
    }
}

