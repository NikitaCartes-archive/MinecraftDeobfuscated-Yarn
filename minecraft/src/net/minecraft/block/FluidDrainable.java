package net.minecraft.block;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public interface FluidDrainable {
	Fluid tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state);
}
