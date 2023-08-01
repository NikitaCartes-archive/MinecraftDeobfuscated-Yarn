package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;

class WitherStatusEffect extends StatusEffect {
	protected WitherStatusEffect(StatusEffectCategory statusEffectCategory, int i) {
		super(statusEffectCategory, i);
	}

	@Override
	public void applyUpdateEffect(LivingEntity entity, int amplifier) {
		super.applyUpdateEffect(entity, amplifier);
		entity.damage(entity.getDamageSources().wither(), 1.0F);
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		int i = 40 >> amplifier;
		return i > 0 ? duration % i == 0 : true;
	}
}
