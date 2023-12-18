package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

class SaturationStatusEffect extends InstantStatusEffect {
	protected SaturationStatusEffect(StatusEffectCategory statusEffectCategory, int i) {
		super(statusEffectCategory, i);
	}

	@Override
	public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
		if (!entity.getWorld().isClient && entity instanceof PlayerEntity playerEntity) {
			playerEntity.getHungerManager().add(amplifier + 1, 1.0F);
		}

		return true;
	}
}
