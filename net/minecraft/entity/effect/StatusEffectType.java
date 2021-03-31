/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.effect;

import net.minecraft.util.Formatting;

public enum StatusEffectType {
    BENEFICIAL(Formatting.BLUE),
    HARMFUL(Formatting.RED),
    NEUTRAL(Formatting.BLUE);

    private final Formatting formatting;

    private StatusEffectType(Formatting format) {
        this.formatting = format;
    }

    public Formatting getFormatting() {
        return this.formatting;
    }
}

