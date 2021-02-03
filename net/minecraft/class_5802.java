/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class class_5802
extends HorizontalFacingBlock
implements Waterloggable {
    private static final BooleanProperty field_28667 = Properties.WATERLOGGED;
    private static final VoxelShape field_28668 = Block.createCuboidShape(5.0, 0.0, 8.0, 11.0, 16.0, 14.0);
    private static final VoxelShape field_28669 = Block.createCuboidShape(5.0, 0.0, 2.0, 11.0, 16.0, 8.0);
    private static final VoxelShape field_28670 = Block.createCuboidShape(2.0, 0.0, 5.0, 8.0, 16.0, 11.0);
    private static final VoxelShape field_28671 = Block.createCuboidShape(8.0, 0.0, 5.0, 14.0, 16.0, 11.0);

    protected class_5802(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(field_28667, false)).with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
            case SOUTH: {
                return field_28669;
            }
            default: {
                return field_28668;
            }
            case WEST: {
                return field_28671;
            }
            case EAST: 
        }
        return field_28670;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(field_28667, FACING);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(field_28667).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        BlockState blockState2 = world.getBlockState(pos.up());
        Block block = blockState2.getBlock();
        return !(!blockState.isOf(this) && !blockState.isSideSolidFullSquare(world, blockPos, Direction.UP) || block != this && block != Blocks.BIG_DRIPLEAF);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (!state.canPlaceAt(world, pos)) {
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }
        if (state.get(field_28667).booleanValue()) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
        }
    }
}

