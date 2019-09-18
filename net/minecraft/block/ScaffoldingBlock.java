/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Waterloggable;
import net.minecraft.class_4538;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ScaffoldingBlock
extends Block
implements Waterloggable {
    private static final VoxelShape NORMAL_OUTLINE_SHAPE;
    private static final VoxelShape BOTTOM_OUTLINE_SHAPE;
    private static final VoxelShape COLLISION_SHAPE;
    private static final VoxelShape OUTLINE_SHAPE;
    public static final IntProperty DISTANCE;
    public static final BooleanProperty WATERLOGGED;
    public static final BooleanProperty BOTTOM;

    protected ScaffoldingBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(DISTANCE, 7)).with(WATERLOGGED, false)).with(BOTTOM, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DISTANCE, WATERLOGGED, BOTTOM);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        if (!entityContext.isHolding(blockState.getBlock().asItem())) {
            return blockState.get(BOTTOM) != false ? BOTTOM_OUTLINE_SHAPE : NORMAL_OUTLINE_SHAPE;
        }
        return VoxelShapes.fullCube();
    }

    @Override
    public VoxelShape getRayTraceShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return VoxelShapes.fullCube();
    }

    @Override
    public boolean canReplace(BlockState blockState, ItemPlacementContext itemPlacementContext) {
        return itemPlacementContext.getStack().getItem() == this.asItem();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        BlockPos blockPos = itemPlacementContext.getBlockPos();
        World world = itemPlacementContext.getWorld();
        int i = ScaffoldingBlock.calculateDistance(world, blockPos);
        return (BlockState)((BlockState)((BlockState)this.getDefaultState().with(WATERLOGGED, world.getFluidState(blockPos).getFluid() == Fluids.WATER)).with(DISTANCE, i)).with(BOTTOM, this.shouldBeBottom(world, blockPos, i));
    }

    @Override
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (!world.isClient) {
            world.getBlockTickScheduler().schedule(blockPos, this, 1);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        if (blockState.get(WATERLOGGED).booleanValue()) {
            iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
        }
        if (!iWorld.isClient()) {
            iWorld.getBlockTickScheduler().schedule(blockPos, this, 1);
        }
        return blockState;
    }

    @Override
    public void onScheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        int i = ScaffoldingBlock.calculateDistance(serverWorld, blockPos);
        BlockState blockState2 = (BlockState)((BlockState)blockState.with(DISTANCE, i)).with(BOTTOM, this.shouldBeBottom(serverWorld, blockPos, i));
        if (blockState2.get(DISTANCE) == 7) {
            if (blockState.get(DISTANCE) == 7) {
                serverWorld.spawnEntity(new FallingBlockEntity(serverWorld, (double)blockPos.getX() + 0.5, blockPos.getY(), (double)blockPos.getZ() + 0.5, (BlockState)blockState2.with(WATERLOGGED, false)));
            } else {
                serverWorld.breakBlock(blockPos, true);
            }
        } else if (blockState != blockState2) {
            serverWorld.setBlockState(blockPos, blockState2, 3);
        }
    }

    @Override
    public boolean canPlaceAt(BlockState blockState, class_4538 arg, BlockPos blockPos) {
        return ScaffoldingBlock.calculateDistance(arg, blockPos) < 7;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        if (!entityContext.isAbove(VoxelShapes.fullCube(), blockPos, true) || entityContext.isSneaking()) {
            if (blockState.get(DISTANCE) != 0 && blockState.get(BOTTOM).booleanValue() && entityContext.isAbove(OUTLINE_SHAPE, blockPos, true)) {
                return COLLISION_SHAPE;
            }
            return VoxelShapes.empty();
        }
        return NORMAL_OUTLINE_SHAPE;
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        if (blockState.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(blockState);
    }

    private boolean shouldBeBottom(BlockView blockView, BlockPos blockPos, int i) {
        return i > 0 && blockView.getBlockState(blockPos.down()).getBlock() != this;
    }

    public static int calculateDistance(BlockView blockView, BlockPos blockPos) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos).setOffset(Direction.DOWN);
        BlockState blockState = blockView.getBlockState(mutable);
        int i = 7;
        if (blockState.getBlock() == Blocks.SCAFFOLDING) {
            i = blockState.get(DISTANCE);
        } else if (blockState.isSideSolidFullSquare(blockView, mutable, Direction.UP)) {
            return 0;
        }
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockState blockState2 = blockView.getBlockState(mutable.set(blockPos).setOffset(direction));
            if (blockState2.getBlock() != Blocks.SCAFFOLDING || (i = Math.min(i, blockState2.get(DISTANCE) + 1)) != 1) continue;
            break;
        }
        return i;
    }

    static {
        COLLISION_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        OUTLINE_SHAPE = VoxelShapes.fullCube().offset(0.0, -1.0, 0.0);
        DISTANCE = Properties.DISTANCE_0_7;
        WATERLOGGED = Properties.WATERLOGGED;
        BOTTOM = Properties.BOTTOM;
        VoxelShape voxelShape = Block.createCuboidShape(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
        VoxelShape voxelShape2 = Block.createCuboidShape(0.0, 0.0, 0.0, 2.0, 16.0, 2.0);
        VoxelShape voxelShape3 = Block.createCuboidShape(14.0, 0.0, 0.0, 16.0, 16.0, 2.0);
        VoxelShape voxelShape4 = Block.createCuboidShape(0.0, 0.0, 14.0, 2.0, 16.0, 16.0);
        VoxelShape voxelShape5 = Block.createCuboidShape(14.0, 0.0, 14.0, 16.0, 16.0, 16.0);
        NORMAL_OUTLINE_SHAPE = VoxelShapes.union(voxelShape, voxelShape2, voxelShape3, voxelShape4, voxelShape5);
        VoxelShape voxelShape6 = Block.createCuboidShape(0.0, 0.0, 0.0, 2.0, 2.0, 16.0);
        VoxelShape voxelShape7 = Block.createCuboidShape(14.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        VoxelShape voxelShape8 = Block.createCuboidShape(0.0, 0.0, 14.0, 16.0, 2.0, 16.0);
        VoxelShape voxelShape9 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 2.0);
        BOTTOM_OUTLINE_SHAPE = VoxelShapes.union(COLLISION_SHAPE, NORMAL_OUTLINE_SHAPE, voxelShape7, voxelShape6, voxelShape9, voxelShape8);
    }
}

