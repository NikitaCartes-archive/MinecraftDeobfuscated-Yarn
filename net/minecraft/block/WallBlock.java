/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.WallShape;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class WallBlock
extends Block
implements Waterloggable {
    public static final BooleanProperty UP = Properties.UP;
    public static final EnumProperty<WallShape> EAST_SHAPE = Properties.EAST_WALL_SHAPE;
    public static final EnumProperty<WallShape> NORTH_SHAPE = Properties.NORTH_WALL_SHAPE;
    public static final EnumProperty<WallShape> SOUTH_SHAPE = Properties.SOUTH_WALL_SHAPE;
    public static final EnumProperty<WallShape> WEST_SHAPE = Properties.WEST_WALL_SHAPE;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private final Map<BlockState, VoxelShape> shapeMap;
    private final Map<BlockState, VoxelShape> collisionShapeMap;
    private static final int field_31276 = 3;
    private static final int field_31277 = 14;
    private static final int field_31278 = 4;
    private static final int field_31279 = 1;
    private static final int field_31280 = 7;
    private static final int field_31281 = 9;
    private static final VoxelShape TALL_POST_SHAPE = Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 9.0);
    private static final VoxelShape TALL_NORTH_SHAPE = Block.createCuboidShape(7.0, 0.0, 0.0, 9.0, 16.0, 9.0);
    private static final VoxelShape TALL_SOUTH_SHAPE = Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 16.0);
    private static final VoxelShape TALL_WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 7.0, 9.0, 16.0, 9.0);
    private static final VoxelShape TALL_EAST_SHAPE = Block.createCuboidShape(7.0, 0.0, 7.0, 16.0, 16.0, 9.0);

    public WallBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(UP, true)).with(NORTH_SHAPE, WallShape.NONE)).with(EAST_SHAPE, WallShape.NONE)).with(SOUTH_SHAPE, WallShape.NONE)).with(WEST_SHAPE, WallShape.NONE)).with(WATERLOGGED, false));
        this.shapeMap = this.getShapeMap(4.0f, 3.0f, 16.0f, 0.0f, 14.0f, 16.0f);
        this.collisionShapeMap = this.getShapeMap(4.0f, 3.0f, 24.0f, 0.0f, 24.0f, 24.0f);
    }

    private static VoxelShape getVoxelShape(VoxelShape base, WallShape wallShape, VoxelShape tall, VoxelShape low) {
        if (wallShape == WallShape.TALL) {
            return VoxelShapes.union(base, low);
        }
        if (wallShape == WallShape.LOW) {
            return VoxelShapes.union(base, tall);
        }
        return base;
    }

    private Map<BlockState, VoxelShape> getShapeMap(float f, float g, float h, float i, float j, float k) {
        float l = 8.0f - f;
        float m = 8.0f + f;
        float n = 8.0f - g;
        float o = 8.0f + g;
        VoxelShape voxelShape = Block.createCuboidShape(l, 0.0, l, m, h, m);
        VoxelShape voxelShape2 = Block.createCuboidShape(n, i, 0.0, o, j, o);
        VoxelShape voxelShape3 = Block.createCuboidShape(n, i, n, o, j, 16.0);
        VoxelShape voxelShape4 = Block.createCuboidShape(0.0, i, n, o, j, o);
        VoxelShape voxelShape5 = Block.createCuboidShape(n, i, n, 16.0, j, o);
        VoxelShape voxelShape6 = Block.createCuboidShape(n, i, 0.0, o, k, o);
        VoxelShape voxelShape7 = Block.createCuboidShape(n, i, n, o, k, 16.0);
        VoxelShape voxelShape8 = Block.createCuboidShape(0.0, i, n, o, k, o);
        VoxelShape voxelShape9 = Block.createCuboidShape(n, i, n, 16.0, k, o);
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
        for (Boolean boolean_ : UP.getValues()) {
            for (WallShape wallShape : EAST_SHAPE.getValues()) {
                for (WallShape wallShape2 : NORTH_SHAPE.getValues()) {
                    for (WallShape wallShape3 : WEST_SHAPE.getValues()) {
                        for (WallShape wallShape4 : SOUTH_SHAPE.getValues()) {
                            VoxelShape voxelShape10 = VoxelShapes.empty();
                            voxelShape10 = WallBlock.getVoxelShape(voxelShape10, wallShape, voxelShape5, voxelShape9);
                            voxelShape10 = WallBlock.getVoxelShape(voxelShape10, wallShape3, voxelShape4, voxelShape8);
                            voxelShape10 = WallBlock.getVoxelShape(voxelShape10, wallShape2, voxelShape2, voxelShape6);
                            voxelShape10 = WallBlock.getVoxelShape(voxelShape10, wallShape4, voxelShape3, voxelShape7);
                            if (boolean_.booleanValue()) {
                                voxelShape10 = VoxelShapes.union(voxelShape10, voxelShape);
                            }
                            BlockState blockState = (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(UP, boolean_)).with(EAST_SHAPE, wallShape)).with(WEST_SHAPE, wallShape3)).with(NORTH_SHAPE, wallShape2)).with(SOUTH_SHAPE, wallShape4);
                            builder.put((BlockState)blockState.with(WATERLOGGED, false), voxelShape10);
                            builder.put((BlockState)blockState.with(WATERLOGGED, true), voxelShape10);
                        }
                    }
                }
            }
        }
        return builder.build();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.shapeMap.get(state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.collisionShapeMap.get(state);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    private boolean shouldConnectTo(BlockState state, boolean faceFullSquare, Direction side) {
        Block block = state.getBlock();
        boolean bl = block instanceof FenceGateBlock && FenceGateBlock.canWallConnect(state, side);
        return state.isIn(BlockTags.WALLS) || !WallBlock.cannotConnect(state) && faceFullSquare || block instanceof PaneBlock || bl;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World worldView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        BlockPos blockPos2 = blockPos.north();
        BlockPos blockPos3 = blockPos.east();
        BlockPos blockPos4 = blockPos.south();
        BlockPos blockPos5 = blockPos.west();
        BlockPos blockPos6 = blockPos.up();
        BlockState blockState = worldView.getBlockState(blockPos2);
        BlockState blockState2 = worldView.getBlockState(blockPos3);
        BlockState blockState3 = worldView.getBlockState(blockPos4);
        BlockState blockState4 = worldView.getBlockState(blockPos5);
        BlockState blockState5 = worldView.getBlockState(blockPos6);
        boolean bl = this.shouldConnectTo(blockState, blockState.isSideSolidFullSquare(worldView, blockPos2, Direction.SOUTH), Direction.SOUTH);
        boolean bl2 = this.shouldConnectTo(blockState2, blockState2.isSideSolidFullSquare(worldView, blockPos3, Direction.WEST), Direction.WEST);
        boolean bl3 = this.shouldConnectTo(blockState3, blockState3.isSideSolidFullSquare(worldView, blockPos4, Direction.NORTH), Direction.NORTH);
        boolean bl4 = this.shouldConnectTo(blockState4, blockState4.isSideSolidFullSquare(worldView, blockPos5, Direction.EAST), Direction.EAST);
        BlockState blockState6 = (BlockState)this.getDefaultState().with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
        return this.getStateWith(worldView, blockState6, blockPos6, blockState5, bl, bl2, bl3, bl4);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED).booleanValue()) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (direction == Direction.DOWN) {
            return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        }
        if (direction == Direction.UP) {
            return this.getStateAt(world, state, neighborPos, neighborState);
        }
        return this.getStateWithNeighbor(world, pos, state, neighborPos, neighborState, direction);
    }

    private static boolean isConnected(BlockState state, Property<WallShape> property) {
        return state.get(property) != WallShape.NONE;
    }

    private static boolean shouldUseTallShape(VoxelShape aboveShape, VoxelShape tallShape) {
        return !VoxelShapes.matchesAnywhere(tallShape, aboveShape, BooleanBiFunction.ONLY_FIRST);
    }

    private BlockState getStateAt(WorldView world, BlockState state, BlockPos pos, BlockState aboveState) {
        boolean bl = WallBlock.isConnected(state, NORTH_SHAPE);
        boolean bl2 = WallBlock.isConnected(state, EAST_SHAPE);
        boolean bl3 = WallBlock.isConnected(state, SOUTH_SHAPE);
        boolean bl4 = WallBlock.isConnected(state, WEST_SHAPE);
        return this.getStateWith(world, state, pos, aboveState, bl, bl2, bl3, bl4);
    }

    private BlockState getStateWithNeighbor(WorldView world, BlockPos pos, BlockState state, BlockPos neighborPos, BlockState neighborState, Direction direction) {
        Direction direction2 = direction.getOpposite();
        boolean bl = direction == Direction.NORTH ? this.shouldConnectTo(neighborState, neighborState.isSideSolidFullSquare(world, neighborPos, direction2), direction2) : WallBlock.isConnected(state, NORTH_SHAPE);
        boolean bl2 = direction == Direction.EAST ? this.shouldConnectTo(neighborState, neighborState.isSideSolidFullSquare(world, neighborPos, direction2), direction2) : WallBlock.isConnected(state, EAST_SHAPE);
        boolean bl3 = direction == Direction.SOUTH ? this.shouldConnectTo(neighborState, neighborState.isSideSolidFullSquare(world, neighborPos, direction2), direction2) : WallBlock.isConnected(state, SOUTH_SHAPE);
        boolean bl4 = direction == Direction.WEST ? this.shouldConnectTo(neighborState, neighborState.isSideSolidFullSquare(world, neighborPos, direction2), direction2) : WallBlock.isConnected(state, WEST_SHAPE);
        BlockPos blockPos = pos.up();
        BlockState blockState = world.getBlockState(blockPos);
        return this.getStateWith(world, state, blockPos, blockState, bl, bl2, bl3, bl4);
    }

    private BlockState getStateWith(WorldView world, BlockState state, BlockPos pos, BlockState aboveState, boolean north, boolean east, boolean south, boolean west) {
        VoxelShape voxelShape = aboveState.getCollisionShape(world, pos).getFace(Direction.DOWN);
        BlockState blockState = this.getStateWith(state, north, east, south, west, voxelShape);
        return (BlockState)blockState.with(UP, this.shouldHavePost(blockState, aboveState, voxelShape));
    }

    private boolean shouldHavePost(BlockState state, BlockState aboveState, VoxelShape aboveShape) {
        boolean bl7;
        boolean bl6;
        boolean bl;
        boolean bl2 = bl = aboveState.getBlock() instanceof WallBlock && aboveState.get(UP) != false;
        if (bl) {
            return true;
        }
        WallShape wallShape = state.get(NORTH_SHAPE);
        WallShape wallShape2 = state.get(SOUTH_SHAPE);
        WallShape wallShape3 = state.get(EAST_SHAPE);
        WallShape wallShape4 = state.get(WEST_SHAPE);
        boolean bl22 = wallShape2 == WallShape.NONE;
        boolean bl3 = wallShape4 == WallShape.NONE;
        boolean bl4 = wallShape3 == WallShape.NONE;
        boolean bl5 = wallShape == WallShape.NONE;
        boolean bl8 = bl6 = bl5 && bl22 && bl3 && bl4 || bl5 != bl22 || bl3 != bl4;
        if (bl6) {
            return true;
        }
        boolean bl9 = bl7 = wallShape == WallShape.TALL && wallShape2 == WallShape.TALL || wallShape3 == WallShape.TALL && wallShape4 == WallShape.TALL;
        if (bl7) {
            return false;
        }
        return aboveState.isIn(BlockTags.WALL_POST_OVERRIDE) || WallBlock.shouldUseTallShape(aboveShape, TALL_POST_SHAPE);
    }

    private BlockState getStateWith(BlockState state, boolean north, boolean east, boolean south, boolean west, VoxelShape aboveShape) {
        return (BlockState)((BlockState)((BlockState)((BlockState)state.with(NORTH_SHAPE, this.getWallShape(north, aboveShape, TALL_NORTH_SHAPE))).with(EAST_SHAPE, this.getWallShape(east, aboveShape, TALL_EAST_SHAPE))).with(SOUTH_SHAPE, this.getWallShape(south, aboveShape, TALL_SOUTH_SHAPE))).with(WEST_SHAPE, this.getWallShape(west, aboveShape, TALL_WEST_SHAPE));
    }

    private WallShape getWallShape(boolean connected, VoxelShape aboveShape, VoxelShape tallShape) {
        if (connected) {
            if (WallBlock.shouldUseTallShape(aboveShape, tallShape)) {
                return WallShape.TALL;
            }
            return WallShape.LOW;
        }
        return WallShape.NONE;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return state.get(WATERLOGGED) == false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP, NORTH_SHAPE, EAST_SHAPE, WEST_SHAPE, SOUTH_SHAPE, WATERLOGGED);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with(NORTH_SHAPE, state.get(SOUTH_SHAPE))).with(EAST_SHAPE, state.get(WEST_SHAPE))).with(SOUTH_SHAPE, state.get(NORTH_SHAPE))).with(WEST_SHAPE, state.get(EAST_SHAPE));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with(NORTH_SHAPE, state.get(EAST_SHAPE))).with(EAST_SHAPE, state.get(SOUTH_SHAPE))).with(SOUTH_SHAPE, state.get(WEST_SHAPE))).with(WEST_SHAPE, state.get(NORTH_SHAPE));
            }
            case CLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with(NORTH_SHAPE, state.get(WEST_SHAPE))).with(EAST_SHAPE, state.get(NORTH_SHAPE))).with(SOUTH_SHAPE, state.get(EAST_SHAPE))).with(WEST_SHAPE, state.get(SOUTH_SHAPE));
            }
        }
        return state;
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)state.with(NORTH_SHAPE, state.get(SOUTH_SHAPE))).with(SOUTH_SHAPE, state.get(NORTH_SHAPE));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)state.with(EAST_SHAPE, state.get(WEST_SHAPE))).with(WEST_SHAPE, state.get(EAST_SHAPE));
            }
        }
        return super.mirror(state, mirror);
    }
}

