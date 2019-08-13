package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;

public class HealthBoostStatusEffect extends StatusEffect {
	public HealthBoostStatusEffect(StatusEffectType statusEffectType, int i) {
		super(statusEffectType, i);
	}

	@Override
	public void method_5562(LivingEntity livingEntity, AbstractEntityAttributeContainer abstractEntityAttributeContainer, int i) {
		super.method_5562(livingEntity, abstractEntityAttributeContainer, i);
		if (livingEntity.getHealth() > livingEntity.getHealthMaximum()) {
			livingEntity.setHealth(livingEntity.getHealthMaximum());
		}
	}
}
