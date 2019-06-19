package net.minecraft.block;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public interface Waterloggable extends FluidDrainable, FluidFillable {
	@Override
	default boolean canFillWithFluid(BlockView blockView, BlockPos blockPos, BlockState blockState, Fluid fluid) {
		return !(Boolean)blockState.get(Properties.WATERLOGGED) && fluid == Fluids.WATER;
	}

	@Override
	default boolean tryFillWithFluid(IWorld iWorld, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
		if (!(Boolean)blockState.get(Properties.WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
			if (!iWorld.isClient()) {
				iWorld.setBlockState(blockPos, blockState.with(Properties.WATERLOGGED, Boolean.valueOf(true)), 3);
				iWorld.getFluidTickScheduler().schedule(blockPos, fluidState.getFluid(), fluidState.getFluid().getTickRate(iWorld));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	default Fluid tryDrainFluid(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		if ((Boolean)blockState.get(Properties.WATERLOGGED)) {
			iWorld.setBlockState(blockPos, blockState.with(Properties.WATERLOGGED, Boolean.valueOf(false)), 3);
			return Fluids.WATER;
		} else {
			return Fluids.field_15906;
		}
	}
}
