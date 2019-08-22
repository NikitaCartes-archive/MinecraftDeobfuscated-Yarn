/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class AbsorptionStatusEffect
extends StatusEffect {
    protected AbsorptionStatusEffect(StatusEffectType statusEffectType, int i) {
        super(statusEffectType, i);
    }

    @Override
    public void onRemoved(LivingEntity livingEntity, AbstractEntityAttributeContainer abstractEntityAttributeContainer, int i) {
        livingEntity.setAbsorptionAmount(livingEntity.getAbsorptionAmount() - (float)(4 * (i + 1)));
        super.onRemoved(livingEntity, abstractEntityAttributeContainer, i);
    }

    @Override
    public void onApplied(LivingEntity livingEntity, AbstractEntityAttributeContainer abstractEntityAttributeContainer, int i) {
        livingEntity.setAbsorptionAmount(livingEntity.getAbsorptionAmount() + (float)(4 * (i + 1)));
        super.onApplied(livingEntity, abstractEntityAttributeContainer, i);
    }
}

