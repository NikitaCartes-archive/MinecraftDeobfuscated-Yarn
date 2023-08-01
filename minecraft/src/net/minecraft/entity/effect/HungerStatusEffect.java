package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

class HungerStatusEffect extends StatusEffect {
	protected HungerStatusEffect(StatusEffectCategory statusEffectCategory, int i) {
		super(statusEffectCategory, i);
	}

	@Override
	public void applyUpdateEffect(LivingEntity entity, int amplifier) {
		super.applyUpdateEffect(entity, amplifier);
		if (entity instanceof PlayerEntity playerEntity) {
			playerEntity.addExhaustion(0.005F * (float)(amplifier + 1));
		}
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}
}
