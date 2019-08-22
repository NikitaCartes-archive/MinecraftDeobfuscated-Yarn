package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;

public class HealthBoostStatusEffect extends StatusEffect {
	public HealthBoostStatusEffect(StatusEffectType statusEffectType, int i) {
		super(statusEffectType, i);
	}

	@Override
	public void onRemoved(LivingEntity livingEntity, AbstractEntityAttributeContainer abstractEntityAttributeContainer, int i) {
		super.onRemoved(livingEntity, abstractEntityAttributeContainer, i);
		if (livingEntity.getHealth() > livingEntity.getMaximumHealth()) {
			livingEntity.setHealth(livingEntity.getMaximumHealth());
		}
	}
}
