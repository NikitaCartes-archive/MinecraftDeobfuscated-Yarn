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
    public void onRemoved(LivingEntity entity, AbstractEntityAttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);
        if (entity.getHealth() > entity.getMaximumHealth()) {
            entity.setHealth(entity.getMaximumHealth());
        }
    }
}

