/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class KelpBlock
extends Block
implements FluidFillable {
    public static final IntProperty AGE = Properties.AGE_25;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);

    protected KelpBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(AGE, 0));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        return SHAPE;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getBlockPos());
        if (fluidState.matches(FluidTags.WATER) && fluidState.getLevel() == 8) {
            return this.getPlacementState(itemPlacementContext.getWorld());
        }
        return null;
    }

    public BlockState getPlacementState(IWorld iWorld) {
        return (BlockState)this.getDefaultState().with(AGE, iWorld.getRandom().nextInt(25));
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        return Fluids.WATER.getStill(false);
    }

    @Override
    public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        if (!blockState.canPlaceAt(world, blockPos)) {
            world.breakBlock(blockPos, true);
            return;
        }
        BlockPos blockPos2 = blockPos.up();
        BlockState blockState2 = world.getBlockState(blockPos2);
        if (blockState2.getBlock() == Blocks.WATER && blockState.get(AGE) < 25 && random.nextDouble() < 0.14) {
            world.setBlockState(blockPos2, (BlockState)blockState.cycle(AGE));
        }
    }

    @Override
    public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.down();
        BlockState blockState2 = viewableWorld.getBlockState(blockPos2);
        Block block = blockState2.getBlock();
        if (block == Blocks.MAGMA_BLOCK) {
            return false;
        }
        return block == this || block == Blocks.KELP_PLANT || blockState2.method_20827(viewableWorld, blockPos2, Direction.UP);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        if (!blockState.canPlaceAt(iWorld, blockPos)) {
            if (direction == Direction.DOWN) {
                return Blocks.AIR.getDefaultState();
            }
            iWorld.getBlockTickScheduler().schedule(blockPos, this, 1);
        }
        if (direction == Direction.UP && blockState2.getBlock() == this) {
            return Blocks.KELP_PLANT.getDefaultState();
        }
        iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
        return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public boolean canFillWithFluid(BlockView blockView, BlockPos blockPos, BlockState blockState, Fluid fluid) {
        return false;
    }

    @Override
    public boolean tryFillWithFluid(IWorld iWorld, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
        return false;
    }
}

