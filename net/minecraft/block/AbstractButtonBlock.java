/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractButtonBlock
extends WallMountedBlock {
    public static final BooleanProperty POWERED = Properties.POWERED;
    protected static final VoxelShape CEILING_X_SHAPE = Block.createCuboidShape(6.0, 14.0, 5.0, 10.0, 16.0, 11.0);
    protected static final VoxelShape CEILING_Z_SHAPE = Block.createCuboidShape(5.0, 14.0, 6.0, 11.0, 16.0, 10.0);
    protected static final VoxelShape FLOOR_X_SHAPE = Block.createCuboidShape(6.0, 0.0, 5.0, 10.0, 2.0, 11.0);
    protected static final VoxelShape FLOOR_Z_SHAPE = Block.createCuboidShape(5.0, 0.0, 6.0, 11.0, 2.0, 10.0);
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(5.0, 6.0, 14.0, 11.0, 10.0, 16.0);
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(5.0, 6.0, 0.0, 11.0, 10.0, 2.0);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(14.0, 6.0, 5.0, 16.0, 10.0, 11.0);
    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0, 6.0, 5.0, 2.0, 10.0, 11.0);
    protected static final VoxelShape CEILING_X_PRESSED_SHAPE = Block.createCuboidShape(6.0, 15.0, 5.0, 10.0, 16.0, 11.0);
    protected static final VoxelShape CEILING_Z_PRESSED_SHAPE = Block.createCuboidShape(5.0, 15.0, 6.0, 11.0, 16.0, 10.0);
    protected static final VoxelShape FLOOR_X_PRESSED_SHAPE = Block.createCuboidShape(6.0, 0.0, 5.0, 10.0, 1.0, 11.0);
    protected static final VoxelShape FLOOR_Z_PRESSED_SHAPE = Block.createCuboidShape(5.0, 0.0, 6.0, 11.0, 1.0, 10.0);
    protected static final VoxelShape NORTH_PRESSED_SHAPE = Block.createCuboidShape(5.0, 6.0, 15.0, 11.0, 10.0, 16.0);
    protected static final VoxelShape SOUTH_PRESSED_SHAPE = Block.createCuboidShape(5.0, 6.0, 0.0, 11.0, 10.0, 1.0);
    protected static final VoxelShape WEST_PRESSED_SHAPE = Block.createCuboidShape(15.0, 6.0, 5.0, 16.0, 10.0, 11.0);
    protected static final VoxelShape EAST_PRESSED_SHAPE = Block.createCuboidShape(0.0, 6.0, 5.0, 1.0, 10.0, 11.0);
    private final boolean wooden;

    protected AbstractButtonBlock(boolean wooden, AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(POWERED, false)).with(FACE, WallMountLocation.WALL));
        this.wooden = wooden;
    }

    private int getPressTicks() {
        return this.wooden ? 30 : 20;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        boolean bl = state.get(POWERED);
        switch ((WallMountLocation)state.get(FACE)) {
            case FLOOR: {
                if (direction.getAxis() == Direction.Axis.X) {
                    return bl ? FLOOR_X_PRESSED_SHAPE : FLOOR_X_SHAPE;
                }
                return bl ? FLOOR_Z_PRESSED_SHAPE : FLOOR_Z_SHAPE;
            }
            case WALL: {
                switch (direction) {
                    case EAST: {
                        return bl ? EAST_PRESSED_SHAPE : EAST_SHAPE;
                    }
                    case WEST: {
                        return bl ? WEST_PRESSED_SHAPE : WEST_SHAPE;
                    }
                    case SOUTH: {
                        return bl ? SOUTH_PRESSED_SHAPE : SOUTH_SHAPE;
                    }
                }
                return bl ? NORTH_PRESSED_SHAPE : NORTH_SHAPE;
            }
        }
        if (direction.getAxis() == Direction.Axis.X) {
            return bl ? CEILING_X_PRESSED_SHAPE : CEILING_X_SHAPE;
        }
        return bl ? CEILING_Z_PRESSED_SHAPE : CEILING_Z_SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(POWERED).booleanValue()) {
            return ActionResult.CONSUME;
        }
        this.powerOn(state, world, pos);
        this.playClickSound(player, world, pos, true);
        world.emitGameEvent((Entity)player, GameEvent.BLOCK_PRESS, pos);
        return ActionResult.success(world.isClient);
    }

    public void powerOn(BlockState state, World world, BlockPos pos) {
        world.setBlockState(pos, (BlockState)state.with(POWERED, true), 3);
        this.updateNeighbors(state, world, pos);
        world.getBlockTickScheduler().schedule(pos, this, this.getPressTicks());
    }

    protected void playClickSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos, boolean powered) {
        world.playSound(powered ? player : null, pos, this.getClickSound(powered), SoundCategory.BLOCKS, 0.3f, powered ? 0.6f : 0.5f);
    }

    protected abstract SoundEvent getClickSound(boolean var1);

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved || state.isOf(newState.getBlock())) {
            return;
        }
        if (state.get(POWERED).booleanValue()) {
            this.updateNeighbors(state, world, pos);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) != false ? 15 : 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (state.get(POWERED).booleanValue() && AbstractButtonBlock.getDirection(state) == direction) {
            return 15;
        }
        return 0;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.get(POWERED).booleanValue()) {
            return;
        }
        if (this.wooden) {
            this.tryPowerWithProjectiles(state, world, pos);
        } else {
            world.setBlockState(pos, (BlockState)state.with(POWERED, false), 3);
            this.updateNeighbors(state, world, pos);
            this.playClickSound(null, world, pos, false);
            world.emitGameEvent(GameEvent.BLOCK_UNPRESS, pos);
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient || !this.wooden || state.get(POWERED).booleanValue()) {
            return;
        }
        this.tryPowerWithProjectiles(state, world, pos);
    }

    private void tryPowerWithProjectiles(BlockState state, World world, BlockPos pos) {
        boolean bl2;
        List<PersistentProjectileEntity> list = world.getNonSpectatingEntities(PersistentProjectileEntity.class, state.getOutlineShape(world, pos).getBoundingBox().offset(pos));
        boolean bl = !list.isEmpty();
        if (bl != (bl2 = state.get(POWERED).booleanValue())) {
            world.setBlockState(pos, (BlockState)state.with(POWERED, bl), 3);
            this.updateNeighbors(state, world, pos);
            this.playClickSound(null, world, pos, bl);
            world.emitGameEvent((Entity)list.stream().findFirst().orElse(null), bl ? GameEvent.BLOCK_PRESS : GameEvent.BLOCK_UNPRESS, pos);
        }
        if (bl) {
            world.getBlockTickScheduler().schedule(new BlockPos(pos), this, this.getPressTicks());
        }
    }

    private void updateNeighbors(BlockState state, World world, BlockPos pos) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.offset(AbstractButtonBlock.getDirection(state).getOpposite()), this);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, FACE);
    }
}

