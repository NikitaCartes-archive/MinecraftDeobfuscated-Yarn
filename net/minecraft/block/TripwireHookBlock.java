/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import com.google.common.base.MoreObjects;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TripwireBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class TripwireHookBlock
extends Block {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty ATTACHED = Properties.ATTACHED;
    protected static final int field_31268 = 1;
    protected static final int field_31269 = 42;
    private static final int SCHEDULED_TICK_DELAY = 10;
    protected static final int field_31270 = 3;
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(5.0, 0.0, 10.0, 11.0, 10.0, 16.0);
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(5.0, 0.0, 0.0, 11.0, 10.0, 6.0);
    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(10.0, 0.0, 5.0, 16.0, 10.0, 11.0);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 5.0, 6.0, 10.0, 11.0);

    public TripwireHookBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(POWERED, false)).with(ATTACHED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
            default: {
                return WEST_SHAPE;
            }
            case WEST: {
                return EAST_SHAPE;
            }
            case SOUTH: {
                return NORTH_SHAPE;
            }
            case NORTH: 
        }
        return SOUTH_SHAPE;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction direction = state.get(FACING);
        BlockPos blockPos = pos.offset(direction.getOpposite());
        BlockState blockState = world.getBlockState(blockPos);
        return direction.getAxis().isHorizontal() && blockState.isSideSolidFullSquare(world, blockPos, direction);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction.getOpposite() == state.get(FACING) && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction[] directions;
        BlockState blockState = (BlockState)((BlockState)this.getDefaultState().with(POWERED, false)).with(ATTACHED, false);
        World worldView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        for (Direction direction : directions = ctx.getPlacementDirections()) {
            Direction direction2;
            if (!direction.getAxis().isHorizontal() || !(blockState = (BlockState)blockState.with(FACING, direction2 = direction.getOpposite())).canPlaceAt(worldView, blockPos)) continue;
            return blockState;
        }
        return null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        this.update(world, pos, state, false, false, -1, null);
    }

    public void update(World world, BlockPos pos, BlockState state, boolean beingRemoved, boolean bl, int i, @Nullable BlockState blockState) {
        BlockPos blockPos;
        Direction direction = state.get(FACING);
        boolean bl2 = state.get(ATTACHED);
        boolean bl3 = state.get(POWERED);
        boolean bl4 = !beingRemoved;
        boolean bl5 = false;
        int j = 0;
        BlockState[] blockStates = new BlockState[42];
        for (int k = 1; k < 42; ++k) {
            blockPos = pos.offset(direction, k);
            BlockState blockState2 = world.getBlockState(blockPos);
            if (blockState2.isOf(Blocks.TRIPWIRE_HOOK)) {
                if (blockState2.get(FACING) != direction.getOpposite()) break;
                j = k;
                break;
            }
            if (blockState2.isOf(Blocks.TRIPWIRE) || k == i) {
                if (k == i) {
                    blockState2 = MoreObjects.firstNonNull(blockState, blockState2);
                }
                boolean bl6 = blockState2.get(TripwireBlock.DISARMED) == false;
                boolean bl7 = blockState2.get(TripwireBlock.POWERED);
                bl5 |= bl6 && bl7;
                blockStates[k] = blockState2;
                if (k != i) continue;
                world.createAndScheduleBlockTick(pos, this, 10);
                bl4 &= bl6;
                continue;
            }
            blockStates[k] = null;
            bl4 = false;
        }
        BlockState blockState3 = (BlockState)((BlockState)this.getDefaultState().with(ATTACHED, bl4)).with(POWERED, bl5 &= (bl4 &= j > 1));
        if (j > 0) {
            blockPos = pos.offset(direction, j);
            Direction direction2 = direction.getOpposite();
            world.setBlockState(blockPos, (BlockState)blockState3.with(FACING, direction2), Block.NOTIFY_ALL);
            this.updateNeighborsOnAxis(world, blockPos, direction2);
            this.playSound(world, blockPos, bl4, bl5, bl2, bl3);
        }
        this.playSound(world, pos, bl4, bl5, bl2, bl3);
        if (!beingRemoved) {
            world.setBlockState(pos, (BlockState)blockState3.with(FACING, direction), Block.NOTIFY_ALL);
            if (bl) {
                this.updateNeighborsOnAxis(world, pos, direction);
            }
        }
        if (bl2 != bl4) {
            for (int l = 1; l < j; ++l) {
                BlockPos blockPos2 = pos.offset(direction, l);
                BlockState blockState4 = blockStates[l];
                if (blockState4 == null) continue;
                world.setBlockState(blockPos2, (BlockState)blockState4.with(ATTACHED, bl4), Block.NOTIFY_ALL);
                if (world.getBlockState(blockPos2).isAir()) continue;
            }
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.update(world, pos, state, false, true, -1, null);
    }

    private void playSound(World world, BlockPos pos, boolean attached, boolean on, boolean detached, boolean off) {
        if (on && !off) {
            world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 0.4f, 0.6f);
            world.emitGameEvent(null, GameEvent.BLOCK_PRESS, pos);
        } else if (!on && off) {
            world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_OFF, SoundCategory.BLOCKS, 0.4f, 0.5f);
            world.emitGameEvent(null, GameEvent.BLOCK_UNPRESS, pos);
        } else if (attached && !detached) {
            world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_ATTACH, SoundCategory.BLOCKS, 0.4f, 0.7f);
            world.emitGameEvent(null, GameEvent.BLOCK_ATTACH, pos);
        } else if (!attached && detached) {
            world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_DETACH, SoundCategory.BLOCKS, 0.4f, 1.2f / (world.random.nextFloat() * 0.2f + 0.9f));
            world.emitGameEvent(null, GameEvent.BLOCK_DETACH, pos);
        }
    }

    private void updateNeighborsOnAxis(World world, BlockPos pos, Direction direction) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.offset(direction.getOpposite()), this);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved || state.isOf(newState.getBlock())) {
            return;
        }
        boolean bl = state.get(ATTACHED);
        boolean bl2 = state.get(POWERED);
        if (bl || bl2) {
            this.update(world, pos, state, true, false, -1, null);
        }
        if (bl2) {
            world.updateNeighborsAlways(pos, this);
            world.updateNeighborsAlways(pos.offset(state.get(FACING).getOpposite()), this);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) != false ? 15 : 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (!state.get(POWERED).booleanValue()) {
            return 0;
        }
        if (state.get(FACING) == direction) {
            return 15;
        }
        return 0;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, ATTACHED);
    }
}

