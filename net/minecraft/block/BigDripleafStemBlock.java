/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BigDripleafBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.PortalUtil;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class BigDripleafStemBlock
extends HorizontalFacingBlock
implements Fertilizable,
Waterloggable {
    private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final int field_31021 = 6;
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(5.0, 0.0, 9.0, 11.0, 16.0, 15.0);
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(5.0, 0.0, 1.0, 11.0, 16.0, 7.0);
    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(1.0, 0.0, 5.0, 7.0, 16.0, 11.0);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(9.0, 0.0, 5.0, 15.0, 16.0, 11.0);

    protected BigDripleafStemBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(WATERLOGGED, false)).with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
            case SOUTH: {
                return SOUTH_SHAPE;
            }
            default: {
                return NORTH_SHAPE;
            }
            case WEST: {
                return WEST_SHAPE;
            }
            case EAST: 
        }
        return EAST_SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, FACING);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.isOf(this) || blockState.isSideSolidFullSquare(world, blockPos, Direction.UP);
    }

    protected static boolean placeStemAt(WorldAccess world, BlockPos pos, FluidState fluidState, Direction direction) {
        BlockState blockState = (BlockState)((BlockState)Blocks.BIG_DRIPLEAF_STEM.getDefaultState().with(WATERLOGGED, fluidState.isEqualAndStill(Fluids.WATER))).with(FACING, direction);
        return world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }
        if (state.get(WATERLOGGED).booleanValue()) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
        }
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        Optional<BlockPos> optional = PortalUtil.method_34851(world, pos, state.getBlock(), Direction.UP, Blocks.BIG_DRIPLEAF);
        if (!optional.isPresent()) {
            return false;
        }
        BlockPos blockPos = optional.get().up();
        BlockState blockState = world.getBlockState(blockPos);
        return BigDripleafBlock.canGrowInto(world, blockPos, blockState);
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        Optional<BlockPos> optional = PortalUtil.method_34851(world, pos, state.getBlock(), Direction.UP, Blocks.BIG_DRIPLEAF);
        if (!optional.isPresent()) {
            return;
        }
        BlockPos blockPos = optional.get();
        BlockPos blockPos2 = blockPos.up();
        FluidState fluidState = world.getFluidState(blockPos2);
        Direction direction = state.get(FACING);
        BigDripleafStemBlock.placeStemAt(world, blockPos, state.getFluidState(), direction);
        BigDripleafBlock.placeDripleafAt(world, blockPos2, fluidState, direction);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(Blocks.BIG_DRIPLEAF);
    }
}

