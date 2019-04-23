/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalConnectedBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class PaneBlock
extends HorizontalConnectedBlock {
    protected PaneBlock(Block.Settings settings) {
        super(1.0f, 1.0f, 16.0f, 16.0f, 16.0f, settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false)).with(WATERLOGGED, false));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        World blockView = itemPlacementContext.getWorld();
        BlockPos blockPos = itemPlacementContext.getBlockPos();
        FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getBlockPos());
        BlockPos blockPos2 = blockPos.north();
        BlockPos blockPos3 = blockPos.south();
        BlockPos blockPos4 = blockPos.west();
        BlockPos blockPos5 = blockPos.east();
        BlockState blockState = blockView.getBlockState(blockPos2);
        BlockState blockState2 = blockView.getBlockState(blockPos3);
        BlockState blockState3 = blockView.getBlockState(blockPos4);
        BlockState blockState4 = blockView.getBlockState(blockPos5);
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(NORTH, this.connectsTo(blockState, Block.isSolidFullSquare(blockState, blockView, blockPos2, Direction.SOUTH)))).with(SOUTH, this.connectsTo(blockState2, Block.isSolidFullSquare(blockState2, blockView, blockPos3, Direction.NORTH)))).with(WEST, this.connectsTo(blockState3, Block.isSolidFullSquare(blockState3, blockView, blockPos4, Direction.EAST)))).with(EAST, this.connectsTo(blockState4, Block.isSolidFullSquare(blockState4, blockView, blockPos5, Direction.WEST)))).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        if (blockState.get(WATERLOGGED).booleanValue()) {
            iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
        }
        if (direction.getAxis().isHorizontal()) {
            return (BlockState)blockState.with((Property)FACING_PROPERTIES.get(direction), this.connectsTo(blockState2, Block.isSolidFullSquare(blockState2, iWorld, blockPos2, direction.getOpposite())));
        }
        return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean isSideInvisible(BlockState blockState, BlockState blockState2, Direction direction) {
        if (blockState2.getBlock() == this) {
            if (!direction.getAxis().isHorizontal()) {
                return true;
            }
            if (((Boolean)blockState.get((Property)FACING_PROPERTIES.get(direction))).booleanValue() && ((Boolean)blockState2.get((Property)FACING_PROPERTIES.get(direction.getOpposite()))).booleanValue()) {
                return true;
            }
        }
        return super.isSideInvisible(blockState, blockState2, direction);
    }

    public final boolean connectsTo(BlockState blockState, boolean bl) {
        Block block = blockState.getBlock();
        return !PaneBlock.canConnect(block) && bl || block instanceof PaneBlock;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
    }
}

