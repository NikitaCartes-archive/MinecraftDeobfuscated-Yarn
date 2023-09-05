package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;

class AbsorptionStatusEffect extends StatusEffect {
	protected AbsorptionStatusEffect(StatusEffectCategory statusEffectCategory, int i) {
		super(statusEffectCategory, i);
	}

	@Override
	public void applyUpdateEffect(LivingEntity entity, int amplifier) {
		super.applyUpdateEffect(entity, amplifier);
		if (entity.getAbsorptionAmount() <= 0.0F && !entity.getWorld().isClient) {
			entity.removeStatusEffect(this);
		}
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}

	@Override
	public void onApplied(LivingEntity entity, int amplifier) {
		super.onApplied(entity, amplifier);
		entity.setAbsorptionAmount(Math.max(entity.getAbsorptionAmount(), (float)(4 * (1 + amplifier))));
	}
}
