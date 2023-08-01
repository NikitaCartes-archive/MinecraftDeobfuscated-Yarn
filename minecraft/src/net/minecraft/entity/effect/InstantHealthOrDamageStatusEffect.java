package net.minecraft.entity.effect;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

class InstantHealthOrDamageStatusEffect extends InstantStatusEffect {
	private final boolean damage;

	public InstantHealthOrDamageStatusEffect(StatusEffectCategory category, int color, boolean damage) {
		super(category, color);
		this.damage = damage;
	}

	@Override
	public void applyUpdateEffect(LivingEntity entity, int amplifier) {
		super.applyUpdateEffect(entity, amplifier);
		if (this.damage == entity.isUndead()) {
			entity.heal((float)Math.max(4 << amplifier, 0));
		} else {
			entity.damage(entity.getDamageSources().magic(), (float)(6 << amplifier));
		}
	}

	@Override
	public void applyInstantEffect(@Nullable Entity source, @Nullable Entity attacker, LivingEntity target, int amplifier, double proximity) {
		if (this.damage == target.isUndead()) {
			int i = (int)(proximity * (double)(4 << amplifier) + 0.5);
			target.heal((float)i);
		} else {
			int i = (int)(proximity * (double)(6 << amplifier) + 0.5);
			if (source == null) {
				target.damage(target.getDamageSources().magic(), (float)i);
			} else {
				target.damage(target.getDamageSources().indirectMagic(source, attacker), (float)i);
			}
		}
	}
}
