/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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

public class LeverBlock
extends WallMountedBlock {
    public static final BooleanProperty POWERED = Properties.POWERED;
    protected static final int field_31184 = 6;
    protected static final int field_31185 = 6;
    protected static final int field_31186 = 8;
    protected static final VoxelShape NORTH_WALL_SHAPE = Block.createCuboidShape(5.0, 4.0, 10.0, 11.0, 12.0, 16.0);
    protected static final VoxelShape SOUTH_WALL_SHAPE = Block.createCuboidShape(5.0, 4.0, 0.0, 11.0, 12.0, 6.0);
    protected static final VoxelShape WEST_WALL_SHAPE = Block.createCuboidShape(10.0, 4.0, 5.0, 16.0, 12.0, 11.0);
    protected static final VoxelShape EAST_WALL_SHAPE = Block.createCuboidShape(0.0, 4.0, 5.0, 6.0, 12.0, 11.0);
    protected static final VoxelShape FLOOR_Z_AXIS_SHAPE = Block.createCuboidShape(5.0, 0.0, 4.0, 11.0, 6.0, 12.0);
    protected static final VoxelShape FLOOR_X_AXIS_SHAPE = Block.createCuboidShape(4.0, 0.0, 5.0, 12.0, 6.0, 11.0);
    protected static final VoxelShape CEILING_Z_AXIS_SHAPE = Block.createCuboidShape(5.0, 10.0, 4.0, 11.0, 16.0, 12.0);
    protected static final VoxelShape CEILING_X_AXIS_SHAPE = Block.createCuboidShape(4.0, 10.0, 5.0, 12.0, 16.0, 11.0);

    protected LeverBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(POWERED, false)).with(FACE, WallMountLocation.WALL));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch ((WallMountLocation)state.get(FACE)) {
            case FLOOR: {
                switch (state.get(FACING).getAxis()) {
                    case X: {
                        return FLOOR_X_AXIS_SHAPE;
                    }
                }
                return FLOOR_Z_AXIS_SHAPE;
            }
            case WALL: {
                switch (state.get(FACING)) {
                    case EAST: {
                        return EAST_WALL_SHAPE;
                    }
                    case WEST: {
                        return WEST_WALL_SHAPE;
                    }
                    case SOUTH: {
                        return SOUTH_WALL_SHAPE;
                    }
                }
                return NORTH_WALL_SHAPE;
            }
        }
        switch (state.get(FACING).getAxis()) {
            case X: {
                return CEILING_X_AXIS_SHAPE;
            }
        }
        return CEILING_Z_AXIS_SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            BlockState blockState = (BlockState)state.cycle(POWERED);
            if (blockState.get(POWERED).booleanValue()) {
                LeverBlock.spawnParticles(blockState, world, pos, 1.0f);
            }
            return ActionResult.SUCCESS;
        }
        BlockState blockState = this.togglePower(state, world, pos);
        float f = blockState.get(POWERED) != false ? 0.6f : 0.5f;
        world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3f, f);
        world.emitGameEvent((Entity)player, blockState.get(POWERED) != false ? GameEvent.BLOCK_SWITCH : GameEvent.BLOCK_UNSWITCH, pos);
        return ActionResult.CONSUME;
    }

    public BlockState togglePower(BlockState state, World world, BlockPos pos) {
        state = (BlockState)state.cycle(POWERED);
        world.setBlockState(pos, state, Block.NOTIFY_ALL);
        this.updateNeighbors(state, world, pos);
        return state;
    }

    private static void spawnParticles(BlockState state, WorldAccess world, BlockPos pos, float alpha) {
        Direction direction = state.get(FACING).getOpposite();
        Direction direction2 = LeverBlock.getDirection(state).getOpposite();
        double d = (double)pos.getX() + 0.5 + 0.1 * (double)direction.getOffsetX() + 0.2 * (double)direction2.getOffsetX();
        double e = (double)pos.getY() + 0.5 + 0.1 * (double)direction.getOffsetY() + 0.2 * (double)direction2.getOffsetY();
        double f = (double)pos.getZ() + 0.5 + 0.1 * (double)direction.getOffsetZ() + 0.2 * (double)direction2.getOffsetZ();
        world.addParticle(new DustParticleEffect(DustParticleEffect.RED, alpha), d, e, f, 0.0, 0.0, 0.0);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(POWERED).booleanValue() && random.nextFloat() < 0.25f) {
            LeverBlock.spawnParticles(state, world, pos, 0.5f);
        }
    }

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
        if (state.get(POWERED).booleanValue() && LeverBlock.getDirection(state) == direction) {
            return 15;
        }
        return 0;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    private void updateNeighbors(BlockState state, World world, BlockPos pos) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.offset(LeverBlock.getDirection(state).getOpposite()), this);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACE, FACING, POWERED);
    }
}

