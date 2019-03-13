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
	default boolean method_10310(BlockView blockView, BlockPos blockPos, BlockState blockState, Fluid fluid) {
		return !(Boolean)blockState.method_11654(Properties.field_12508) && fluid == Fluids.WATER;
	}

	@Override
	default boolean method_10311(IWorld iWorld, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
		if (!(Boolean)blockState.method_11654(Properties.field_12508) && fluidState.getFluid() == Fluids.WATER) {
			if (!iWorld.isClient()) {
				iWorld.method_8652(blockPos, blockState.method_11657(Properties.field_12508, Boolean.valueOf(true)), 3);
				iWorld.method_8405().method_8676(blockPos, fluidState.getFluid(), fluidState.getFluid().getTickRate(iWorld));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	default Fluid method_9700(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		if ((Boolean)blockState.method_11654(Properties.field_12508)) {
			iWorld.method_8652(blockPos, blockState.method_11657(Properties.field_12508, Boolean.valueOf(false)), 3);
			return Fluids.WATER;
		} else {
			return Fluids.EMPTY;
		}
	}
}
