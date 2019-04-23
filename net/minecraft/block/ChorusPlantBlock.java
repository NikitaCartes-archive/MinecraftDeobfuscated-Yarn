/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConnectedPlantBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class ChorusPlantBlock
extends ConnectedPlantBlock {
    protected ChorusPlantBlock(Block.Settings settings) {
        super(0.3125f, settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false)).with(UP, false)).with(DOWN, false));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        return this.withConnectionProperties(itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos());
    }

    public BlockState withConnectionProperties(BlockView blockView, BlockPos blockPos) {
        Block block = blockView.getBlockState(blockPos.down()).getBlock();
        Block block2 = blockView.getBlockState(blockPos.up()).getBlock();
        Block block3 = blockView.getBlockState(blockPos.north()).getBlock();
        Block block4 = blockView.getBlockState(blockPos.east()).getBlock();
        Block block5 = blockView.getBlockState(blockPos.south()).getBlock();
        Block block6 = blockView.getBlockState(blockPos.west()).getBlock();
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(DOWN, block == this || block == Blocks.CHORUS_FLOWER || block == Blocks.END_STONE)).with(UP, block2 == this || block2 == Blocks.CHORUS_FLOWER)).with(NORTH, block3 == this || block3 == Blocks.CHORUS_FLOWER)).with(EAST, block4 == this || block4 == Blocks.CHORUS_FLOWER)).with(SOUTH, block5 == this || block5 == Blocks.CHORUS_FLOWER)).with(WEST, block6 == this || block6 == Blocks.CHORUS_FLOWER);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        if (!blockState.canPlaceAt(iWorld, blockPos)) {
            iWorld.getBlockTickScheduler().schedule(blockPos, this, 1);
            return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
        }
        Block block = blockState2.getBlock();
        boolean bl = block == this || block == Blocks.CHORUS_FLOWER || direction == Direction.DOWN && block == Blocks.END_STONE;
        return (BlockState)blockState.with((Property)FACING_PROPERTIES.get(direction), bl);
    }

    @Override
    public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        if (!blockState.canPlaceAt(world, blockPos)) {
            world.breakBlock(blockPos, true);
        }
    }

    @Override
    public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
        BlockState blockState2 = viewableWorld.getBlockState(blockPos.down());
        boolean bl = !viewableWorld.getBlockState(blockPos.up()).isAir() && !blockState2.isAir();
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos2 = blockPos.offset(direction);
            Block block = viewableWorld.getBlockState(blockPos2).getBlock();
            if (block != this) continue;
            if (bl) {
                return false;
            }
            Block block2 = viewableWorld.getBlockState(blockPos2.down()).getBlock();
            if (block2 != this && block2 != Blocks.END_STONE) continue;
            return true;
        }
        Block block3 = blockState2.getBlock();
        return block3 == this || block3 == Blocks.END_STONE;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
        return false;
    }
}

