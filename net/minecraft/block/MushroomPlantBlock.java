/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.HugeMushroomFeatureConfig;

public class MushroomPlantBlock
extends PlantBlock
implements Fertilizable {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);

    public MushroomPlantBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityContext context) {
        return SHAPE;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (random.nextInt(25) == 0) {
            int i = 5;
            int j = 4;
            for (BlockPos blockPos : BlockPos.iterate(pos.add(-4, -1, -4), pos.add(4, 1, 4))) {
                if (world.getBlockState(blockPos).getBlock() != this || --i > 0) continue;
                return;
            }
            BlockPos blockPos2 = pos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            for (int k = 0; k < 4; ++k) {
                if (world.isAir(blockPos2) && state.canPlaceAt(world, blockPos2)) {
                    pos = blockPos2;
                }
                blockPos2 = pos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            }
            if (world.isAir(blockPos2) && state.canPlaceAt(world, blockPos2)) {
                world.setBlockState(blockPos2, state, 2);
            }
        }
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isFullOpaque(world, pos);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (block == Blocks.MYCELIUM || block == Blocks.PODZOL) {
            return true;
        }
        return world.getBaseLightLevel(pos, 0) < 13 && this.canPlantOnTop(blockState, world, blockPos);
    }

    public boolean trySpawningBigMushroom(ServerWorld serverWorld, BlockPos pos, BlockState state, Random random) {
        ConfiguredFeature<HugeMushroomFeatureConfig, ?> configuredFeature;
        serverWorld.removeBlock(pos, false);
        if (this == Blocks.BROWN_MUSHROOM) {
            configuredFeature = Feature.HUGE_BROWN_MUSHROOM.configure(DefaultBiomeFeatures.HUGE_BROWN_MUSHROOM_CONFIG);
        } else if (this == Blocks.RED_MUSHROOM) {
            configuredFeature = Feature.HUGE_RED_MUSHROOM.configure(DefaultBiomeFeatures.HUGE_RED_MUSHROOM_CONFIG);
        } else {
            serverWorld.setBlockState(pos, state, 3);
            return false;
        }
        if (configuredFeature.generate(serverWorld, serverWorld.getChunkManager().getChunkGenerator(), random, pos)) {
            return true;
        }
        serverWorld.setBlockState(pos, state, 3);
        return false;
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return (double)random.nextFloat() < 0.4;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        this.trySpawningBigMushroom(world, pos, state, random);
    }

    @Override
    public boolean shouldPostProcess(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }
}

