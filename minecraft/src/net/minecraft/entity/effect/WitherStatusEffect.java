package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

class WitherStatusEffect extends StatusEffect {
	protected WitherStatusEffect(StatusEffectCategory statusEffectCategory, int i) {
		super(statusEffectCategory, i);
	}

	@Override
	public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
		entity.damage(world, entity.getDamageSources().wither(), 1.0F);
		return true;
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		int i = 40 >> amplifier;
		return i > 0 ? duration % i == 0 : true;
	}
}
