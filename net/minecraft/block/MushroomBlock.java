/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectedPlantBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class MushroomBlock
extends Block {
    public static final BooleanProperty NORTH = ConnectedPlantBlock.NORTH;
    public static final BooleanProperty EAST = ConnectedPlantBlock.EAST;
    public static final BooleanProperty SOUTH = ConnectedPlantBlock.SOUTH;
    public static final BooleanProperty WEST = ConnectedPlantBlock.WEST;
    public static final BooleanProperty UP = ConnectedPlantBlock.UP;
    public static final BooleanProperty DOWN = ConnectedPlantBlock.DOWN;
    private static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ConnectedPlantBlock.FACING_PROPERTIES;

    public MushroomBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(NORTH, true)).with(EAST, true)).with(SOUTH, true)).with(WEST, true)).with(UP, true)).with(DOWN, true));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        World blockView = itemPlacementContext.getWorld();
        BlockPos blockPos = itemPlacementContext.getBlockPos();
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(DOWN, this != blockView.getBlockState(blockPos.down()).getBlock())).with(UP, this != blockView.getBlockState(blockPos.up()).getBlock())).with(NORTH, this != blockView.getBlockState(blockPos.north()).getBlock())).with(EAST, this != blockView.getBlockState(blockPos.east()).getBlock())).with(SOUTH, this != blockView.getBlockState(blockPos.south()).getBlock())).with(WEST, this != blockView.getBlockState(blockPos.west()).getBlock());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        if (blockState2.getBlock() == this) {
            return (BlockState)blockState.with(FACING_PROPERTIES.get(direction), false);
        }
        return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
    }

    @Override
    public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)blockState.with(FACING_PROPERTIES.get(blockRotation.rotate(Direction.NORTH)), blockState.get(NORTH))).with(FACING_PROPERTIES.get(blockRotation.rotate(Direction.SOUTH)), blockState.get(SOUTH))).with(FACING_PROPERTIES.get(blockRotation.rotate(Direction.EAST)), blockState.get(EAST))).with(FACING_PROPERTIES.get(blockRotation.rotate(Direction.WEST)), blockState.get(WEST))).with(FACING_PROPERTIES.get(blockRotation.rotate(Direction.UP)), blockState.get(UP))).with(FACING_PROPERTIES.get(blockRotation.rotate(Direction.DOWN)), blockState.get(DOWN));
    }

    @Override
    public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)blockState.with(FACING_PROPERTIES.get(blockMirror.apply(Direction.NORTH)), blockState.get(NORTH))).with(FACING_PROPERTIES.get(blockMirror.apply(Direction.SOUTH)), blockState.get(SOUTH))).with(FACING_PROPERTIES.get(blockMirror.apply(Direction.EAST)), blockState.get(EAST))).with(FACING_PROPERTIES.get(blockMirror.apply(Direction.WEST)), blockState.get(WEST))).with(FACING_PROPERTIES.get(blockMirror.apply(Direction.UP)), blockState.get(UP))).with(FACING_PROPERTIES.get(blockMirror.apply(Direction.DOWN)), blockState.get(DOWN));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }
}

