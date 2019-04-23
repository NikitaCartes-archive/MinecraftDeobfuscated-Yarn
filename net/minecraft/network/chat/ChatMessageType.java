/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.chat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum ChatMessageType {
    CHAT(0, false),
    SYSTEM(1, true),
    GAME_INFO(2, true);

    private final byte id;
    private final boolean interruptsNarration;

    private ChatMessageType(byte b, boolean bl) {
        this.id = b;
        this.interruptsNarration = bl;
    }

    public byte getId() {
        return this.id;
    }

    public static ChatMessageType byId(byte b) {
        for (ChatMessageType chatMessageType : ChatMessageType.values()) {
            if (b != chatMessageType.id) continue;
            return chatMessageType;
        }
        return CHAT;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean interruptsNarration() {
        return this.interruptsNarration;
    }
}

