package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;

class RegenerationStatusEffect extends StatusEffect {
	protected RegenerationStatusEffect(StatusEffectCategory statusEffectCategory, int i) {
		super(statusEffectCategory, i);
	}

	@Override
	public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
		if (entity.getHealth() < entity.getMaxHealth()) {
			entity.heal(1.0F);
		}

		return true;
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		int i = 50 >> amplifier;
		return i > 0 ? duration % i == 0 : true;
	}
}
