/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.effect;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;

public enum StatusEffectType {
    BENEFICIAL(ChatFormat.BLUE),
    HARMFUL(ChatFormat.RED),
    NEUTRAL(ChatFormat.BLUE);

    private final ChatFormat formatting;

    private StatusEffectType(ChatFormat chatFormat) {
        this.formatting = chatFormat;
    }

    @Environment(value=EnvType.CLIENT)
    public ChatFormat getFormatting() {
        return this.formatting;
    }
}

