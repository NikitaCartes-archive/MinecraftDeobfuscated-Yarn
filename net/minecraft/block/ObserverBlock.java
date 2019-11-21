/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ObserverBlock
extends FacingBlock {
    public static final BooleanProperty POWERED = Properties.POWERED;

    public ObserverBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.SOUTH)).with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
        return (BlockState)blockState.with(FACING, blockRotation.rotate(blockState.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
        return blockState.rotate(blockMirror.getRotation(blockState.get(FACING)));
    }

    @Override
    public void scheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        if (blockState.get(POWERED).booleanValue()) {
            serverWorld.setBlockState(blockPos, (BlockState)blockState.with(POWERED, false), 2);
        } else {
            serverWorld.setBlockState(blockPos, (BlockState)blockState.with(POWERED, true), 2);
            serverWorld.getBlockTickScheduler().schedule(blockPos, this, 2);
        }
        this.updateNeighbors(serverWorld, blockPos, blockState);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        if (blockState.get(FACING) == direction && !blockState.get(POWERED).booleanValue()) {
            this.scheduleTick(iWorld, blockPos);
        }
        return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
    }

    private void scheduleTick(IWorld iWorld, BlockPos blockPos) {
        if (!iWorld.isClient() && !iWorld.getBlockTickScheduler().isScheduled(blockPos, this)) {
            iWorld.getBlockTickScheduler().schedule(blockPos, this, 2);
        }
    }

    protected void updateNeighbors(World world, BlockPos blockPos, BlockState blockState) {
        Direction direction = blockState.get(FACING);
        BlockPos blockPos2 = blockPos.offset(direction.getOpposite());
        world.updateNeighbor(blockPos2, this, blockPos);
        world.updateNeighborsExcept(blockPos2, this, direction);
    }

    @Override
    public boolean emitsRedstonePower(BlockState blockState) {
        return true;
    }

    @Override
    public int getStrongRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
        return blockState.getWeakRedstonePower(blockView, blockPos, direction);
    }

    @Override
    public int getWeakRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
        if (blockState.get(POWERED).booleanValue() && blockState.get(FACING) == direction) {
            return 15;
        }
        return 0;
    }

    @Override
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState.getBlock() == blockState2.getBlock()) {
            return;
        }
        if (!world.isClient() && blockState.get(POWERED).booleanValue() && !world.getBlockTickScheduler().isScheduled(blockPos, this)) {
            BlockState blockState3 = (BlockState)blockState.with(POWERED, false);
            world.setBlockState(blockPos, blockState3, 18);
            this.updateNeighbors(world, blockPos, blockState3);
        }
    }

    @Override
    public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState.getBlock() == blockState2.getBlock()) {
            return;
        }
        if (!world.isClient && blockState.get(POWERED).booleanValue() && world.getBlockTickScheduler().isScheduled(blockPos, this)) {
            this.updateNeighbors(world, blockPos, (BlockState)blockState.with(POWERED, false));
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        return (BlockState)this.getDefaultState().with(FACING, itemPlacementContext.getPlayerLookDirection().getOpposite().getOpposite());
    }
}

