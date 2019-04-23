/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class InstantStatusEffect
extends StatusEffect {
    public InstantStatusEffect(StatusEffectType statusEffectType, int i) {
        super(statusEffectType, i);
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int i, int j) {
        return i >= 1;
    }
}

