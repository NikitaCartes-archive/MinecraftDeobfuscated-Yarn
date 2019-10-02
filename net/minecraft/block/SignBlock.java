/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldView;

public class SignBlock
extends AbstractSignBlock {
    public static final IntProperty ROTATION = Properties.ROTATION;

    public SignBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(ROTATION, 0)).with(WATERLOGGED, false));
    }

    @Override
    public boolean canPlaceAt(BlockState blockState, WorldView worldView, BlockPos blockPos) {
        return worldView.getBlockState(blockPos.method_10074()).getMaterial().isSolid();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getBlockPos());
        return (BlockState)((BlockState)this.getDefaultState().with(ROTATION, MathHelper.floor((double)((180.0f + itemPlacementContext.getPlayerYaw()) * 16.0f / 360.0f) + 0.5) & 0xF)).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        if (direction == Direction.DOWN && !this.canPlaceAt(blockState, iWorld, blockPos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
    }

    @Override
    public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
        return (BlockState)blockState.with(ROTATION, blockRotation.rotate(blockState.get(ROTATION), 16));
    }

    @Override
    public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
        return (BlockState)blockState.with(ROTATION, blockMirror.mirror(blockState.get(ROTATION), 16));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, WATERLOGGED);
    }
}

