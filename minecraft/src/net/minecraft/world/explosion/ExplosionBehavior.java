package net.minecraft.world.explosion;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public interface ExplosionBehavior {
	Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState);

	boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power);
}
