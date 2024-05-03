package net.minecraft.world.explosion;

import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class AdvancedExplosionBehavior extends ExplosionBehavior {
	private final boolean destroyBlocks;
	private final boolean damageEntities;
	private final Optional<Float> knockbackModifier;
	private final Optional<RegistryEntryList<Block>> immuneBlocks;

	public AdvancedExplosionBehavior(
		boolean destroyBlocks, boolean damageEntities, Optional<Float> knockbackModifier, Optional<RegistryEntryList<Block>> immuneBlocks
	) {
		this.destroyBlocks = destroyBlocks;
		this.damageEntities = damageEntities;
		this.knockbackModifier = knockbackModifier;
		this.immuneBlocks = immuneBlocks;
	}

	@Override
	public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
		if (this.immuneBlocks.isPresent()) {
			return blockState.isIn((RegistryEntryList<Block>)this.immuneBlocks.get()) ? Optional.of(3600000.0F) : Optional.empty();
		} else {
			return super.getBlastResistance(explosion, world, pos, blockState, fluidState);
		}
	}

	@Override
	public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
		return this.destroyBlocks;
	}

	@Override
	public boolean shouldDamage(Explosion explosion, Entity entity) {
		return this.damageEntities;
	}

	@Override
	public float getKnockbackModifier(Entity entity) {
		boolean var10000;
		label17: {
			if (entity instanceof PlayerEntity playerEntity && playerEntity.getAbilities().flying) {
				var10000 = true;
				break label17;
			}

			var10000 = false;
		}

		boolean bl = var10000;
		return bl ? 0.0F : (Float)this.knockbackModifier.orElseGet(() -> super.getKnockbackModifier(entity));
	}
}
