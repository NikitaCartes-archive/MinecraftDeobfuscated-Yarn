/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

public class PlantBlock
extends Block {
    protected PlantBlock(Block.Settings settings) {
        super(settings);
    }

    protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        Block block = blockState.getBlock();
        return block == Blocks.GRASS_BLOCK || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.FARMLAND;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        if (!blockState.canPlaceAt(iWorld, blockPos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
    }

    @Override
    public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.down();
        return this.canPlantOnTop(viewableWorld.getBlockState(blockPos2), viewableWorld, blockPos2);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isTranslucent(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return true;
    }
}

