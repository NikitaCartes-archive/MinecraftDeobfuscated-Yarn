package net.minecraft.block;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public interface FluidFillable {
	boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid);

	boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState);
}
