package net.minecraft.block;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public interface FluidDrainable {
	Fluid tryDrainFluid(IWorld iWorld, BlockPos blockPos, BlockState blockState);
}
