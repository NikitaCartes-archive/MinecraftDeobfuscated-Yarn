/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class DamageModifierStatusEffect
extends StatusEffect {
    protected final double modifier;

    protected DamageModifierStatusEffect(StatusEffectType statusEffectType, int color, double d) {
        super(statusEffectType, color);
        this.modifier = d;
    }

    @Override
    public double method_5563(int i, EntityAttributeModifier entityAttributeModifier) {
        return this.modifier * (double)(i + 1);
    }
}

