/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class VineBlock
extends Block {
    public static final BooleanProperty UP = ConnectingBlock.UP;
    public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
    public static final BooleanProperty EAST = ConnectingBlock.EAST;
    public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
    public static final BooleanProperty WEST = ConnectingBlock.WEST;
    public static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES.entrySet().stream().filter(entry -> entry.getKey() != Direction.DOWN).collect(Util.toMap());
    protected static final VoxelShape UP_SHAPE = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);

    public VineBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(UP, false)).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        VoxelShape voxelShape = VoxelShapes.empty();
        if (blockState.get(UP).booleanValue()) {
            voxelShape = VoxelShapes.union(voxelShape, UP_SHAPE);
        }
        if (blockState.get(NORTH).booleanValue()) {
            voxelShape = VoxelShapes.union(voxelShape, NORTH_SHAPE);
        }
        if (blockState.get(EAST).booleanValue()) {
            voxelShape = VoxelShapes.union(voxelShape, EAST_SHAPE);
        }
        if (blockState.get(SOUTH).booleanValue()) {
            voxelShape = VoxelShapes.union(voxelShape, SOUTH_SHAPE);
        }
        if (blockState.get(WEST).booleanValue()) {
            voxelShape = VoxelShapes.union(voxelShape, WEST_SHAPE);
        }
        return voxelShape;
    }

    @Override
    public boolean canPlaceAt(BlockState blockState, CollisionView collisionView, BlockPos blockPos) {
        return this.hasAdjacentBlocks(this.getPlacementShape(blockState, collisionView, blockPos));
    }

    private boolean hasAdjacentBlocks(BlockState blockState) {
        return this.getAdjacentBlockCount(blockState) > 0;
    }

    private int getAdjacentBlockCount(BlockState blockState) {
        int i = 0;
        for (BooleanProperty booleanProperty : FACING_PROPERTIES.values()) {
            if (!blockState.get(booleanProperty).booleanValue()) continue;
            ++i;
        }
        return i;
    }

    private boolean shouldHaveSide(BlockView blockView, BlockPos blockPos, Direction direction) {
        if (direction == Direction.DOWN) {
            return false;
        }
        BlockPos blockPos2 = blockPos.offset(direction);
        if (VineBlock.shouldConnectTo(blockView, blockPos2, direction)) {
            return true;
        }
        if (direction.getAxis() != Direction.Axis.Y) {
            BooleanProperty booleanProperty = FACING_PROPERTIES.get(direction);
            BlockState blockState = blockView.getBlockState(blockPos.up());
            return blockState.getBlock() == this && blockState.get(booleanProperty) != false;
        }
        return false;
    }

    public static boolean shouldConnectTo(BlockView blockView, BlockPos blockPos, Direction direction) {
        BlockState blockState = blockView.getBlockState(blockPos);
        return Block.isFaceFullSquare(blockState.getCollisionShape(blockView, blockPos), direction.getOpposite());
    }

    private BlockState getPlacementShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.up();
        if (blockState.get(UP).booleanValue()) {
            blockState = (BlockState)blockState.with(UP, VineBlock.shouldConnectTo(blockView, blockPos2, Direction.DOWN));
        }
        BlockState blockState2 = null;
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BooleanProperty booleanProperty = VineBlock.getFacingProperty(direction);
            if (!blockState.get(booleanProperty).booleanValue()) continue;
            boolean bl = this.shouldHaveSide(blockView, blockPos, direction);
            if (!bl) {
                if (blockState2 == null) {
                    blockState2 = blockView.getBlockState(blockPos2);
                }
                bl = blockState2.getBlock() == this && blockState2.get(booleanProperty) != false;
            }
            blockState = (BlockState)blockState.with(booleanProperty, bl);
        }
        return blockState;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        if (direction == Direction.DOWN) {
            return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
        }
        BlockState blockState3 = this.getPlacementShape(blockState, iWorld, blockPos);
        if (!this.hasAdjacentBlocks(blockState3)) {
            return Blocks.AIR.getDefaultState();
        }
        return blockState3;
    }

    @Override
    public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        BlockState blockState6;
        BlockState blockState5;
        BlockPos blockPos3;
        BlockState blockState3;
        if (world.isClient) {
            return;
        }
        BlockState blockState2 = this.getPlacementShape(blockState, world, blockPos);
        if (blockState2 != blockState) {
            if (this.hasAdjacentBlocks(blockState2)) {
                world.setBlockState(blockPos, blockState2, 2);
            } else {
                VineBlock.dropStacks(blockState, world, blockPos);
                world.removeBlock(blockPos, false);
            }
            return;
        }
        if (world.random.nextInt(4) != 0) {
            return;
        }
        Direction direction = Direction.random(random);
        BlockPos blockPos2 = blockPos.up();
        if (direction.getAxis().isHorizontal() && !blockState.get(VineBlock.getFacingProperty(direction)).booleanValue()) {
            if (!this.canGrowAt(world, blockPos)) {
                return;
            }
            BlockPos blockPos32 = blockPos.offset(direction);
            BlockState blockState32 = world.getBlockState(blockPos32);
            if (blockState32.isAir()) {
                Direction direction2 = direction.rotateYClockwise();
                Direction direction3 = direction.rotateYCounterclockwise();
                boolean bl = blockState.get(VineBlock.getFacingProperty(direction2));
                boolean bl2 = blockState.get(VineBlock.getFacingProperty(direction3));
                BlockPos blockPos4 = blockPos32.offset(direction2);
                BlockPos blockPos5 = blockPos32.offset(direction3);
                if (bl && VineBlock.shouldConnectTo(world, blockPos4, direction2)) {
                    world.setBlockState(blockPos32, (BlockState)this.getDefaultState().with(VineBlock.getFacingProperty(direction2), true), 2);
                } else if (bl2 && VineBlock.shouldConnectTo(world, blockPos5, direction3)) {
                    world.setBlockState(blockPos32, (BlockState)this.getDefaultState().with(VineBlock.getFacingProperty(direction3), true), 2);
                } else {
                    Direction direction4 = direction.getOpposite();
                    if (bl && world.isAir(blockPos4) && VineBlock.shouldConnectTo(world, blockPos.offset(direction2), direction4)) {
                        world.setBlockState(blockPos4, (BlockState)this.getDefaultState().with(VineBlock.getFacingProperty(direction4), true), 2);
                    } else if (bl2 && world.isAir(blockPos5) && VineBlock.shouldConnectTo(world, blockPos.offset(direction3), direction4)) {
                        world.setBlockState(blockPos5, (BlockState)this.getDefaultState().with(VineBlock.getFacingProperty(direction4), true), 2);
                    } else if ((double)world.random.nextFloat() < 0.05 && VineBlock.shouldConnectTo(world, blockPos32.up(), Direction.UP)) {
                        world.setBlockState(blockPos32, (BlockState)this.getDefaultState().with(UP, true), 2);
                    }
                }
            } else if (VineBlock.shouldConnectTo(world, blockPos32, direction)) {
                world.setBlockState(blockPos, (BlockState)blockState.with(VineBlock.getFacingProperty(direction), true), 2);
            }
            return;
        }
        if (direction == Direction.UP && blockPos.getY() < 255) {
            if (this.shouldHaveSide(world, blockPos, direction)) {
                world.setBlockState(blockPos, (BlockState)blockState.with(UP, true), 2);
                return;
            }
            if (world.isAir(blockPos2)) {
                if (!this.canGrowAt(world, blockPos)) {
                    return;
                }
                BlockState blockState4 = blockState;
                for (Direction direction2 : Direction.Type.HORIZONTAL) {
                    if (!random.nextBoolean() && VineBlock.shouldConnectTo(world, blockPos2.offset(direction2), Direction.UP)) continue;
                    blockState4 = (BlockState)blockState4.with(VineBlock.getFacingProperty(direction2), false);
                }
                if (this.hasHorizontalSide(blockState4)) {
                    world.setBlockState(blockPos2, blockState4, 2);
                }
                return;
            }
        }
        if (blockPos.getY() > 0 && ((blockState3 = world.getBlockState(blockPos3 = blockPos.down())).isAir() || blockState3.getBlock() == this) && (blockState5 = blockState3.isAir() ? this.getDefaultState() : blockState3) != (blockState6 = this.getGrownState(blockState, blockState5, random)) && this.hasHorizontalSide(blockState6)) {
            world.setBlockState(blockPos3, blockState6, 2);
        }
    }

    private BlockState getGrownState(BlockState blockState, BlockState blockState2, Random random) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BooleanProperty booleanProperty;
            if (!random.nextBoolean() || !blockState.get(booleanProperty = VineBlock.getFacingProperty(direction)).booleanValue()) continue;
            blockState2 = (BlockState)blockState2.with(booleanProperty, true);
        }
        return blockState2;
    }

    private boolean hasHorizontalSide(BlockState blockState) {
        return blockState.get(NORTH) != false || blockState.get(EAST) != false || blockState.get(SOUTH) != false || blockState.get(WEST) != false;
    }

    private boolean canGrowAt(BlockView blockView, BlockPos blockPos) {
        int i = 4;
        Iterable<BlockPos> iterable = BlockPos.iterate(blockPos.getX() - 4, blockPos.getY() - 1, blockPos.getZ() - 4, blockPos.getX() + 4, blockPos.getY() + 1, blockPos.getZ() + 4);
        int j = 5;
        for (BlockPos blockPos2 : iterable) {
            if (blockView.getBlockState(blockPos2).getBlock() != this || --j > 0) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean canReplace(BlockState blockState, ItemPlacementContext itemPlacementContext) {
        BlockState blockState2 = itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getBlockPos());
        if (blockState2.getBlock() == this) {
            return this.getAdjacentBlockCount(blockState2) < FACING_PROPERTIES.size();
        }
        return super.canReplace(blockState, itemPlacementContext);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        BlockState blockState = itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getBlockPos());
        boolean bl = blockState.getBlock() == this;
        BlockState blockState2 = bl ? blockState : this.getDefaultState();
        for (Direction direction : itemPlacementContext.getPlacementDirections()) {
            boolean bl2;
            if (direction == Direction.DOWN) continue;
            BooleanProperty booleanProperty = VineBlock.getFacingProperty(direction);
            boolean bl3 = bl2 = bl && blockState.get(booleanProperty) != false;
            if (bl2 || !this.shouldHaveSide(itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos(), direction)) continue;
            return (BlockState)blockState2.with(booleanProperty, true);
        }
        return bl ? blockState2 : null;
    }

    @Override
    public RenderLayer getRenderLayer() {
        return RenderLayer.CUTOUT;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP, NORTH, EAST, SOUTH, WEST);
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

    public static BooleanProperty getFacingProperty(Direction direction) {
        return FACING_PROPERTIES.get(direction);
    }
}

