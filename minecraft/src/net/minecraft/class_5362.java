package net.minecraft;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;

public interface class_5362 {
	Optional<Float> method_29555(Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, FluidState fluidState);

	boolean method_29554(Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, float f);
}
