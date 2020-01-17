/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.block.HorizontalConnectingBlock;
import net.minecraft.block.TripwireHookBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class TripwireBlock
extends Block {
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty ATTACHED = Properties.ATTACHED;
    public static final BooleanProperty DISARMED = Properties.DISARMED;
    public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
    public static final BooleanProperty EAST = ConnectingBlock.EAST;
    public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
    public static final BooleanProperty WEST = ConnectingBlock.WEST;
    private static final Map<Direction, BooleanProperty> FACING_PROPERTIES = HorizontalConnectingBlock.FACING_PROPERTIES;
    protected static final VoxelShape ATTACHED_SHAPE = Block.createCuboidShape(0.0, 1.0, 0.0, 16.0, 2.5, 16.0);
    protected static final VoxelShape DETACHED_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    private final TripwireHookBlock hookBlock;

    public TripwireBlock(TripwireHookBlock hookBlock, Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(POWERED, false)).with(ATTACHED, false)).with(DISARMED, false)).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false));
        this.hookBlock = hookBlock;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return state.get(ATTACHED) != false ? ATTACHED_SHAPE : DETACHED_SHAPE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World blockView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        return (BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(NORTH, this.shouldConnectTo(blockView.getBlockState(blockPos.north()), Direction.NORTH))).with(EAST, this.shouldConnectTo(blockView.getBlockState(blockPos.east()), Direction.EAST))).with(SOUTH, this.shouldConnectTo(blockView.getBlockState(blockPos.south()), Direction.SOUTH))).with(WEST, this.shouldConnectTo(blockView.getBlockState(blockPos.west()), Direction.WEST));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if (facing.getAxis().isHorizontal()) {
            return (BlockState)state.with(FACING_PROPERTIES.get(facing), this.shouldConnectTo(neighborState, facing));
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        if (oldState.getBlock() == state.getBlock()) {
            return;
        }
        this.update(world, pos, state);
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved || state.getBlock() == newState.getBlock()) {
            return;
        }
        this.update(world, pos, (BlockState)state.with(POWERED, true));
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && !player.getMainHandStack().isEmpty() && player.getMainHandStack().getItem() == Items.SHEARS) {
            world.setBlockState(pos, (BlockState)state.with(DISARMED, true), 4);
        }
        super.onBreak(world, pos, state, player);
    }

    private void update(World world, BlockPos pos, BlockState state) {
        block0: for (Direction direction : new Direction[]{Direction.SOUTH, Direction.WEST}) {
            for (int i = 1; i < 42; ++i) {
                BlockPos blockPos = pos.offset(direction, i);
                BlockState blockState = world.getBlockState(blockPos);
                if (blockState.getBlock() == this.hookBlock) {
                    if (blockState.get(TripwireHookBlock.FACING) != direction.getOpposite()) continue block0;
                    this.hookBlock.update(world, blockPos, blockState, false, true, i, state);
                    continue block0;
                }
                if (blockState.getBlock() != this) continue block0;
            }
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient) {
            return;
        }
        if (state.get(POWERED).booleanValue()) {
            return;
        }
        this.updatePowered(world, pos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!world.getBlockState(pos).get(POWERED).booleanValue()) {
            return;
        }
        this.updatePowered(world, pos);
    }

    private void updatePowered(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        boolean bl = blockState.get(POWERED);
        boolean bl2 = false;
        List<Entity> list = world.getEntities(null, blockState.getOutlineShape(world, pos).getBoundingBox().offset(pos));
        if (!list.isEmpty()) {
            for (Entity entity : list) {
                if (entity.canAvoidTraps()) continue;
                bl2 = true;
                break;
            }
        }
        if (bl2 != bl) {
            blockState = (BlockState)blockState.with(POWERED, bl2);
            world.setBlockState(pos, blockState, 3);
            this.update(world, pos, blockState);
        }
        if (bl2) {
            world.getBlockTickScheduler().schedule(new BlockPos(pos), this, this.getTickRate(world));
        }
    }

    public boolean shouldConnectTo(BlockState state, Direction facing) {
        Block block = state.getBlock();
        if (block == this.hookBlock) {
            return state.get(TripwireHookBlock.FACING) == facing.getOpposite();
        }
        return block == this;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with(NORTH, state.get(SOUTH))).with(EAST, state.get(WEST))).with(SOUTH, state.get(NORTH))).with(WEST, state.get(EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with(NORTH, state.get(EAST))).with(EAST, state.get(SOUTH))).with(SOUTH, state.get(WEST))).with(WEST, state.get(NORTH));
            }
            case CLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with(NORTH, state.get(WEST))).with(EAST, state.get(NORTH))).with(SOUTH, state.get(EAST))).with(WEST, state.get(SOUTH));
            }
        }
        return state;
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)state.with(NORTH, state.get(SOUTH))).with(SOUTH, state.get(NORTH));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)state.with(EAST, state.get(WEST))).with(WEST, state.get(EAST));
            }
        }
        return super.mirror(state, mirror);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
    }
}

