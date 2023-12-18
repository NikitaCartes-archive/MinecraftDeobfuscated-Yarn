package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;

class WitherStatusEffect extends StatusEffect {
	protected WitherStatusEffect(StatusEffectCategory statusEffectCategory, int i) {
		super(statusEffectCategory, i);
	}

	@Override
	public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
		entity.damage(entity.getDamageSources().wither(), 1.0F);
		return true;
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		int i = 40 >> amplifier;
		return i > 0 ? duration % i == 0 : true;
	}
}
