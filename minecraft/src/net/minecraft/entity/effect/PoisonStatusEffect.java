package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;

class PoisonStatusEffect extends StatusEffect {
	protected PoisonStatusEffect(StatusEffectCategory statusEffectCategory, int i) {
		super(statusEffectCategory, i);
	}

	@Override
	public void applyUpdateEffect(LivingEntity entity, int amplifier) {
		super.applyUpdateEffect(entity, amplifier);
		if (entity.getHealth() > 1.0F) {
			entity.damage(entity.getDamageSources().magic(), 1.0F);
		}
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		int i = 25 >> amplifier;
		if (i > 0) {
			return duration % i == 0;
		} else {
			return true;
		}
	}
}
