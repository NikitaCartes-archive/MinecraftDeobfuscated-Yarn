package net.minecraft;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;

public class class_5361 implements class_5362 {
	private final Entity field_25399;

	public class_5361(Entity entity) {
		this.field_25399 = entity;
	}

	@Override
	public Optional<Float> method_29555(Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
		return class_5360.field_25397
			.method_29555(explosion, blockView, blockPos, blockState, fluidState)
			.map(float_ -> this.field_25399.getEffectiveExplosionResistance(explosion, blockView, blockPos, blockState, fluidState, float_));
	}

	@Override
	public boolean method_29554(Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, float f) {
		return this.field_25399.canExplosionDestroyBlock(explosion, blockView, blockPos, blockState, f);
	}
}
