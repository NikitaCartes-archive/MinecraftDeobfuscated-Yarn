/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;

public enum AdvancementFrame {
    TASK("task", 0, ChatFormat.GREEN),
    CHALLENGE("challenge", 26, ChatFormat.DARK_PURPLE),
    GOAL("goal", 52, ChatFormat.GREEN);

    private final String id;
    private final int texV;
    private final ChatFormat titleFormat;

    private AdvancementFrame(String string2, int j, ChatFormat chatFormat) {
        this.id = string2;
        this.texV = j;
        this.titleFormat = chatFormat;
    }

    public String getId() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public int texV() {
        return this.texV;
    }

    public static AdvancementFrame forName(String string) {
        for (AdvancementFrame advancementFrame : AdvancementFrame.values()) {
            if (!advancementFrame.id.equals(string)) continue;
            return advancementFrame;
        }
        throw new IllegalArgumentException("Unknown frame type '" + string + "'");
    }

    public ChatFormat getTitleFormat() {
        return this.titleFormat;
    }
}

