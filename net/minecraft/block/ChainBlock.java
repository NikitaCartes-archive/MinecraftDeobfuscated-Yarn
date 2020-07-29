/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class ChainBlock
extends PillarBlock
implements Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    protected static final VoxelShape Y_SHAPE = Block.createCuboidShape(6.5, 0.0, 6.5, 9.5, 16.0, 9.5);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.5, 6.5, 0.0, 9.5, 9.5, 16.0);
    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0, 6.5, 6.5, 16.0, 9.5, 9.5);

    public ChainBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(WATERLOGGED, false)).with(AXIS, Direction.Axis.Y));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch ((Direction.Axis)state.get(AXIS)) {
            default: {
                return X_SHAPE;
            }
            case Z: {
                return Z_SHAPE;
            }
            case Y: 
        }
        return Y_SHAPE;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = fluidState.getFluid() == Fluids.WATER;
        return (BlockState)super.getPlacementState(ctx).with(WATERLOGGED, bl);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (state.get(WATERLOGGED).booleanValue()) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED).add(AXIS);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }
}

