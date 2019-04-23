/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import net.minecraft.util.math.MathHelper;

public class DamageUtil {
    public static float getDamageLeft(float f, float g, float h) {
        float i = 2.0f + h / 4.0f;
        float j = MathHelper.clamp(g - f / i, g * 0.2f, 20.0f);
        return f * (1.0f - j / 25.0f);
    }

    public static float getInflictedDamage(float f, float g) {
        float h = MathHelper.clamp(g, 0.0f, 20.0f);
        return f * (1.0f - h / 25.0f);
    }
}

