/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class AbstractPressurePlateBlock
extends Block {
    protected static final VoxelShape PRESSED_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 0.5, 15.0);
    protected static final VoxelShape DEFAULT_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 1.0, 15.0);
    protected static final Box BOX = new Box(0.125, 0.0, 0.125, 0.875, 0.25, 0.875);

    protected AbstractPressurePlateBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
        return this.getRedstoneOutput(state) > 0 ? PRESSED_SHAPE : DEFAULT_SHAPE;
    }

    @Override
    public int getTickRate(WorldView worldView) {
        return 20;
    }

    @Override
    public boolean canMobSpawnInside() {
        return true;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if (facing == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        return AbstractPressurePlateBlock.topCoversMediumSquare(world, blockPos) || AbstractPressurePlateBlock.sideCoversSmallSquare(world, blockPos, Direction.UP);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i = this.getRedstoneOutput(state);
        if (i > 0) {
            this.updatePlateState(world, pos, state, i);
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient) {
            return;
        }
        int i = this.getRedstoneOutput(state);
        if (i == 0) {
            this.updatePlateState(world, pos, state, i);
        }
    }

    protected void updatePlateState(World world, BlockPos pos, BlockState blockState, int rsOut) {
        boolean bl2;
        int i = this.getRedstoneOutput(world, pos);
        boolean bl = rsOut > 0;
        boolean bl3 = bl2 = i > 0;
        if (rsOut != i) {
            BlockState blockState2 = this.setRedstoneOutput(blockState, i);
            world.setBlockState(pos, blockState2, 2);
            this.updateNeighbors(world, pos);
            world.checkBlockRerender(pos, blockState, blockState2);
        }
        if (!bl2 && bl) {
            this.playDepressSound(world, pos);
        } else if (bl2 && !bl) {
            this.playPressSound(world, pos);
        }
        if (bl2) {
            world.getBlockTickScheduler().schedule(new BlockPos(pos), this, this.getTickRate(world));
        }
    }

    protected abstract void playPressSound(IWorld var1, BlockPos var2);

    protected abstract void playDepressSound(IWorld var1, BlockPos var2);

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved || state.getBlock() == newState.getBlock()) {
            return;
        }
        if (this.getRedstoneOutput(state) > 0) {
            this.updateNeighbors(world, pos);
        }
        super.onBlockRemoved(state, world, pos, newState, moved);
    }

    protected void updateNeighbors(World world, BlockPos pos) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.down(), this);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
        return this.getRedstoneOutput(state);
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
        if (facing == Direction.UP) {
            return this.getRedstoneOutput(state);
        }
        return 0;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

    protected abstract int getRedstoneOutput(World var1, BlockPos var2);

    protected abstract int getRedstoneOutput(BlockState var1);

    protected abstract BlockState setRedstoneOutput(BlockState var1, int var2);
}

