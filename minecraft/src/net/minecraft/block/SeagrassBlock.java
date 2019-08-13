package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class SeagrassBlock extends PlantBlock implements Fertilizable, FluidFillable {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);

	protected SeagrassBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return SHAPE;
	}

	@Override
	protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.isSideSolidFullSquare(blockView, blockPos, Direction.field_11036) && blockState.getBlock() != Blocks.field_10092;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getBlockPos());
		return fluidState.matches(FluidTags.field_15517) && fluidState.getLevel() == 8 ? super.getPlacementState(itemPlacementContext) : null;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		BlockState blockState3 = super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		if (!blockState3.isAir()) {
			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		return blockState3;
	}

	@Override
	public boolean isFertilizable(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		return true;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return true;
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return Fluids.WATER.getStill(false);
	}

	@Override
	public void grow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		BlockState blockState2 = Blocks.field_10238.getDefaultState();
		BlockState blockState3 = blockState2.with(TallSeagrassBlock.HALF, DoubleBlockHalf.field_12609);
		BlockPos blockPos2 = blockPos.up();
		if (world.getBlockState(blockPos2).getBlock() == Blocks.field_10382) {
			world.setBlockState(blockPos, blockState2, 2);
			world.setBlockState(blockPos2, blockState3, 2);
		}
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
