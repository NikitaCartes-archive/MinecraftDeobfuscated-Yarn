package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

class SaturationStatusEffect extends InstantStatusEffect {
	protected SaturationStatusEffect(StatusEffectCategory statusEffectCategory, int i) {
		super(statusEffectCategory, i);
	}

	@Override
	public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
		if (entity instanceof PlayerEntity playerEntity) {
			playerEntity.getHungerManager().add(amplifier + 1, 1.0F);
		}

		return true;
	}
}
