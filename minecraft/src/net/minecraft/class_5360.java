package net.minecraft;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;

public enum class_5360 implements class_5362 {
	field_25397;

	@Override
	public Optional<Float> method_29555(Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
		return blockState.isAir() && fluidState.isEmpty()
			? Optional.empty()
			: Optional.of(Math.max(blockState.getBlock().getBlastResistance(), fluidState.getBlastResistance()));
	}

	@Override
	public boolean method_29554(Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, float f) {
		return true;
	}
}
