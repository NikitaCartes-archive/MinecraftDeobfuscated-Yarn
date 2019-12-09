/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import net.minecraft.util.math.MathHelper;

public class DamageUtil {
    public static float getDamageLeft(float damage, float armor, float armorToughness) {
        float f = 2.0f + armorToughness / 4.0f;
        float g = MathHelper.clamp(armor - damage / f, armor * 0.2f, 20.0f);
        return damage * (1.0f - g / 25.0f);
    }

    public static float getInflictedDamage(float damageDealt, float protection) {
        float f = MathHelper.clamp(protection, 0.0f, 20.0f);
        return damageDealt * (1.0f - f / 25.0f);
    }
}

