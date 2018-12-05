package net.minecraft;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public interface class_2402 {
	boolean method_10310(BlockView blockView, BlockPos blockPos, BlockState blockState, Fluid fluid);

	boolean method_10311(IWorld iWorld, BlockPos blockPos, BlockState blockState, FluidState fluidState);
}
