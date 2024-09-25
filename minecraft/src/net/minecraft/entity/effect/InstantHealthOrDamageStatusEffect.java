package net.minecraft.entity.effect;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

class InstantHealthOrDamageStatusEffect extends InstantStatusEffect {
	private final boolean damage;

	public InstantHealthOrDamageStatusEffect(StatusEffectCategory category, int color, boolean damage) {
		super(category, color);
		this.damage = damage;
	}

	@Override
	public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
		if (this.damage == entity.hasInvertedHealingAndHarm()) {
			entity.heal((float)Math.max(4 << amplifier, 0));
		} else {
			entity.damage(world, entity.getDamageSources().magic(), (float)(6 << amplifier));
		}

		return true;
	}

	@Override
	public void applyInstantEffect(
		ServerWorld world, @Nullable Entity effectEntity, @Nullable Entity attacker, LivingEntity target, int amplifier, double proximity
	) {
		if (this.damage == target.hasInvertedHealingAndHarm()) {
			int i = (int)(proximity * (double)(4 << amplifier) + 0.5);
			target.heal((float)i);
		} else {
			int i = (int)(proximity * (double)(6 << amplifier) + 0.5);
			if (effectEntity == null) {
				target.damage(world, target.getDamageSources().magic(), (float)i);
			} else {
				target.damage(world, target.getDamageSources().indirectMagic(effectEntity, attacker), (float)i);
			}
		}
	}
}
