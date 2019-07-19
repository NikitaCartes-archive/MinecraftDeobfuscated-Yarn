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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlantedFeatureConfig;

public class MushroomPlantBlock
extends PlantBlock
implements Fertilizable {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);

    public MushroomPlantBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        return SHAPE;
    }

    @Override
    public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        if (random.nextInt(25) == 0) {
            int i = 5;
            int j = 4;
            for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-4, -1, -4), blockPos.add(4, 1, 4))) {
                if (world.getBlockState(blockPos2).getBlock() != this || --i > 0) continue;
                return;
            }
            BlockPos blockPos3 = blockPos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            for (int k = 0; k < 4; ++k) {
                if (world.isAir(blockPos3) && blockState.canPlaceAt(world, blockPos3)) {
                    blockPos = blockPos3;
                }
                blockPos3 = blockPos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            }
            if (world.isAir(blockPos3) && blockState.canPlaceAt(world, blockPos3)) {
                world.setBlockState(blockPos3, blockState, 2);
            }
        }
    }

    @Override
    protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return blockState.isFullOpaque(blockView, blockPos);
    }

    @Override
    public boolean canPlaceAt(BlockState blockState, CollisionView collisionView, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.down();
        BlockState blockState2 = collisionView.getBlockState(blockPos2);
        Block block = blockState2.getBlock();
        if (block == Blocks.MYCELIUM || block == Blocks.PODZOL) {
            return true;
        }
        return collisionView.getLightLevel(blockPos, 0) < 13 && this.canPlantOnTop(blockState2, collisionView, blockPos2);
    }

    public boolean trySpawningBigMushroom(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random) {
        iWorld.removeBlock(blockPos, false);
        Feature<PlantedFeatureConfig> feature = null;
        if (this == Blocks.BROWN_MUSHROOM) {
            feature = Feature.HUGE_BROWN_MUSHROOM;
        } else if (this == Blocks.RED_MUSHROOM) {
            feature = Feature.HUGE_RED_MUSHROOM;
        }
        if (feature != null && feature.generate(iWorld, iWorld.getChunkManager().getChunkGenerator(), random, blockPos, new PlantedFeatureConfig(true))) {
            return true;
        }
        iWorld.setBlockState(blockPos, blockState, 3);
        return false;
    }

    @Override
    public boolean isFertilizable(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
        return true;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos blockPos, BlockState blockState) {
        return (double)random.nextFloat() < 0.4;
    }

    @Override
    public void grow(World world, Random random, BlockPos blockPos, BlockState blockState) {
        this.trySpawningBigMushroom(world, blockPos, blockState, random);
    }

    @Override
    public boolean shouldPostProcess(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return true;
    }
}

