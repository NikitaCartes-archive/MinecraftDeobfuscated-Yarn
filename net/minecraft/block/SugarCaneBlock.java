/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class SugarCaneBlock
extends Block {
    public static final IntProperty AGE = Properties.AGE_15;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    protected SugarCaneBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        return SHAPE;
    }

    @Override
    public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        if (!blockState.canPlaceAt(world, blockPos)) {
            world.breakBlock(blockPos, true);
        } else if (world.isAir(blockPos.up())) {
            int i = 1;
            while (world.getBlockState(blockPos.down(i)).getBlock() == this) {
                ++i;
            }
            if (i < 3) {
                int j = blockState.get(AGE);
                if (j == 15) {
                    world.setBlockState(blockPos.up(), this.getDefaultState());
                    world.setBlockState(blockPos, (BlockState)blockState.with(AGE, 0), 4);
                } else {
                    world.setBlockState(blockPos, (BlockState)blockState.with(AGE, j + 1), 4);
                }
            }
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        if (!blockState.canPlaceAt(iWorld, blockPos)) {
            iWorld.getBlockTickScheduler().schedule(blockPos, this, 1);
        }
        return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
    }

    @Override
    public boolean canPlaceAt(BlockState blockState, CollisionView collisionView, BlockPos blockPos) {
        Block block = collisionView.getBlockState(blockPos.down()).getBlock();
        if (block == this) {
            return true;
        }
        if (block == Blocks.GRASS_BLOCK || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.SAND || block == Blocks.RED_SAND) {
            BlockPos blockPos2 = blockPos.down();
            for (Direction direction : Direction.Type.HORIZONTAL) {
                BlockState blockState2 = collisionView.getBlockState(blockPos2.offset(direction));
                FluidState fluidState = collisionView.getFluidState(blockPos2.offset(direction));
                if (!fluidState.matches(FluidTags.WATER) && blockState2.getBlock() != Blocks.FROSTED_ICE) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public RenderLayer getRenderLayer() {
        return RenderLayer.CUTOUT;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
}

