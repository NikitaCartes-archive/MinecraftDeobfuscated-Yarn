/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import net.minecraft.ChatFormat;

public enum Rarity {
    COMMON(ChatFormat.WHITE),
    UNCOMMON(ChatFormat.YELLOW),
    RARE(ChatFormat.AQUA),
    EPIC(ChatFormat.LIGHT_PURPLE);

    public final ChatFormat formatting;

    private Rarity(ChatFormat chatFormat) {
        this.formatting = chatFormat;
    }
}

