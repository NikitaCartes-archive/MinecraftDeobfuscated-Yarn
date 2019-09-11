/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.class_4538;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
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

    protected AbstractButtonBlock(boolean bl, Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(FACING, Direction.NORTH)).with(POWERED, false)).with(FACE, WallMountLocation.WALL));
        this.wooden = bl;
    }

    @Override
    public int getTickRate(class_4538 arg) {
        return this.wooden ? 30 : 20;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        Direction direction = blockState.get(FACING);
        boolean bl = blockState.get(POWERED);
        switch ((WallMountLocation)blockState.get(FACE)) {
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
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (blockState.get(POWERED).booleanValue()) {
            return true;
        }
        this.method_21845(blockState, world, blockPos);
        this.playClickSound(playerEntity, world, blockPos, true);
        return true;
    }

    public void method_21845(BlockState blockState, World world, BlockPos blockPos) {
        world.setBlockState(blockPos, (BlockState)blockState.with(POWERED, true), 3);
        this.updateNeighbors(blockState, world, blockPos);
        world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world));
    }

    protected void playClickSound(@Nullable PlayerEntity playerEntity, IWorld iWorld, BlockPos blockPos, boolean bl) {
        iWorld.playSound(bl ? playerEntity : null, blockPos, this.getClickSound(bl), SoundCategory.BLOCKS, 0.3f, bl ? 0.6f : 0.5f);
    }

    protected abstract SoundEvent getClickSound(boolean var1);

    @Override
    public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (bl || blockState.getBlock() == blockState2.getBlock()) {
            return;
        }
        if (blockState.get(POWERED).booleanValue()) {
            this.updateNeighbors(blockState, world, blockPos);
        }
        super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
    }

    @Override
    public int getWeakRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
        return blockState.get(POWERED) != false ? 15 : 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
        if (blockState.get(POWERED).booleanValue() && AbstractButtonBlock.getDirection(blockState) == direction) {
            return 15;
        }
        return 0;
    }

    @Override
    public boolean emitsRedstonePower(BlockState blockState) {
        return true;
    }

    @Override
    public void onScheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        if (!blockState.get(POWERED).booleanValue()) {
            return;
        }
        if (this.wooden) {
            this.tryPowerWithProjectiles(blockState, serverWorld, blockPos);
        } else {
            serverWorld.setBlockState(blockPos, (BlockState)blockState.with(POWERED, false), 3);
            this.updateNeighbors(blockState, serverWorld, blockPos);
            this.playClickSound(null, serverWorld, blockPos, false);
        }
    }

    @Override
    public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
        if (world.isClient || !this.wooden || blockState.get(POWERED).booleanValue()) {
            return;
        }
        this.tryPowerWithProjectiles(blockState, world, blockPos);
    }

    private void tryPowerWithProjectiles(BlockState blockState, World world, BlockPos blockPos) {
        boolean bl2;
        List<ProjectileEntity> list = world.getNonSpectatingEntities(ProjectileEntity.class, blockState.getOutlineShape(world, blockPos).getBoundingBox().offset(blockPos));
        boolean bl = !list.isEmpty();
        if (bl != (bl2 = blockState.get(POWERED).booleanValue())) {
            world.setBlockState(blockPos, (BlockState)blockState.with(POWERED, bl), 3);
            this.updateNeighbors(blockState, world, blockPos);
            this.playClickSound(null, world, blockPos, bl);
        }
        if (bl) {
            world.getBlockTickScheduler().schedule(new BlockPos(blockPos), this, this.getTickRate(world));
        }
    }

    private void updateNeighbors(BlockState blockState, World world, BlockPos blockPos) {
        world.updateNeighborsAlways(blockPos, this);
        world.updateNeighborsAlways(blockPos.offset(AbstractButtonBlock.getDirection(blockState).getOpposite()), this);
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, FACE);
    }
}

