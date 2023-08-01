package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;

class RegenerationStatusEffect extends StatusEffect {
	protected RegenerationStatusEffect(StatusEffectCategory statusEffectCategory, int i) {
		super(statusEffectCategory, i);
	}

	@Override
	public void applyUpdateEffect(LivingEntity entity, int amplifier) {
		super.applyUpdateEffect(entity, amplifier);
		if (entity.getHealth() < entity.getMaxHealth()) {
			entity.heal(1.0F);
		}
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		int i = 50 >> amplifier;
		return i > 0 ? duration % i == 0 : true;
	}
}
