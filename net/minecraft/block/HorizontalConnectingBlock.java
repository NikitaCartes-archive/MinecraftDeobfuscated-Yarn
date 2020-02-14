/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class HorizontalConnectingBlock
extends Block
implements Waterloggable {
    public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
    public static final BooleanProperty EAST = ConnectingBlock.EAST;
    public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
    public static final BooleanProperty WEST = ConnectingBlock.WEST;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    protected static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES.entrySet().stream().filter(entry -> ((Direction)entry.getKey()).getAxis().isHorizontal()).collect(Util.toMap());
    protected final VoxelShape[] collisionShapes;
    protected final VoxelShape[] boundingShapes;
    private final Object2IntMap<BlockState> SHAPE_INDEX_CACHE = new Object2IntOpenHashMap<BlockState>();

    protected HorizontalConnectingBlock(float radius1, float radius2, float boundingHeight1, float boundingHeight2, float collisionHeight, Block.Settings settings) {
        super(settings);
        this.collisionShapes = this.createShapes(radius1, radius2, collisionHeight, 0.0f, collisionHeight);
        this.boundingShapes = this.createShapes(radius1, radius2, boundingHeight1, 0.0f, boundingHeight2);
    }

    protected VoxelShape[] createShapes(float radius1, float radius2, float height1, float offset2, float height2) {
        float f = 8.0f - radius1;
        float g = 8.0f + radius1;
        float h = 8.0f - radius2;
        float i = 8.0f + radius2;
        VoxelShape voxelShape = Block.createCuboidShape(f, 0.0, f, g, height1, g);
        VoxelShape voxelShape2 = Block.createCuboidShape(h, offset2, 0.0, i, height2, i);
        VoxelShape voxelShape3 = Block.createCuboidShape(h, offset2, h, i, height2, 16.0);
        VoxelShape voxelShape4 = Block.createCuboidShape(0.0, offset2, h, i, height2, i);
        VoxelShape voxelShape5 = Block.createCuboidShape(h, offset2, h, 16.0, height2, i);
        VoxelShape voxelShape6 = VoxelShapes.union(voxelShape2, voxelShape5);
        VoxelShape voxelShape7 = VoxelShapes.union(voxelShape3, voxelShape4);
        VoxelShape[] voxelShapes = new VoxelShape[]{VoxelShapes.empty(), voxelShape3, voxelShape4, voxelShape7, voxelShape2, VoxelShapes.union(voxelShape3, voxelShape2), VoxelShapes.union(voxelShape4, voxelShape2), VoxelShapes.union(voxelShape7, voxelShape2), voxelShape5, VoxelShapes.union(voxelShape3, voxelShape5), VoxelShapes.union(voxelShape4, voxelShape5), VoxelShapes.union(voxelShape7, voxelShape5), voxelShape6, VoxelShapes.union(voxelShape3, voxelShape6), VoxelShapes.union(voxelShape4, voxelShape6), VoxelShapes.union(voxelShape7, voxelShape6)};
        for (int j = 0; j < 16; ++j) {
            voxelShapes[j] = VoxelShapes.union(voxelShape, voxelShapes[j]);
        }
        return voxelShapes;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return state.get(WATERLOGGED) == false;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityContext context) {
        return this.boundingShapes[this.getShapeIndex(state)];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, EntityContext context) {
        return this.collisionShapes[this.getShapeIndex(state)];
    }

    private static int getDirectionMask(Direction dir) {
        return 1 << dir.getHorizontal();
    }

    protected int getShapeIndex(BlockState state) {
        return this.SHAPE_INDEX_CACHE.computeIntIfAbsent(state, blockState -> {
            int i = 0;
            if (blockState.get(NORTH).booleanValue()) {
                i |= HorizontalConnectingBlock.getDirectionMask(Direction.NORTH);
            }
            if (blockState.get(EAST).booleanValue()) {
                i |= HorizontalConnectingBlock.getDirectionMask(Direction.EAST);
            }
            if (blockState.get(SOUTH).booleanValue()) {
                i |= HorizontalConnectingBlock.getDirectionMask(Direction.SOUTH);
            }
            if (blockState.get(WEST).booleanValue()) {
                i |= HorizontalConnectingBlock.getDirectionMask(Direction.WEST);
            }
            return i;
        });
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment env) {
        return false;
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
}

