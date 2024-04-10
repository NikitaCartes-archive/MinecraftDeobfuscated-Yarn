package net.minecraft.world.explosion;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

public class ExplosionBehavior {
	public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
		return blockState.isAir() && fluidState.isEmpty()
			? Optional.empty()
			: Optional.of(Math.max(blockState.getBlock().getBlastResistance(), fluidState.getBlastResistance()));
	}

	public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
		return true;
	}

	public boolean shouldDamage(Explosion explosion, Entity entity) {
		return true;
	}

	public float getKnockbackModifier(Entity entity) {
		return 1.0F;
	}

	public float calculateDamage(Explosion explosion, Entity entity) {
		float f = explosion.getPower() * 2.0F;
		Vec3d vec3d = explosion.getPosition();
		double d = Math.sqrt(entity.squaredDistanceTo(vec3d)) / (double)f;
		double e = (1.0 - d) * (double)Explosion.getExposure(vec3d, entity);
		return (float)((e * e + e) / 2.0 * 7.0 * (double)f + 1.0);
	}
}
