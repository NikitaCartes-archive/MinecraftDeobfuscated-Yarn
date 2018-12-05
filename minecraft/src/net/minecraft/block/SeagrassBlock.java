package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_2402;
import net.minecraft.block.enums.BlockHalf;
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

public class SeagrassBlock extends PlantBlock implements Fertilizable, class_2402 {
	protected static final VoxelShape field_11485 = Block.createCubeShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);

	protected SeagrassBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return field_11485;
	}

	@Override
	protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return Block.method_9501(blockState.method_11628(blockView, blockPos), Direction.UP) && blockState.getBlock() != Blocks.field_10092;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getPos());
		return fluidState.matches(FluidTags.field_15517) && fluidState.method_15761() == 8 ? super.getPlacementState(itemPlacementContext) : null;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		BlockState blockState3 = super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		if (!blockState3.isAir()) {
			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.method_15789(iWorld));
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
		return Fluids.WATER.getState(false);
	}

	@Override
	public void grow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		BlockState blockState2 = Blocks.field_10238.getDefaultState();
		BlockState blockState3 = blockState2.with(TallSeagrassBlock.PROPERTY_HALF, BlockHalf.field_12609);
		BlockPos blockPos2 = blockPos.up();
		if (world.getBlockState(blockPos2).getBlock() == Blocks.field_10382) {
			world.setBlockState(blockPos, blockState2, 2);
			world.setBlockState(blockPos2, blockState3, 2);
		}
	}

	@Override
	public boolean method_10310(BlockView blockView, BlockPos blockPos, BlockState blockState, Fluid fluid) {
		return false;
	}

	@Override
	public boolean method_10311(IWorld iWorld, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
		return false;
	}
}
