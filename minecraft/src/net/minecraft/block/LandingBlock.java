package net.minecraft.block;

import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface LandingBlock {
	default void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity fallingBlockEntity) {
	}

	default void onDestroyedOnLanding(World world, BlockPos pos, FallingBlockEntity fallingBlockEntity) {
	}

	default DamageSource getDamageSource() {
		return DamageSource.FALLING_BLOCK;
	}

	default Predicate<Entity> getEntityPredicate() {
		return EntityPredicates.EXCEPT_SPECTATOR;
	}
}
