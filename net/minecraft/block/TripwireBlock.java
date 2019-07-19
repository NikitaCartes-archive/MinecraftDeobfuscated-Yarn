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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
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

    public TripwireBlock(TripwireHookBlock tripwireHookBlock, Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(POWERED, false)).with(ATTACHED, false)).with(DISARMED, false)).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false));
        this.hookBlock = tripwireHookBlock;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        return blockState.get(ATTACHED) != false ? ATTACHED_SHAPE : DETACHED_SHAPE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        World blockView = itemPlacementContext.getWorld();
        BlockPos blockPos = itemPlacementContext.getBlockPos();
        return (BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(NORTH, this.shouldConnectTo(blockView.getBlockState(blockPos.north()), Direction.NORTH))).with(EAST, this.shouldConnectTo(blockView.getBlockState(blockPos.east()), Direction.EAST))).with(SOUTH, this.shouldConnectTo(blockView.getBlockState(blockPos.south()), Direction.SOUTH))).with(WEST, this.shouldConnectTo(blockView.getBlockState(blockPos.west()), Direction.WEST));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        if (direction.getAxis().isHorizontal()) {
            return (BlockState)blockState.with(FACING_PROPERTIES.get(direction), this.shouldConnectTo(blockState2, direction));
        }
        return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
    }

    @Override
    public RenderLayer getRenderLayer() {
        return RenderLayer.TRANSLUCENT;
    }

    @Override
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState2.getBlock() == blockState.getBlock()) {
            return;
        }
        this.update(world, blockPos, blockState);
    }

    @Override
    public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (bl || blockState.getBlock() == blockState2.getBlock()) {
            return;
        }
        this.update(world, blockPos, (BlockState)blockState.with(POWERED, true));
    }

    @Override
    public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
        if (!world.isClient && !playerEntity.getMainHandStack().isEmpty() && playerEntity.getMainHandStack().getItem() == Items.SHEARS) {
            world.setBlockState(blockPos, (BlockState)blockState.with(DISARMED, true), 4);
        }
        super.onBreak(world, blockPos, blockState, playerEntity);
    }

    private void update(World world, BlockPos blockPos, BlockState blockState) {
        block0: for (Direction direction : new Direction[]{Direction.SOUTH, Direction.WEST}) {
            for (int i = 1; i < 42; ++i) {
                BlockPos blockPos2 = blockPos.offset(direction, i);
                BlockState blockState2 = world.getBlockState(blockPos2);
                if (blockState2.getBlock() == this.hookBlock) {
                    if (blockState2.get(TripwireHookBlock.FACING) != direction.getOpposite()) continue block0;
                    this.hookBlock.update(world, blockPos2, blockState2, false, true, i, blockState);
                    continue block0;
                }
                if (blockState2.getBlock() != this) continue block0;
            }
        }
    }

    @Override
    public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
        if (world.isClient) {
            return;
        }
        if (blockState.get(POWERED).booleanValue()) {
            return;
        }
        this.updatePowered(world, blockPos);
    }

    @Override
    public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        if (world.isClient) {
            return;
        }
        if (!world.getBlockState(blockPos).get(POWERED).booleanValue()) {
            return;
        }
        this.updatePowered(world, blockPos);
    }

    private void updatePowered(World world, BlockPos blockPos) {
        BlockState blockState = world.getBlockState(blockPos);
        boolean bl = blockState.get(POWERED);
        boolean bl2 = false;
        List<Entity> list = world.getEntities(null, blockState.getOutlineShape(world, blockPos).getBoundingBox().offset(blockPos));
        if (!list.isEmpty()) {
            for (Entity entity : list) {
                if (entity.canAvoidTraps()) continue;
                bl2 = true;
                break;
            }
        }
        if (bl2 != bl) {
            blockState = (BlockState)blockState.with(POWERED, bl2);
            world.setBlockState(blockPos, blockState, 3);
            this.update(world, blockPos, blockState);
        }
        if (bl2) {
            world.getBlockTickScheduler().schedule(new BlockPos(blockPos), this, this.getTickRate(world));
        }
    }

    public boolean shouldConnectTo(BlockState blockState, Direction direction) {
        Block block = blockState.getBlock();
        if (block == this.hookBlock) {
            return blockState.get(TripwireHookBlock.FACING) == direction.getOpposite();
        }
        return block == this;
    }

    @Override
    public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
        switch (blockRotation) {
            case CLOCKWISE_180: {
                return (BlockState)((BlockState)((BlockState)((BlockState)blockState.with(NORTH, blockState.get(SOUTH))).with(EAST, blockState.get(WEST))).with(SOUTH, blockState.get(NORTH))).with(WEST, blockState.get(EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)blockState.with(NORTH, blockState.get(EAST))).with(EAST, blockState.get(SOUTH))).with(SOUTH, blockState.get(WEST))).with(WEST, blockState.get(NORTH));
            }
            case CLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)blockState.with(NORTH, blockState.get(WEST))).with(EAST, blockState.get(NORTH))).with(SOUTH, blockState.get(EAST))).with(WEST, blockState.get(SOUTH));
            }
        }
        return blockState;
    }

    @Override
    public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
        switch (blockMirror) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)blockState.with(NORTH, blockState.get(SOUTH))).with(SOUTH, blockState.get(NORTH));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)blockState.with(EAST, blockState.get(WEST))).with(WEST, blockState.get(EAST));
            }
        }
        return super.mirror(blockState, blockMirror);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
    }
}

