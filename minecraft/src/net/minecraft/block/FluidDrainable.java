package net.minecraft.block;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public interface FluidDrainable {
	Fluid method_9700(IWorld iWorld, BlockPos blockPos, BlockState blockState);
}
