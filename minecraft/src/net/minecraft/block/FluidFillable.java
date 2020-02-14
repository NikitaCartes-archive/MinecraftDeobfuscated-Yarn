package net.minecraft.block;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public interface FluidFillable {
	boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid);

	boolean tryFillWithFluid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState);
}
