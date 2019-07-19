/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class HealthBoostStatusEffect
extends StatusEffect {
    public HealthBoostStatusEffect(StatusEffectType statusEffectType, int i) {
        super(statusEffectType, i);
    }

    @Override
    public void method_5562(LivingEntity livingEntity, AbstractEntityAttributeContainer abstractEntityAttributeContainer, int i) {
        super.method_5562(livingEntity, abstractEntityAttributeContainer, i);
        if (livingEntity.getHealth() > livingEntity.getMaximumHealth()) {
            livingEntity.setHealth(livingEntity.getMaximumHealth());
        }
    }
}

