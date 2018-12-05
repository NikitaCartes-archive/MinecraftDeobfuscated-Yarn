package net.minecraft.block;

import net.minecraft.class_2263;
import net.minecraft.class_2402;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public interface Waterloggable extends class_2263, class_2402 {
	@Override
	default boolean method_10310(BlockView blockView, BlockPos blockPos, BlockState blockState, Fluid fluid) {
		return !(Boolean)blockState.get(Properties.WATERLOGGED) && fluid == Fluids.WATER;
	}

	@Override
	default boolean method_10311(IWorld iWorld, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
		if (!(Boolean)blockState.get(Properties.WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
			if (!iWorld.isRemote()) {
				iWorld.setBlockState(blockPos, blockState.with(Properties.WATERLOGGED, Boolean.valueOf(true)), 3);
				iWorld.getFluidTickScheduler().schedule(blockPos, fluidState.getFluid(), fluidState.getFluid().method_15789(iWorld));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	default Fluid method_9700(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		if ((Boolean)blockState.get(Properties.WATERLOGGED)) {
			iWorld.setBlockState(blockPos, blockState.with(Properties.WATERLOGGED, Boolean.valueOf(false)), 3);
			return Fluids.WATER;
		} else {
			return Fluids.field_15906;
		}
	}
}
